package adult;

import static utils.Configuration.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClusterAdultRows {
	
	private Map<AdultDataRowClusterPair, Double> distanceCache;
	public List<AdultDataRowCluster> adultDataRowClusters;
	public ArrayList<AdultDataRow> adultDataRows;
	private ArrayList<AdultDataRow> backupQueue;
	public boolean use_k_means = true;
	
	private static final int K_FOR_ANONYMITY = 10;
	private static final int L_FOR_DIVERSITY = 5;
	private static final double THETA_FOR_FREQUENCY = 0.5;
	
	private static final boolean NO_K_MEANS = false;
	private static final boolean USE_K_MEANS = true;
	
	
	int cacheHits = 0;
	
	public ClusterAdultRows(Collection<AdultDataRow> adultDataRows, boolean k_means) {
		
//		distanceCache = new HashMap<AdultDataRowCollectionPair, Double>(adultDataRows.size()*adultDataRows.size());
		adultDataRowClusters = new ArrayList<AdultDataRowCluster>();
		distanceCache = new HashMap<AdultDataRowClusterPair, Double>();
		this.adultDataRows = new ArrayList<AdultDataRow>();
		this.adultDataRows.addAll(adultDataRows);	
//		if(k_means) {
	
//		if(adultDataRows.size() > 50) {
//			kMeansCluster();
//		} else {
//			initWardCluster(); // Do not call if already done kMeansCluster, or fix so this is less janky.
//		}
//		wardCluster();
		
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
				System.err.println("Done? No point is closest to new cluster");
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
			System.out.println("DONE: failed to place " + unplaced + " points from backup queue");
////			adultDataRows.addAll(backupQueue);
//			backupQueue.clear();
			
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
			if(cluster.rows.size() > 100) {
				System.out.println("Large cluster");
			}
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


	public ClusterAdultRows(Collection<AdultDataRow> adultDataRows) {
		this(adultDataRows, USE_K_MEANS);
	}
	
	
	private void kMeansCluster() {
//		int numClusters = 100;
		int numClusters = adultDataRows.size()/50;
		initKMeans(numClusters);
		
		int numSteps = 0;
//		while(updateKMeans()) {
		if(updateKMeans()) {
			numSteps++;
			if(numSteps%100 == 0) {
				System.out.println(numSteps + " cache: " + cacheHits);
			}
		}
		System.out.println("DONE: " + numSteps);
		System.out.println(adultDataRowClusters.size());
		int maxSize = 0;
		int numCompliant = 0;
		int sampleNum = 0;
		int[] lDivRatios = new int[11];
		
		Collection<AdultDataRowCluster> sampleOfClusters = new HashSet<AdultDataRowCluster>();
		for(AdultDataRowCluster cluster : adultDataRowClusters) {
			sampleNum++;
			if(sampleNum < 100) {
				sampleOfClusters.add(cluster);
			}
			maxSize = Math.max(cluster.rows.size(), maxSize);
			double lDivRatio = lDiversityRatio(cluster);
			lDivRatios[(int)(lDivRatio*10)]++;
			if(lDivRatio < (1.0 / L_FOR_DIVERSITY)) {
				numCompliant ++;
			}
		}
		int[] sizeHisto = new int[maxSize+1];
		for(AdultDataRowCluster cluster : adultDataRowClusters) {
			sizeHisto[cluster.rows.size()]++;
		}
		System.out.println("Sizes");
		for(Integer size : sizeHisto) {
			System.out.print(size +",");
		}
		
		System.out.println("Done");
		
	}

	private void initKMeans(int numClusters) {
		int numInitClusters = 0;
		for(AdultDataRow row : adultDataRows) {
			Set<AdultDataRow> individuallSet = new HashSet<AdultDataRow>();
			individuallSet.add(row);
			adultDataRowClusters.add(new AdultDataRowCluster(individuallSet));
			numInitClusters++;
			if(numInitClusters >= numClusters) {
				break;
			}
		}
	}

	private boolean updateKMeans() {
		boolean anyChanges = false;
		int rowNum = 0;
		for(AdultDataRow row : adultDataRows) {
			rowNum ++;
			if(rowNum%1000 == 0) {
				System.out.println(rowNum + " rowNum");
			}
			double minDistance = Double.MAX_VALUE;
			AdultDataRowCluster closestCluster = null;
			AdultDataRowCluster currentCluster = null;
			for(AdultDataRowCluster cluster : adultDataRowClusters) {
				
				double distance = distance(cluster, row);
				if(cluster.rows.contains(row)) {
					// Already in the cluster. 
					if(distance <= minDistance) {
						// If the cluster it is in is the closest so far, clear the best cluster.
						currentCluster = cluster;
						closestCluster = null;
					}
					// Update the min. 
					minDistance = distance;
				}
				else if(distance < minDistance) {
					minDistance = distance;
					closestCluster = cluster;
				}
			}
			if(closestCluster != null) {
				if(currentCluster != null) {
					currentCluster.rows.remove(row);
				}
				closestCluster.rows.add(row);
				anyChanges = true;
			}
		}
		return anyChanges;
	}

	private double distance(AdultDataRowCluster cluster, AdultDataRow row) {
		Set<AdultDataRow> individualSet = new HashSet<AdultDataRow>();
		individualSet.add(row);
		return distance(cluster, new AdultDataRowCluster(individualSet));
	}
	
	private double distance(AdultDataRow row1, AdultDataRow row2) {
		Set<AdultDataRow> individualSet = new HashSet<AdultDataRow>();
		individualSet.add(row1);
		Set<AdultDataRow> individualSet2 = new HashSet<AdultDataRow>();
		individualSet2.add(row2);
		return distance(new AdultDataRowCluster(individualSet), new AdultDataRowCluster(individualSet2));
	}



	private void initWardCluster() {
		for(AdultDataRow row : adultDataRows) {
			Set<AdultDataRow> individuallSet = new HashSet<AdultDataRow>();
			individuallSet.add(row);
			adultDataRowClusters.add(new AdultDataRowCluster(individuallSet));
		}
	}
	
	private void wardCluster() {
		// Find farthest two points
		
		int numMerges = 0;
		while(mergeTwoClusters()) {
			numMerges++;
			if(numMerges%100 == 0) {
				System.out.println(numMerges + " cache: " + cacheHits);
			}
		}
		System.out.println("DONE: " + numMerges);
		System.out.println(adultDataRowClusters.size());
		int maxSize = 0;
		int numCompliant = 0;
		int sampleNum = 0;
		int[] lDivRatios = new int[11];
		
		Collection<AdultDataRowCluster> sampleOfClusters = new HashSet<AdultDataRowCluster>();
		for(AdultDataRowCluster cluster : adultDataRowClusters) {
			sampleNum++;
			if(sampleNum < 100) {
				sampleOfClusters.add(cluster);
			}
			maxSize = Math.max(cluster.rows.size(), maxSize);
			double lDivRatio = lDiversityRatio(cluster);
			lDivRatios[(int)(lDivRatio*10)]++;
			if(lDivRatio < (1.0 / L_FOR_DIVERSITY)) {
				numCompliant ++;
			}
		}
		int[] sizeHisto = new int[maxSize+1];
		for(AdultDataRowCluster cluster : adultDataRowClusters) {
			sizeHisto[cluster.rows.size()]++;
		}
		System.out.println("Sizes");
		for(Integer size : sizeHisto) {
			System.out.print(size +",");
		}
		
		System.out.println("Done");
	}
	
	private boolean mergeTwoClusters() {
		double minDistance = Double.MAX_VALUE;
		int numTries = 0;
		AdultDataRowCluster bestRow1 = null;
		AdultDataRowCluster bestRow2 = null;
		//TODO: Partition into smaller subsets to make this faster, and/or
		//TODO: Look up l-diversity specific clustering methods. Original paper uses generalization.
		
		for(int i = 0; i < adultDataRowClusters.size(); i++) {
//		for(AdultDataRowCluster row1 : adultDataRowClusters) {
			AdultDataRowCluster row1 = adultDataRowClusters.get(i);
			if(row1.rows.size() >= K_FOR_ANONYMITY) {
				continue;
			}
			for(int j = i; j < adultDataRowClusters.size(); j++) {
//			for(AdultDataRowCluster row2 : adultDataRowClusters) {
				AdultDataRowCluster row2 = adultDataRowClusters.get(j);
				if(row1.equals(row2)) {
					continue;
				}
				double curDistance = distance(row1, row2);
				numTries++;
				if(curDistance < minDistance) {
					bestRow1 = row1;
					bestRow2 = row2;
					minDistance = curDistance;
				}
				if(numTries % 1000000 == 0) {
					System.out.println(numTries);
				}
			}
			// Arbitrarily pick row1 to be the first we found.
//			break;
		}
		
		// No clusters to merge
		if(bestRow1 == null || bestRow2 == null) { 
			// Recurse on clusters that are too large.
			boolean splitCluster = false;
			for(AdultDataRowCluster row1 : adultDataRowClusters) {
				if(row1.rows.size() < 2*K_FOR_ANONYMITY) {
					continue;
				}
				
				ClusterAdultRows clusterAdultRows = new ClusterAdultRows(row1.rows, NO_K_MEANS);
				adultDataRowClusters.remove(row1);
				adultDataRowClusters.addAll(clusterAdultRows.adultDataRowClusters);
				splitCluster = true;
			}
			return splitCluster;
		}
		bestRow1.merge(bestRow2);
		adultDataRowClusters.remove(bestRow2);
		return true;
	}

	private double distance(AdultDataRowCluster cluster1, AdultDataRowCluster cluster2) {
		// Weighted distance between two groups with univariate distance metric:
		// [n1*n2/(n1+n2)]*(average1-average2)^2
		// From: Domingo-Ferrer, Josep, and Josep Maria Mateo-Sanz. "Practical data-oriented microaggregation for statistical disclosure control." IEEE Transactions on Knowledge and data Engineering 14.1 (2002): 189-201.
	
		
		boolean cacheable = false; //cluster1.rows.size() == 1 && cluster2.rows.size() == 1; // Keep cache only of size n^2
		AdultDataRowClusterPair pair = new AdultDataRowClusterPair(cluster1, cluster2);
		if(cacheable && distanceCache.containsKey(pair)) {
//			cacheHits++;
			return distanceCache.get(pair);
		}
		
		int totalSize = cluster1.rows.size()+cluster2.rows.size();
		
		double totalAgeDistance = 1.0*cluster1.rows.size()*cluster2.rows.size()/(0.0+totalSize);
		double averageAgeDifference = averageAge(cluster1)-averageAge(cluster2);
		totalAgeDistance *= averageAgeDifference*averageAgeDifference;
		
		double totalSexDistance = 1.0*cluster1.rows.size()*cluster2.rows.size()/(0.0+totalSize);
		double averageSexDifference = averageSex(cluster1)-averageSex(cluster2);
		totalSexDistance *= averageSexDifference*averageSexDifference;
	
		
		/*
		 * TOO SLOW
		 * 
		 * HashMap<String, Double> occupationVectors1 = new HashMap<String, Double>();
		for(String occupation : cluster1.occupations.keySet()) {
			occupationVectors1.put(occupation, (0.0+cluster1.occupations.get(occupation)/ cluster1.rows.size()));
		}
		
		HashMap<String, Double> occupationVectors2 = new HashMap<String, Double>();
		for(String occupation : cluster2.occupations.keySet()) {
			occupationVectors2.put(occupation, (0.0+cluster2.occupations.get(occupation)/ cluster2.rows.size()));
		}
		
		double dotProduct = 0.0;
		Set<String> keysetUnion = new HashSet<String>();
		keysetUnion.addAll(cluster1.occupations.keySet());
		keysetUnion.addAll(cluster2.occupations.keySet());
		for(String occupation : keysetUnion) {
			double magnitude1 = occupationVectors1.containsKey(occupation) ?  occupationVectors1.get(occupation) : 0.0;
			double magnitude2 = occupationVectors2.containsKey(occupation) ?  occupationVectors2.get(occupation) : 0.0;
			dotProduct += magnitude1 * magnitude2;
		}
		
		if(dotProduct < 1.0 && dotProduct > 0) {
			System.out.println("DotProd: " + dotProduct);
		}*/
		
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
		
		
		
		
		/*
		boolean newOccupation = false;
		for(String occupation : cluster1.occupations.keySet()) {
			if(!cluster2.occupations.containsKey(occupation)) {
				newOccupation = true;
				break;
			}
		}
		if(!newOccupation) {
			for(String occupation : cluster2.occupations.keySet()) {
				if(!cluster1.occupations.containsKey(occupation)) {
					newOccupation = true;
					break;
				}
			}
		}
		
		totalDistance += newOccupation ? 0.1 : 0; */
		
		if(cacheable) {
			cacheHits++;
			distanceCache.put(pair, totalDistance);
		}
		return totalDistance;
	}
	
	private double averageAge(AdultDataRowCluster cluster) {
		return cluster.averageAge();
	}
	
	private double averageSex(AdultDataRowCluster cluster) {
		return cluster.averageSex();
	}
	
	private double lDiversityRatio(AdultDataRowCluster cluster) {
		int mostFrequent = 0;
		Map<String, Integer> valuesToFrequency = new HashMap<String, Integer>();
		for(AdultDataRow row : cluster.rows) {
			String key = OCCUPATION_LABEL + ":" + row.adult_attributes.get(OCCUPATION_LABEL).toString(); 
			int valToPut = 1;
			if(valuesToFrequency.containsKey(key)) {
				valToPut = valuesToFrequency.get(key)+1;
			}
			mostFrequent = Math.max(valToPut, mostFrequent);
			valuesToFrequency.put(key, valToPut);
		}
		return mostFrequent/cluster.rows.size();
	}
}
