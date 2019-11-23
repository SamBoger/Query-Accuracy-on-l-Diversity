package census;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import anonymization.QuasiIdentifier;

public class CensusGeneralization {
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

	public static Map<QuasiIdentifier, Map<Integer, Integer>> getCensusEquivalenceClasses(Collection<CensusDataRow> censusDataRows) {
			Map<QuasiIdentifier, Map<Integer, Integer>> quasiIdentifiersToSensitiveValues = new HashMap<QuasiIdentifier, Map<Integer, Integer>>();
			String[] quasiIdentifierKeys = {"Age", "Ancestry1"};
	//		String[] quasiIdentifierKeys = {"Ancestry1"};
			String sensitiveValueKey = "Salary";
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
