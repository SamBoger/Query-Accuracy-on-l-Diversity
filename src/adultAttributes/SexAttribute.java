package adultAttributes;

public class SexAttribute extends AdultStringDataAttribute {

	public SexAttribute(Object attributeValue, String lab) {
		super(attributeValue, lab);
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
