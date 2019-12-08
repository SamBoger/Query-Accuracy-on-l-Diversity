package census;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import anonymization.AnonymizationUtils;
import anonymization.QuasiIdentifier;
import static utils.Configuration.*;

public class CensusGeneralization {
	
	// TODO
	public static void tryAllGeneralizations(Collection<CensusDataRow> censusData) {
		int curAgeGeneralization = MAX_AGE_GENERALIZATION;
		int curAncestryGeneralization = MAX_ANCESTRY_GENERALIZATION;
		int curClassGeneralization = MAX_CLASS_GENERALIZATION;
		Map<String, Integer> generalizationLevels = new HashMap<String, Integer>();
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
		generalizationLevels.put(AGE_LABEL, 2);
		generalizationLevels.put(ANCESTRY_LABEL, 1);
		generalizationLevels.put(CLASS_LABEL, 1);
		Collection<CensusDataRow> generalized = getCensusGeneralizedData(censusData, generalizationLevels);
		System.out.println("Generalized");
		AnonymizationUtils.analyzeCensusData(generalized);
	}
	
	// TODO:
	public static Collection<CensusDataRow> getCensusGeneralizedData(Collection<CensusDataRow> censusData, Map<String, Integer> generalizationLevels) {
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
