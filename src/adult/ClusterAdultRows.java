package adult;

import static utils.Configuration.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClusterAdultRows {
	
	public List<AdultDataRowCluster> adultDataRowClusters;
	public ArrayList<AdultDataRow> adultDataRows;
	private ArrayList<AdultDataRow> backupQueue;
	
	private static final int K_FOR_ANONYMITY = 10;
	private static final int L_FOR_DIVERSITY = 5;
	private static final double THETA_FOR_FREQUENCY = 0.5;
	
	
	public ClusterAdultRows(Collection<AdultDataRow> adultDataRows) {
		adultDataRowClusters = new ArrayList<AdultDataRowCluster>();
		this.adultDataRows = new ArrayList<AdultDataRow>();
		this.adultDataRows.addAll(adultDataRows);	
		
		constructClustersInit();
	}
	
	private void constructClustersInit() {
		backupQueue = new ArrayList<AdultDataRow>();
		while(constructClusters()) {
			
		}
		// Now empty the backup queue
		emptyBackupQueue();
		
		// Now merge clusters together to satisfy k, l, theta, if possible.
		mergeClusters();
	}
	
	private void mergeClusters() {
		int numUnmergeable = 0;
		List<AdultDataRowCluster> clustersToMerge = new ArrayList<AdultDataRowCluster>();
		for(AdultDataRowCluster cluster : adultDataRowClusters) {
			clustersToMerge.add(cluster);
		}
		
		for(int i = 0; i < clustersToMerge.size(); i++) {
			AdultDataRowCluster clusterToMerge = clustersToMerge.get(i);
			if(clusterToMerge == null || clusterToMerge.rows.size()==0) {
				// This cluster has been merged already.
				continue;
			}
			if(clusterToMerge.rows.size() >= K_FOR_ANONYMITY &&
					clusterToMerge.getMaxSensitiveValueFrequency() <= THETA_FOR_FREQUENCY &&
					clusterToMerge.numDistinctSensitiveValues() >= L_FOR_DIVERSITY) {
				// Cluster is satisfied, don't merge.
				continue;
			}
			double bestSatisfiedMergeDistance = Double.MAX_VALUE;
			AdultDataRowCluster bestSatisfiedMergeCluster = null;
			double bestUnsatisfiedMergeDistance = Double.MAX_VALUE;
			AdultDataRowCluster bestUnsatisfiedMergeCluster = null;
			for(int j = 0; j < adultDataRowClusters.size(); j++) {
				AdultDataRowCluster otherCluster = adultDataRowClusters.get(j);
				if(otherCluster == null || otherCluster.rows.isEmpty() || clusterToMerge.equals(otherCluster)) {
					continue;
				}
				int[] results = clusterToMerge.getMaxAndTotalSensitiveValues(otherCluster);
				
				
				boolean unsatisfied = false;
				boolean thetaUnsatisfied = false;
				int newSize = clusterToMerge.rows.size() + otherCluster.rows.size();
				if(newSize < K_FOR_ANONYMITY) {
					unsatisfied = true;
				}
				if((0.0+results[0]) / newSize  > THETA_FOR_FREQUENCY) {
					thetaUnsatisfied = true;
				}
				if(results[1] < L_FOR_DIVERSITY) {
					unsatisfied = true;
				}
				if(thetaUnsatisfied) {
					// Don't merge clusters that mess up THETA requirement
					continue;
				}
				double newDistance = distance(clusterToMerge, otherCluster);
				if(unsatisfied) {
					// unsatisfied
					if(newDistance < bestUnsatisfiedMergeDistance) {
						bestUnsatisfiedMergeDistance = newDistance;
						bestUnsatisfiedMergeCluster = otherCluster;
					}
				} else {
					// satisfied
					if(newDistance < bestSatisfiedMergeDistance) {
						bestSatisfiedMergeDistance = newDistance;
						bestSatisfiedMergeCluster = otherCluster;
					}
				}
			}
			if(bestSatisfiedMergeCluster != null) {
				clusterToMerge.merge(bestSatisfiedMergeCluster);
				bestSatisfiedMergeCluster.rows.clear();
				adultDataRowClusters.remove(bestSatisfiedMergeCluster);
			} else if(bestUnsatisfiedMergeCluster != null) {
				clusterToMerge.merge(bestUnsatisfiedMergeCluster);
				bestUnsatisfiedMergeCluster.rows.clear();
				adultDataRowClusters.remove(bestUnsatisfiedMergeCluster);	
			} else {
				numUnmergeable++;
			}
		}
		System.out.println("Unmergeable clusters: " + numUnmergeable);
		reportOnClusters();
	}

	// Returns true if there's more to allocate.
	private boolean constructClusters() {
		if(adultDataRowClusters.size() % 100 == 0) {
			System.out.println("Num  Clusters: " + adultDataRowClusters.size());
		}
		
		// Pick arbitrary new cluster center
		AdultDataRow newCenter = adultDataRows.get(0);
		adultDataRows.remove(newCenter);
		AdultDataRowCluster newCluster = new AdultDataRowCluster(newCenter);
		adultDataRowClusters.add(newCluster);
		
		// Fill this cluster
		while(newCluster.numDistinctSensitiveValues() < L_FOR_DIVERSITY) {
			AdultDataRow closestPoint = findPointToAddToCluster(newCluster, adultDataRows);
			if(closestPoint == null) {
				System.out.println("Done with initial clusters. No new points to add.");
				break;
			}
			
			if(newCluster.numDistinctSensitiveValues() < L_FOR_DIVERSITY && !newCluster.containsSensitiveValueOfRow(closestPoint)) {
				// New cluster needs more diversity and this point helps.
				newCluster.merge(closestPoint);
				adultDataRows.remove(closestPoint);
			} else {
				// This cluster doesn't need more diversity or this point doesn't help. Put in backup queue.
				backupQueue.add(closestPoint);
				adultDataRows.remove(closestPoint);
			}
		}
		if(adultDataRows.size() > 0) {
			// More rows to place in clusters or backup queue.
			return true;
		}
		
		System.out.println("DONE: need to place " + backupQueue.size() + " points from backup queue");
		reportOnClusters();
		return false;
	}
	
	private void emptyBackupQueue() {
		int unplaced = 0;
		while(!backupQueue.isEmpty()) {
			AdultDataRow newBackupPoint = backupQueue.remove(backupQueue.size()-1);
			double minDistance = Double.MAX_VALUE;
			AdultDataRowCluster closestEligibleCluster = null;
			for(AdultDataRowCluster cluster : adultDataRowClusters) {
				if(cluster.rows.size() >= K_FOR_ANONYMITY && 
						cluster.getMaxSensitiveValueFrequency() < THETA_FOR_FREQUENCY) {
					// This cluster is done.
					continue;
				} else if(cluster.rows.size() < K_FOR_ANONYMITY && 
						(!cluster.containsSensitiveValueOfRow(newBackupPoint) ||
								cluster.getMaxSensitiveValueFrequencyWithNewPoint(newBackupPoint) < THETA_FOR_FREQUENCY)
						) {
					// This point can be added to the cluster without breaking THETA
					// And this cluster needs more points
					// So this cluster is eligible to receive this new point.
					double newDistance = distance(cluster, newBackupPoint);
					if(newDistance < minDistance) {
						minDistance = newDistance;
						closestEligibleCluster = cluster;
					}
				}
			}
			if(closestEligibleCluster == null) {
				// TODO: Create new cluster with this row
				unplaced++;
				adultDataRows.add(newBackupPoint);
			} else {
				closestEligibleCluster.merge(newBackupPoint);
			}
		}
		if(unplaced > 0) {
			System.out.println("DONE: failed to place " + unplaced + " points from backup queue");;
			
			while(constructClusters()) {
				
			}
			emptyBackupQueue();
			return;
		}
		System.out.println("DONE: failed to place " + unplaced + " points from backup queue");
		reportOnClusters();
		
	}
	
	private void reportOnClusters() {
		int satisfied = 0;
		int unsatisfiedK = 0;
		int unsatisfiedL = 0;
		int unsatisfiedTheta = 0;
		int unsatisfiedTotal = 0;
		int maxSize = 0;
		int totalDataRows = 0;
		for(AdultDataRowCluster cluster : adultDataRowClusters) {
			maxSize = Math.max(cluster.rows.size(), maxSize);
			totalDataRows += cluster.rows.size();
			boolean unsatisfied = false;
			if(cluster.rows.size() < K_FOR_ANONYMITY) {
				unsatisfiedK++;
				unsatisfied = true;
			}
			if(cluster.getMaxSensitiveValueFrequency() > THETA_FOR_FREQUENCY) {
				unsatisfiedTheta++;
				unsatisfied = true;
			}
			if(cluster.numDistinctSensitiveValues() < L_FOR_DIVERSITY) {
				unsatisfiedL++;
				unsatisfied = true;
			}
			if(unsatisfied) {
				unsatisfiedTotal++;
			} else {
				satisfied++;
			}
		}
		System.out.println("Total rows: " + totalDataRows);
		System.out.println("Satsified: " + satisfied);
		System.out.println("Unsatsified: " + unsatisfiedTotal);
		System.out.println("Unsatsified K: " + unsatisfiedK);
		System.out.println("Unsatsified L: " + unsatisfiedL);
		System.out.println("Unsatsified Theta: " + unsatisfiedTheta);
		int[] sizeHisto = new int[maxSize+1];
		for(AdultDataRowCluster cluster : adultDataRowClusters) {
			sizeHisto[cluster.rows.size()]++;
		}
		System.out.println("Sizes");
		for(Integer size : sizeHisto) {
			System.out.print(size +",");
		}
		System.out.println("");
	}

	private AdultDataRow findPointToAddToCluster(AdultDataRowCluster newCluster, List<AdultDataRow> poolOfPoints) {
		double minDistance = Double.MAX_VALUE;
		AdultDataRow closestPoint = null;
		for(int i = 0; i < poolOfPoints.size(); i++) {
			double distance = distance(newCluster, poolOfPoints.get(i));
			if(distance < minDistance) {
				minDistance = distance;
				closestPoint = poolOfPoints.get(i);
			}
		}
		return closestPoint;
	}
	
	private double distance(AdultDataRowCluster cluster, AdultDataRow row) {
		Set<AdultDataRow> individualSet = new HashSet<AdultDataRow>();
		individualSet.add(row);
		return distance(cluster, new AdultDataRowCluster(individualSet));
	}

	private double distance(AdultDataRowCluster cluster1, AdultDataRowCluster cluster2) {
		// Weighted distance between two groups with univariate distance metric:
		// [n1*n2/(n1+n2)]*(average1-average2)^2
		// From: Domingo-Ferrer, Josep, and Josep Maria Mateo-Sanz. "Practical data-oriented microaggregation for statistical disclosure control." IEEE Transactions on Knowledge and data Engineering 14.1 (2002): 189-201.
		
		int totalSize = cluster1.rows.size()+cluster2.rows.size();
		
		double totalAgeDistance = 1.0*cluster1.rows.size()*cluster2.rows.size()/(0.0+totalSize);
		double averageAgeDifference = averageAge(cluster1)-averageAge(cluster2);
		totalAgeDistance *= averageAgeDifference*averageAgeDifference;
		
		double totalSexDistance = 1.0*cluster1.rows.size()*cluster2.rows.size()/(0.0+totalSize);
		double averageSexDifference = averageSex(cluster1)-averageSex(cluster2);
		totalSexDistance *= averageSexDifference*averageSexDifference;
		
		double totalDistance = totalAgeDistance + totalSexDistance; //+ (1-dotProduct);
		
		
		for(int i = 0; i < QI_COLUMNS.length; i++) {
			boolean newKey = false;
			for(String key : cluster1.QISets.get(i)) {
				if(!cluster2.QISets.get(i).contains(key)) {
					newKey = true;
					break;
				}
			}
			if(!newKey) {
				for(String key : cluster2.QISets.get(i)) {
					if(!cluster1.QISets.get(i).contains(key)) {
						newKey = true;
						break;
					}
				}
			}
			totalDistance += newKey ? QI_DISTANCE_WEIGHTS[i] : 0;
		}
		
		return totalDistance;
	}
	
	private double averageAge(AdultDataRowCluster cluster) {
		return cluster.averageAge();
	}
	
	private double averageSex(AdultDataRowCluster cluster) {
		return cluster.averageSex();
	}
}
