package censusAttributes;

import static utils.Configuration.CLASS_GENERALIZATION_GRANULARITY;

public class ClassAttribute extends CensusDataAttribute {

	public ClassAttribute(int attributeValue, String lab) {
		super(attributeValue, lab);
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public CensusDataAttribute getGeneralization(int generalizationLevel) {
		if(generalizationLevel == 0) {
			return new ClassAttribute(attribute_value, label);
		}
		// Divide by 2*(2^(level-1))
		return new ClassAttribute(attribute_value/(CLASS_GENERALIZATION_GRANULARITY*(1<<generalizationLevel)),
				label);
	}
}
