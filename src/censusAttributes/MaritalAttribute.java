package censusAttributes;

import static utils.Configuration.MARITAL_DEFAULT_VALUE;
import static utils.Configuration.MARITAL_GENERALIZATION_GRANULARITY;

public class MaritalAttribute extends CensusDataAttribute {

	public MaritalAttribute(int attributeValue, String lab) {
		super(attributeValue, lab);
	}

	@Override
	public boolean isValid() {
		return true;
	}

	// To match the comparison paper, marital status has a generalization hierarchy of 3.
	@Override
	public CensusDataAttribute getGeneralization(int generalizationLevel) {
		if(generalizationLevel == 0) {
			return new MaritalAttribute(attribute_value, label);
		} else if (generalizationLevel == 1) {
			// If generalizing at level 1, split countries in half.
			return new MaritalAttribute(attribute_value/MARITAL_GENERALIZATION_GRANULARITY, label);
		} else {
			// If generalizing at 2, use constant default value.
			return new MaritalAttribute(MARITAL_DEFAULT_VALUE, label);
		}
	}

}
