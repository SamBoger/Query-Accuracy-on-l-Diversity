package adultAttributes;

import static utils.Configuration.MAX_AGE_VALUE;
import static utils.Configuration.MIN_AGE_VALUE;

import adultAttributes.AdultDataAttribute;

public class AgeAttribute extends AdultDataAttribute {
	
	public AgeAttribute(Object value, String lab) {
		super(value, lab);
	}

	@Override
	public boolean isValid() {
		int attribute_int = attribute_value instanceof Integer ? (Integer) attribute_value : -1;
		return attribute_int >= MIN_AGE_VALUE && attribute_int <= MAX_AGE_VALUE;
	}

	@Override
	public String toString() {
		return ((Integer) attribute_value).toString();
	}
}