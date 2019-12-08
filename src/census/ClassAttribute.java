package census;

import static utils.Configuration.CLASS_GENERALIZATION_GRANULARITY;

public class ClassAttribute extends CensusDataAttribute {

	public ClassAttribute(int attributeValue, String lab) {
		super(attributeValue, lab);
	}

	@Override
	boolean isValid() {
		return true;
	}

	// Max = 4
	@Override
	CensusDataAttribute getGeneralization(int generalizationLevel) {
		if(generalizationLevel == 0) {
			return new ClassAttribute(attribute_value, label);
		}
		return new ClassAttribute(attribute_value/(CLASS_GENERALIZATION_GRANULARITY*(1<<generalizationLevel)),
				label);
	}
}
