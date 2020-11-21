package adultAttributes;

public class OccupationAttribute extends AdultStringDataAttribute {

	public OccupationAttribute(Object attributeValue, String lab) {
		super(attributeValue, lab);
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
