package adultAttributes;

import adultAttributes.AdultStringDataAttribute;

public class WorkClassAttribute extends AdultStringDataAttribute {

	public WorkClassAttribute(Object attributeValue, String lab) {
		super(attributeValue, lab);
	}

	@Override
	public boolean isValid() {
		return true;
	}
}