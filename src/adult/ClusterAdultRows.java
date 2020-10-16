package adult;

import static utils.Configuration.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClusterAdultRows {
	
//	private Map<AdultDataRowCollectionPair, Double> distanceCache;
	public Collection<Collection<AdultDataRow>> adultDataRowClusters;
	
	private static final int K_FOR_ANONYMITY = 10;
	
	
	public ClusterAdultRows(Collection<AdultDataRow> adultDataRows) {
//		distanceCache = new HashMap<AdultDataRowCollectionPair, Double>(adultDataRows.size()*adultDataRows.size());
		adultDataRowClusters = new HashSet<Collection<AdultDataRow>>();
		for(AdultDataRow row : adultDataRows) {
			Set<AdultDataRow> individuallSet = new HashSet<AdultDataRow>();
			individuallSet.add(row);
			adultDataRowClusters.add(individuallSet);
		}
		wardCluster();
	}
	
	private void wardCluster() {
		// Find farthest two points
		double maxDistance = 0;
		int numTries = 0;
		Collection<AdultDataRow> bestRow1 = null;
		Collection<AdultDataRow> bestRow2 = null;
		
		//TODO: Partition into smaller subsets to make this faster, and/or
		//TODO: Look up l-diversity specific clustering methods. Original paper uses generalization.
		for(Collection<AdultDataRow> row1 : adultDataRowClusters) {
			for(Collection<AdultDataRow> row2 : adultDataRowClusters) {
				double curDistance = distance(row1, row2);
				numTries++;
				if(curDistance > maxDistance) {
					bestRow1 = row1;
					bestRow2 = row2;
					maxDistance = curDistance;
				}
				if(numTries % 1000000 == 0) {
					System.out.println(numTries);
				}
			}
		}
		System.out.println("Max distance: " + maxDistance + " between row1: " + bestRow1.iterator().next() + " and row2: " + bestRow2.iterator().next());
		
	}

	private double distance(Collection<AdultDataRow> group1, Collection<AdultDataRow> group2) {
		// Weighted distance between two groups with univariate distance metric:
		// [n1*n2/(n1+n2)]*(average1-average2)^2
		double totalDistance = 1.0*group1.size()*group2.size()/(0.0+group1.size()+group2.size());
		double averageDifference = averageAge(group1)-averageAge(group2);
		totalDistance *= averageDifference*averageDifference;
		return totalDistance;
	}
	
//	private double distance(AdultDataRow row1, AdultDataRow row2) {
//		AdultDataRowCollectionPair pair = new AdultDataRowCollectionPair(row1, row2);
//		
//		double distance = 0.0;
//		
//		if(distanceCache.containsKey(pair)) {
//			distance = distanceCache.get(pair);
//		} else {
//			distanceCache.put(pair, distance);
//		}
//		return distance;
//	}
	
	private double averageAge(Collection<AdultDataRow> cluster) {
		double totalAge = 0.0;
		for(AdultDataRow row : cluster) {
			totalAge += (Integer)row.adult_attributes.get(AGE_LABEL).attribute_value;
		}
		return totalAge / cluster.size();
	}
}
