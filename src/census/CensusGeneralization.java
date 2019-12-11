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
	
    Set<Generalization> minimum_generalization_candidates = null;
    Set<Generalization> visited_generalizations = null;
	
    Map<Generalization, Boolean> l_diverse_generalizations_cache = null;
    
 // TODO: Can we optimize by skipping generalizations that we won't pick as optimal?
//    int min_generalization_levels = Integer.MAX_VALUE;
    
	public CensusGeneralization() {
		minimum_generalization_candidates = new HashSet<Generalization>();
		visited_generalizations = new HashSet<Generalization>();
		l_diverse_generalizations_cache = new HashMap<Generalization, Boolean>();
	}
	
	public void printMinGeneralization() {
		Generalization minGen = null;
		int minTotalLevels = Integer.MAX_VALUE;
		for(Generalization g : minimum_generalization_candidates) {
			System.out.println("Possible min gen: " + g);
			if(g.getTotalLevels() < minTotalLevels) {
				minTotalLevels = g.getTotalLevels();
				minGen = g;
			}
		}
		if(minGen == null) {
			System.err.println("No min generalization");
			return;
		}
		System.out.println("min gen: " + minGen + " with total generalization levels " + minGen.getTotalLevels());
	}
	
	public void tryAllGeneralizations(Collection<CensusDataRow> censusData) throws SQLException {
		Generalization curGeneralization = new Generalization(QUASI_IDENTIFIER_MAX_GENERALIZATIONS);
		if (!isGeneralizationLDiverse(censusData, curGeneralization)) {
			// If the maximum generalized table is not l-diverse, then none of the less generalized ones will be either.
			System.err.println("Max generalization still too granular");
		} else {
			System.out.println("Max generalization has good L-diversity");
		}
		visit_generalization(censusData, curGeneralization);

//		String outputFilename = INPUT_DATABASE_FILENAME + "_" + "anonymized.sql";
//		CensusDatabaseUtils.createSqliteDb(outputFilename);
//		CensusDatabaseUtils.writeCensusDataToDatabase(outputFilename, generalized);
	}
	
	private void visit_generalization(Collection<CensusDataRow> censusData, Generalization curGeneralization) {
		if(visited_generalizations.contains(curGeneralization)) {
			return;
		}
		boolean isMinimumGeneralzationCandidate = true;
		visited_generalizations.add(curGeneralization);
		
		// Look at slightly less generalized tables.
		for(int i = 0; i < curGeneralization.generalization_levels.length; i++) {
			// If this attribute is fully generalized, move on to the next attribute.
			if(curGeneralization.generalization_levels[i] == 0) {
				continue;
			}
			Integer[] newGeneralizationLevels = Arrays.copyOf(curGeneralization.generalization_levels,
					curGeneralization.generalization_levels.length); 
			newGeneralizationLevels[i]--;
			Generalization newGeneralization = new Generalization(newGeneralizationLevels);
			if(isGeneralizationLDiverse(censusData, newGeneralization)) {
				// There is a less general table which still satisfies l-diversity!
				// So current generalization is not a candidate for the minimum.
				isMinimumGeneralzationCandidate = false;
				visit_generalization(censusData, newGeneralization);
			}
		}
		// The current generalization is l-diverse but none of the less generalized are!
		// Add this to the candidates if not already present.
		if(isMinimumGeneralzationCandidate && !minimum_generalization_candidates.contains(curGeneralization)) {
			minimum_generalization_candidates.add(curGeneralization);
			System.out.print("Got a candidate! " + curGeneralization); 
		}
	}

	public boolean isGeneralizationLDiverse(Collection<CensusDataRow> censusData, Generalization curGeneralization) {
		// If we have already computed this generalization, return the value from the cache.
		if(l_diverse_generalizations_cache.containsKey(curGeneralization)) {
			return l_diverse_generalizations_cache.get(curGeneralization);
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
			// This generalization is l-diverse, so are all the strictly more general ones.
			cacheUp(curGeneralization);
		} else {
			// This generalization is not l-diverse, so neither are the strictly less general ones.
			cacheDown(curGeneralization);	
		}
		return lDiverse;
	}

	private void cacheUp(Generalization curGeneralization) {
		// If we have already put this in the cache, then this method has already been called on this generalization.
		if(l_diverse_generalizations_cache.containsKey(curGeneralization)) {
			return;
		}
		for(int i = 0; i < curGeneralization.generalization_levels.length; i++) {
			if(curGeneralization.generalization_levels[i] < QUASI_IDENTIFIER_MAX_GENERALIZATIONS[i]) {
				Integer[] transitiveGeneralizationLevels = Arrays.copyOf(curGeneralization.generalization_levels,
						curGeneralization.generalization_levels.length);
				// Any more general generalization is also l-diverse by monotonicity.
				transitiveGeneralizationLevels[i]++;
				Generalization transitiveGeneralization = new Generalization(transitiveGeneralizationLevels);
				// TODO: This check seems redundant.
				if(!l_diverse_generalizations_cache.containsKey(transitiveGeneralization)) {
					cacheUp(transitiveGeneralization);
				}
			}
		}
		System.out.println("caching " + curGeneralization + " with TRUE ");
		l_diverse_generalizations_cache.put(curGeneralization, true);
	}

	private void cacheDown(Generalization curGeneralization) {
		// If we have already put this in the cache, then this method has already been called on this generalization.
		if(l_diverse_generalizations_cache.containsKey(curGeneralization)) {
			return;
		}
		for(int i = 0; i < curGeneralization.generalization_levels.length; i++) {
			if(curGeneralization.generalization_levels[i] > 0) {
				Integer[] transitiveGeneralizationLevels = Arrays.copyOf(curGeneralization.generalization_levels,
						curGeneralization.generalization_levels.length);
				// Any less general generalization is also not l-diverse by monotonicity.
				transitiveGeneralizationLevels[i]--;
				Generalization transitiveGeneralization = new Generalization(transitiveGeneralizationLevels);
				// TODO: This check seems redundant.
				if(!l_diverse_generalizations_cache.containsKey(transitiveGeneralization)) {
					cacheDown(transitiveGeneralization);
				}
			}
		}
		System.out.println("caching " + curGeneralization + " with FALSE ");
		l_diverse_generalizations_cache.put(curGeneralization, false);
	}
	
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
