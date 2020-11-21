package adultAttributes;

public class CountryAttribute extends AdultStringDataAttribute {

	public CountryAttribute(Object attributeValue, String lab) {
		super(attributeValue, lab);
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
