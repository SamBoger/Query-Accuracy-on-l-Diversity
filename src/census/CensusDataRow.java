package census;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import anonymization.QuasiIdentifier;
import utils.Configuration;


public class CensusDataRow {
	//TODO Build a map of data or turn this into a Map<String, Integer>??
	public CensusDataAttribute[] census_attributes;
	
	public CensusDataRow(CensusDataAttribute[] censusAttributes) {
		census_attributes = censusAttributes;
//		census_data = new HashMap<String, Integer>(dataInRow.length);
//		for(int i = 0; i < dataInRow.length; i++) {
//			census_data.put(Configuration.DATA_SPECIFICATION[i].label, dataInRow[i]);
//		}
	}
	
	public QuasiIdentifier getQuasiIdentifier(Set<String> quasiIdentifierKeys) {
		Map<String, Integer> quasiIdentifierData = new HashMap<String, Integer>(quasiIdentifierKeys.size());
		for(int i = 0; i < census_attributes.length; i++) {
			if(!quasiIdentifierKeys.contains(census_attributes[i].label)) {
				continue;
			}
			quasiIdentifierData.put(census_attributes[i].label, census_attributes[i].attribute_value);
		}
		return new QuasiIdentifier(quasiIdentifierData);
	}
	
	public Integer getSensitiveValue(String sensitiveValueKey) {
		for(int i = 0; i < census_attributes.length; i++) {
			if(census_attributes[i].label.equals(sensitiveValueKey)) {
				return census_attributes[i].attribute_value;
			}
		}
		return null;
	}
	
	public CensusDataRow getGeneralizedDataRow(Integer[] generalizationLevels) {
		if(generalizationLevels.length !=3) {
			System.err.println("Generalization levels incorrect length, got " + generalizationLevels.length + " wanted 3");
			return null;
		}
				// FIX for new design
		return this;
	}
	
	public boolean isValid() {
		for(CensusDataAttribute attr : census_attributes) {
			if(!attr.isValid()) {
				return false;
			}
		}
		return true;
	}
	
//	@Override
//	public String toString() {
//		StringBuilder sb = new StringBuilder();
//		for(String s : census_data.keySet()) {
//			sb.append(s).append(": ").append(census_data.get(s)).append("\n");
//		}
//		return sb.toString();
//	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(CensusDataAttribute att : census_attributes) {
			sb.append(att.label).append(": ").append(att.attribute_value).append("\n");
		}
		return sb.toString();
	}
}
