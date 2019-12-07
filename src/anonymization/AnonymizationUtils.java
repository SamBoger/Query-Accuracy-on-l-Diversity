package anonymization;

import java.util.Collection;
import java.util.Map;

import census.CensusDataRow;
import census.CensusGeneralization;

public class AnonymizationUtils {

	public static boolean areEquivClassesKAnonymous(Map<QuasiIdentifier, Map<Integer, Integer>> equivalenceClasses, int k) {
		for(Map<Integer, Integer> equivClass : equivalenceClasses.values()) {
			int numEntries = 0;
			for(Integer numSensitiveValues : equivClass.values()) {
				numEntries += numSensitiveValues;
			}
			if (numEntries < k) {
				System.out.println("QuasiIdentifier has only " + numEntries + " rows, not " + k + "-anonymous");
				return false;
			}
		}
		return true;
	}
	
	public static int measureKAnonymity(Map<QuasiIdentifier, Map<Integer, Integer>> equivalenceClasses) {
		int minKAnonymity = Integer.MAX_VALUE;
		System.out.println("Measuring k anaonymity over " + equivalenceClasses.size() + " classes");
		for(Map<Integer, Integer> equivClass : equivalenceClasses.values()) {
			int numEntries = 0;
			for(Integer numSensitiveValues : equivClass.values()) {
				numEntries += numSensitiveValues;
			}
			minKAnonymity = Math.min(minKAnonymity, numEntries);
		}
		return minKAnonymity;
	}
	
	public static boolean areEquivClassesLDiverse(Map<QuasiIdentifier, Map<Integer, Integer>> equivalenceClasses, int l) {
		for(Map<Integer, Integer> equivClass : equivalenceClasses.values()) {
			int distinctSensitiveValues = 0;
			for(Integer sensitiveValue : equivClass.keySet()) {
				if(equivClass.get(sensitiveValue) > 0) {
					distinctSensitiveValues ++;
				}
			}
			if (distinctSensitiveValues < l) {
				System.out.println("QuasiIdentifier has only " + distinctSensitiveValues + " distinct sensitive values, not " + l + "-diverse");
				return false;
			}
		}
		return true;
	}
	
	public static int measureLDiversity(Map<QuasiIdentifier, Map<Integer, Integer>> equivalenceClasses) {
		int minLDiversity = Integer.MAX_VALUE;
		for(Map<Integer, Integer> equivClass : equivalenceClasses.values()) {
			int distinctSensitiveValues = 0;
			for(Integer sensitiveValue : equivClass.keySet()) {
				if(equivClass.get(sensitiveValue) > 0) {
					distinctSensitiveValues ++;
				}
			}
			minLDiversity = Math.min(minLDiversity, distinctSensitiveValues);
		}
		return minLDiversity;
	}
	
	public static double percentEquivClassesLDiverse(Map<QuasiIdentifier, Map<Integer, Integer>> equivalenceClasses, int l) {
		int numLDiverse = equivalenceClasses.size();
		for(Map<Integer, Integer> equivClass : equivalenceClasses.values()) {
			int distinctSensitiveValues = 0;
			for(Integer sensitiveValue : equivClass.keySet()) {
				if(equivClass.get(sensitiveValue) > 0) {
					distinctSensitiveValues ++;
				}
			}
			if (distinctSensitiveValues < l) {
				numLDiverse--;
			}
		}
		return (numLDiverse+0.0)/equivalenceClasses.size();
	}
	
	public static void analyzeCensusData(Collection<CensusDataRow> censusData, String[] quasiIdentifiers, String sensitiveValueKey) {
		Map<QuasiIdentifier, Map<Integer, Integer>> equivClasses = 
				CensusGeneralization.getCensusEquivalenceClasses(censusData, quasiIdentifiers, sensitiveValueKey);
		System.out.println("Got " + equivClasses.size() + " equivalence classes!");
		int totalSize = 0;
		for(Map<Integer, Integer> sensValues : equivClasses.values()) {
			for(Integer i : sensValues.values()) {
				totalSize += i;
			}
		}
		System.out.println("Total sensitive values: " + totalSize + " for an average size of " + (totalSize/equivClasses.size()));
		System.out.println("K-Anonymity: " + measureKAnonymity(equivClasses));
		System.out.println("L-Diversity: " + measureLDiversity(equivClasses));
		for(int l = 1; l < 10; l++) {
			System.out.println("l-Diverse pecent " + l + " = " + AnonymizationUtils.percentEquivClassesLDiverse(equivClasses, l));
		}
	}
}
