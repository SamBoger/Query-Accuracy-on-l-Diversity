package censusAttributes;

import static utils.Configuration.RACE_DEFAULT_VALUE;
import static utils.Configuration.RACE_GRANULARITY;

public class RaceAttribute extends CensusDataAttribute {

	public RaceAttribute(int attributeValue, String lab) {
		super(attributeValue, lab);
		attribute_value = attribute_value / RACE_GRANULARITY;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public CensusDataAttribute getGeneralization(int generalizationLevel) {
		if(generalizationLevel == 0) {
			return new RaceAttribute(attribute_value, label);
		} else {
			return new RaceAttribute(RACE_DEFAULT_VALUE, label);
		}
	}

}
