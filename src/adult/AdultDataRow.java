package adult;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import adultAttributes.AdultDataAttribute;

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
}
