package adultAttributes;

public abstract class AdultStringDataAttribute extends AdultDataAttribute {

	public AdultStringDataAttribute(Object attributeValue, String lab) {
		super(attributeValue, lab);
	}

	public abstract boolean isValid();
	
	public String toString() {
		return (String) attribute_value;
	}
}
