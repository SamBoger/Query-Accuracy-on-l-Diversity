package adultAttributes;

public class FiftyThousandAttribute extends AdultStringDataAttribute {

	public FiftyThousandAttribute(Object attributeValue, String lab) {
		// values are "<=50K" or ">=50K"
		super(attributeValue, lab);
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
