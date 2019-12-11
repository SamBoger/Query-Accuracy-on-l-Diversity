package censusAttributes;

import static utils.Configuration.RACE_DEFAULT_VALUE;
import static utils.Configuration.RACE_GRANULARITY;

public class RaceAttribute extends CensusDataAttribute {

	public RaceAttribute(int attributeValue, String lab) {
		super(attributeValue, lab);
		// TODO: Paper has 9 values here. The possible values are mostly 1-100 then 300-350 or so. Need to decide bucketing.
		attribute_value = attribute_value / RACE_GRANULARITY;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	// To match the comparison paper, race has a generalization hierarchy of 2.
	@Override
	public CensusDataAttribute getGeneralization(int generalizationLevel) {
		if(generalizationLevel == 0) {
			return new RaceAttribute(attribute_value, label);
		} else {
			// If generalizing, use constant default value.
			return new RaceAttribute(RACE_DEFAULT_VALUE, label);
		}
	}

}
