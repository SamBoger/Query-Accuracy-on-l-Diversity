package adultAttributes;

public abstract class AdultDataAttribute {

	public Object attribute_value;
	public String label;
	
	public AdultDataAttribute(Object attributeValue, String lab) {
		attribute_value = attributeValue;
		label = lab;
	}
	
	public abstract boolean isValid();
	
	public abstract String toString();
}
