package adult;

import static utils.Configuration.SENSITIVE_VALUE_KEY;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adultAttributes.AdultDataAttribute;
import utils.Configuration;

public class AdultDataRow {
	public Map<String, AdultDataAttribute> adult_attributes;
	
	public AdultDataRow(Collection<AdultDataAttribute> adultAttributes) {
		adult_attributes = new HashMap<String, AdultDataAttribute>(adultAttributes.size());
		for(AdultDataAttribute attribute : adultAttributes) {
			adult_attributes.put(attribute.label, attribute);
		}
	}
	
	public boolean isValid() {
		for(AdultDataAttribute attr : adult_attributes.values()) {
			if(!attr.isValid()) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(AdultDataAttribute att : adult_attributes.values()) {
			sb.append(att.label).append(": ").append(att.attribute_value).append("\n");
		}
		return sb.toString();
	}

	public String sensitiveValues() {
		return Configuration.SENSITIVE_VALUE_KEY + ":" + adult_attributes.get(Configuration.SENSITIVE_VALUE_KEY);
	}
	
	public List<AdultDataAttribute> getNonSensitiveAttributes() {
		List<AdultDataAttribute> nonSensitiveAttributes = new ArrayList<AdultDataAttribute>();
		for(String key : adult_attributes.keySet()) {
			if(!key.equals(SENSITIVE_VALUE_KEY)) {
				nonSensitiveAttributes.add(adult_attributes.get(key));
			}
		}
		return nonSensitiveAttributes;
	}
	
	public List<AdultDataAttribute> getSensitiveAttributes() {
		List<AdultDataAttribute> sensitiveAttributes = new ArrayList<AdultDataAttribute>();
		sensitiveAttributes.add(adult_attributes.get(SENSITIVE_VALUE_KEY));
		return sensitiveAttributes;
	}
}
