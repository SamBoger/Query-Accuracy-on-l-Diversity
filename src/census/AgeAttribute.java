package census;

import utils.Configuration;

public class AgeAttribute extends CensusDataAttribute {
	
	public AgeAttribute(int attributeValue, String lab) {
		super(attributeValue, lab);
	}

	@Override
	public boolean isValid() {
		return attribute_value >= Configuration.MIN_AGE_VALUE && attribute_value <= Configuration.MAX_AGE_VALUE;
	}

	// Max generalization = 6
	@Override
	public int getGeneralization(int generalizationLevel) {
		if(generalizationLevel == 0) {
			return attribute_value;
		}
		return attribute_value/(Configuration.AGE_GENERALIZATION_GRANULARITY*(1<<(generalizationLevel-1)));
	}
}
