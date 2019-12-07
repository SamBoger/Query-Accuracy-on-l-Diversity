package census;

public class ClassAttribute extends CensusDataAttribute {

	public ClassAttribute(int attributeValue, String lab) {
		super(attributeValue, lab);
	}

	@Override
	boolean isValid() {
		return true;
	}

	// Max = 5
	@Override
	int getGeneralization(int generalizationLevel) {
		return attribute_value / (1 << generalizationLevel);
	}
}
