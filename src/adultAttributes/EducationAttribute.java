package adultAttributes;

public class EducationAttribute extends AdultStringDataAttribute {

	public EducationAttribute(Object attributeValue, String lab) {
		super(attributeValue, lab);
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
