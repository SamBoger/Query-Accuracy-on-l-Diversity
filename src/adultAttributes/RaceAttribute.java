package adultAttributes;

public class RaceAttribute extends AdultStringDataAttribute {

	public RaceAttribute(Object attributeValue, String lab) {
		super(attributeValue, lab);
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
