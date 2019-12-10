package censusAttributes;

import static utils.Configuration.*;

public class AgeAttribute extends CensusDataAttribute {
	
	public AgeAttribute(int attributeValue, String lab) {
		super(attributeValue, lab);
	}

	@Override
	public boolean isValid() {
		return attribute_value >= MIN_AGE_VALUE && attribute_value <= MAX_AGE_VALUE;
	}

	// Max generalization = 5
	@Override
	public CensusDataAttribute getGeneralization(int generalizationLevel) {
		if(generalizationLevel == 0) {
			return new AgeAttribute(attribute_value, label);
		}
		return new AgeAttribute((attribute_value - MIN_AGE_VALUE)/(AGE_GENERALIZATION_GRANULARITY*(1<<(generalizationLevel-1))),
			 label);
	}
}
