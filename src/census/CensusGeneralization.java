package census;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import anonymization.AnonymizationUtils;
import anonymization.QuasiIdentifier;
import generalization.AgeGeneralization;
import utils.Configuration;

public class CensusGeneralization {
	
	// TODO
	public static void tryAllGeneralizations(Collection<CensusDataRow> censusData) {
		int curAgeGeneralization = Configuration.MAX_AGE_GENERALIZATION-1;
		int curAncestryGeneralization = Configuration.MAX_ANCESTRY_GENERALIZATION;
		int curClassGeneralization = Configuration.MAX_CLASS_GENERALIZATION-1;
//		for(int curAgeGen = 0; curAgeGen <= Configuration.MAX_AGE_GENERALIZATION; curAgeGen++) {
//			for(int curAncGen = 0; curAncGen <= Configuration.MAX_ANCESTRY_GENERALIZATION; curAncGen++) {
//				for(int curClassGen = 0; curClassGen <= Configuration.MAX_CLASS_GENERALIZATION; curClassGen++) {
//					System.out.println("Age: " + curAgeGen + " Anc: " + curAncGen + " Class: " + curClassGen);
//					Integer[] gens = {curAgeGen, curAncGen, curClassGen};
//					Collection<CensusDataRow> generalized = getCensusGeneralizedData(censusData, gens);
//					System.out.println("Generalized");
//					AnonymizationUtils.analyzeCensusData(generalized);
//				}
//			}
//		}
		Integer[] gens = {curAgeGeneralization, curAncestryGeneralization, curClassGeneralization};
		Collection<CensusDataRow> generalized = getCensusGeneralizedData(censusData, gens);
		System.out.println("Generalized");
//		AnonymizationUtils.analyzeCensusData(generalized);
	}
	
	// TODO:
	public static Collection<CensusDataRow> getCensusGeneralizedData(Collection<CensusDataRow> censusData, Integer[] generalizationLevels) {
		if(generalizationLevels.length != 3) {
			System.err.println("Generalization Levels wrong legnth " + generalizationLevels.length);
			return null;
		}
		Collection<CensusDataRow> generalizedData = new ArrayList<CensusDataRow>(censusData.size());
		for(CensusDataRow dataRow : censusData) {
			generalizedData.add(dataRow.getGeneralizedDataRow(generalizationLevels));
		}
		return generalizedData;
	}

	public static Map<QuasiIdentifier, Map<Integer, Integer>> getCensusEquivalenceClasses(Collection<CensusDataRow> censusDataRows,
		String[] quasiIdentifiers, String sensitiveValueKey) {
		
		System.out.println("Getting equiv classes");
	
		Map<QuasiIdentifier, Map<Integer, Integer>> quasiIdentifiersToSensitiveValues = new HashMap<QuasiIdentifier, Map<Integer, Integer>>();
		Set<String> quasiIdentifierKeys = new HashSet<String>();
		for(String qid : quasiIdentifiers) {
			quasiIdentifierKeys.add(qid);
		}
		for(CensusDataRow censusDataRow : censusDataRows) {
			QuasiIdentifier qid = censusDataRow.getQuasiIdentifier(quasiIdentifierKeys);
			Integer currentSensitiveValue = censusDataRow.getSensitiveValue(sensitiveValueKey);
			
			if(quasiIdentifiersToSensitiveValues.containsKey(qid)) {
				Map<Integer, Integer> sensitiveValues = quasiIdentifiersToSensitiveValues.get(qid);
				Integer currentCount = sensitiveValues.get(currentSensitiveValue);
				if(currentCount == null) {
					currentCount = 0;
				}
				sensitiveValues.put(currentSensitiveValue, currentCount+1);
			} else {
				Map<Integer, Integer> sensitiveValue = new HashMap<Integer, Integer>();
				sensitiveValue.put(currentSensitiveValue, 1);
				quasiIdentifiersToSensitiveValues.put(qid, sensitiveValue);
			}
		}
		return quasiIdentifiersToSensitiveValues;
	}
}
