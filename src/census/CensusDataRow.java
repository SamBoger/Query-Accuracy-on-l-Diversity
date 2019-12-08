package census;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import anonymization.QuasiIdentifier;


public class CensusDataRow {
	//TODO Build a map of data or turn this into a Map<String, Integer>??
	//public CensusDataAttribute[] census_attributes;
	public Map<String, CensusDataAttribute> census_attributes;
	
	public CensusDataRow(Collection<CensusDataAttribute> censusAttributes) {
//		census_attributes = censusAttributes;
		census_attributes = new HashMap<String, CensusDataAttribute>(censusAttributes.size());
		for(CensusDataAttribute attribute : censusAttributes) {
			census_attributes.put(attribute.label, attribute);
		}
	}
	
	public QuasiIdentifier getQuasiIdentifier(Set<String> quasiIdentifierKeys) {
		Map<String, Integer> quasiIdentifierData = new HashMap<String, Integer>(quasiIdentifierKeys.size());
		for(String attributeName : census_attributes.keySet()) {
			if(!quasiIdentifierKeys.contains(attributeName)) {
				continue;
			}
			CensusDataAttribute attribute = census_attributes.get(attributeName);
			quasiIdentifierData.put(attribute.label, attribute.attribute_value);
		}
		return new QuasiIdentifier(quasiIdentifierData);
	}
	
	public Integer getSensitiveValue(String sensitiveValueKey) {
		CensusDataAttribute sensitiveValue = census_attributes.get(sensitiveValueKey);
		if(sensitiveValue == null) {
			return null;
		}
		return sensitiveValue.attribute_value;
	}
	
	public CensusDataRow getGeneralizedDataRow(Map<String, Integer> generalizationLevels) {
		Set<CensusDataAttribute> attributes = new HashSet<CensusDataAttribute>(census_attributes.size());
		for(CensusDataAttribute attribute : census_attributes.values()) {
			if(generalizationLevels.containsKey(attribute.label) && generalizationLevels.get(attribute.label) > 0) {
				attributes.add(attribute.getGeneralization(generalizationLevels.get(attribute.label)));
			} else {
				attributes.add(attribute);
			}
		}
		return new CensusDataRow(attributes);
	}
	
	public boolean isValid() {
		for(CensusDataAttribute attr : census_attributes.values()) {
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
		for(CensusDataAttribute att : census_attributes.values()) {
			sb.append(att.label).append(": ").append(att.attribute_value).append("\n");
		}
		return sb.toString();
	}
}
