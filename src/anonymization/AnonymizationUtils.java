package anonymization;

import java.util.Collection;
import java.util.Map;

import census.CensusDataRow;
import census.CensusGeneralization;
import static utils.Configuration.*;

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
//		System.out.println("Measuring k anaonymity over " + equivalenceClasses.size() + " classes");
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
//			int mostFrequentValueFrequency = 0;
//			int totalSensitiveValues = 0;
			for(Integer sensitiveValue : equivClass.keySet()) {
				int numOfSensitiveValue = equivClass.get(sensitiveValue);
				if(numOfSensitiveValue > 0) {
					distinctSensitiveValues ++;
				}
//				totalSensitiveValues += numOfSensitiveValue;
//				mostFrequentValueFrequency = Math.max(mostFrequentValueFrequency, numOfSensitiveValue);
			}
			
//			New l-diversity definition, need to investigate more before using.
//			if((0.0 + mostFrequentValueFrequency) / totalSensitiveValues > (1.0/L_DIVERSITY_REQUIREMENT)) {
//				return false;
//			}
			
//			 Old l-diversity definition.
			if (distinctSensitiveValues < l) {
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
	
	public static void analyzeCensusData(Collection<CensusDataRow> censusData) {
		CensusGeneralization cenGen = new CensusGeneralization();
		Map<QuasiIdentifier, Map<Integer, Integer>> equivClasses = 
				cenGen.getCensusEquivalenceClasses(censusData, QUASI_IDENTIFIER_KEYS, SENSITIVE_VALUE_KEY);
		int totalSize = 0;
		for(Map<Integer, Integer> sensValues : equivClasses.values()) {
			for(Integer i : sensValues.values()) {
				totalSize += i;
			}
		}
		System.out.println("Total equivalence classes: " + equivClasses.size() + " for an average size of " + (totalSize/equivClasses.size()));
		System.out.println("K-Anonymity: " + measureKAnonymity(equivClasses));
		System.out.println("L-Diversity: " + measureLDiversity(equivClasses));
	}
}
