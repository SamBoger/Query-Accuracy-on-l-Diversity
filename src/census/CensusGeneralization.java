package census;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import anonymization.AnonymizationUtils;
import anonymization.Generalization;
import anonymization.QuasiIdentifier;
import static utils.Configuration.*;

public class CensusGeneralization {
	
    Set<Generalization> deadEndGeneralizations = null;
    Set<Generalization> visited = null;
	
    Map<Generalization, Boolean> cache = null;
    
	public CensusGeneralization() {
		deadEndGeneralizations = new HashSet<Generalization>();
		visited = new HashSet<Generalization>();
		cache = new HashMap<Generalization, Boolean>();
	}
	
	public void printMinGeneralization() {
		Generalization minGen = null;
		int minTotalLevels = Integer.MAX_VALUE;
		for(Generalization g : deadEndGeneralizations) {
			System.out.println("Possible min gen: " + g);
			if(g.getTotalLevels() < minTotalLevels) {
				minTotalLevels = minGen.getTotalLevels();
				minGen = g;
			}
		}
		if(minGen == null) {
			System.err.println("No min generalization");
			return;
		}
		System.out.println("min gen: " + minGen + " at total " + minGen.getTotalLevels());
	}
	
	// TODO
	public void tryAllGeneralizations(Collection<CensusDataRow> censusData) throws SQLException {
		Generalization curGeneralization = new Generalization(QUASI_IDNETIFIER_MAX_GENERALIZATIONS);
		if (!tryGeneralization(censusData, curGeneralization)) {
			System.err.println("Max generalization still too granular");
		} else {
			System.out.println("Max generalization have good L-diversity");
		}
		traverseForDeadEnds(censusData, curGeneralization);

//		String outputFilename = INPUT_DATABASE_FILENAME + "_" + "anonymized.sql";
//		CensusDatabaseUtils.createSqliteDb(outputFilename);
//		CensusDatabaseUtils.writeCensusDataToDatabase(outputFilename, generalized);
	}
	
	private void traverseForDeadEnds(Collection<CensusDataRow> censusData, Generalization curGeneralization) {
		if(visited.contains(curGeneralization)) {
			return;
		}
		boolean isDeadEnd = true;
		visited.add(curGeneralization);
		for(int i = 0; i < curGeneralization.generalization_levels.length; i++) {
			if(curGeneralization.generalization_levels[i] == 0) {
				continue;
			}
			Integer[] newGeneralizationLevels = Arrays.copyOf(curGeneralization.generalization_levels,
					curGeneralization.generalization_levels.length); 
			newGeneralizationLevels[i] = newGeneralizationLevels[i] - 1;
			Generalization newGeneralization = new Generalization(newGeneralizationLevels);
			if(tryGeneralization(censusData, newGeneralization)) {
				isDeadEnd = false;
				traverseForDeadEnds(censusData, newGeneralization);
			}
		}
		// curGeneralization is l-diverse but none of the less generalized are!
		if(isDeadEnd && !deadEndGeneralizations.contains(curGeneralization)) {
			deadEndGeneralizations.add(curGeneralization);
			System.out.print("Got a dead end! " + curGeneralization); 
		}
	}

	public boolean tryGeneralization(Collection<CensusDataRow> censusData, Generalization curGeneralization) {
		if(cache.containsKey(curGeneralization)) {
			return cache.get(curGeneralization);
		}
		Map<String, Integer> generalizationLevels = new HashMap<String, Integer>();
		for(int i = 0; i < curGeneralization.generalization_levels.length; i++) {
			generalizationLevels.put(QUASI_IDENTIFIER_KEYS[i], curGeneralization.generalization_levels[i]);
		}
		Collection<CensusDataRow> generalized = getCensusGeneralizedData(censusData, generalizationLevels);
		boolean lDiverse = AnonymizationUtils.areEquivClassesLDiverse(
				getCensusEquivalenceClasses(generalized, QUASI_IDENTIFIER_KEYS, SENSITIVE_VALUE_KEY),
				L_DIVERSITY_REQUIREMENT);
		if(lDiverse) {
			cacheUp(curGeneralization);
		} else {
			cacheDown(curGeneralization);	
		}
		return lDiverse;
	}

	// When curGeneralization is true
	private void cacheUp(Generalization curGeneralization) {
		if(cache.containsKey(curGeneralization)) {
			return;
		}
		for(int i = 0; i < curGeneralization.generalization_levels.length; i++) {
			if(curGeneralization.generalization_levels[i] < QUASI_IDNETIFIER_MAX_GENERALIZATIONS[i]) {
				Integer[] transitiveGeneralizationLevels = Arrays.copyOf(curGeneralization.generalization_levels,
						curGeneralization.generalization_levels.length);
				transitiveGeneralizationLevels[i]++;
				Generalization transitiveGeneralization = new Generalization(transitiveGeneralizationLevels);
				if(!cache.containsKey(transitiveGeneralization)) {
					cacheUp(transitiveGeneralization);
				}
			}
		}
		System.out.println("caching " + curGeneralization + " with TRUE ");
		cache.put(curGeneralization, true);
	}

	// When curGeneralization is false
	private void cacheDown(Generalization curGeneralization) {
		if(cache.containsKey(curGeneralization)) {
			return;
		}
		for(int i = 0; i < curGeneralization.generalization_levels.length; i++) {
			if(curGeneralization.generalization_levels[i] > 0) {
				Integer[] transitiveGeneralizationLevels = Arrays.copyOf(curGeneralization.generalization_levels,
						curGeneralization.generalization_levels.length);
				transitiveGeneralizationLevels[i]--;
				Generalization transitiveGeneralization = new Generalization(transitiveGeneralizationLevels);
				if(!cache.containsKey(transitiveGeneralization)) {
					cacheDown(transitiveGeneralization);
				}
			}
		}
		System.out.println("caching " + curGeneralization + " with FALSE ");
		cache.put(curGeneralization, false);
	}
	// TODO:
	public Collection<CensusDataRow> getCensusGeneralizedData(Collection<CensusDataRow> censusData, Map<String, Integer> generalizationLevels) {
		Collection<CensusDataRow> generalizedData = new ArrayList<CensusDataRow>(censusData.size());
		for(CensusDataRow dataRow : censusData) {
			generalizedData.add(dataRow.getGeneralizedDataRow(generalizationLevels));
		}
		return generalizedData;
	}

	public Map<QuasiIdentifier, Map<Integer, Integer>> getCensusEquivalenceClasses(Collection<CensusDataRow> censusDataRows,
		String[] quasiIdentifiers, String sensitiveValueKey) {
		
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
