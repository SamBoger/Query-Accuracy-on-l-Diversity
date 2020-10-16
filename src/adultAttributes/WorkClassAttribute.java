package adultAttributes;

import adultAttributes.AdultDataAttribute;

public class WorkClassAttribute extends AdultDataAttribute {

	public WorkClassAttribute(Object attributeValue, String lab) {
		super(attributeValue, lab);
	}

	@Override
	public boolean isValid() {
		return true;
	}
}