package censusAttributes;

import static utils.Configuration.COUNTRY_DEFAULT_VALUE;
import static utils.Configuration.COUNTRY_GRANULARITY;
import static utils.Configuration.COUNTRY_GENERALIZATION_GRANULARITY;

public class CountryAttribute extends CensusDataAttribute {

	// TODO: Decide ebtween this and ancestry to match "country" in paper.
	public CountryAttribute(int attributeValue, String lab) {
		super(attributeValue, lab);
		attribute_value = attribute_value / COUNTRY_GRANULARITY;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	// To match the comparison paper, country has a generalization hierarchy of 3.
	@Override
	public CensusDataAttribute getGeneralization(int generalizationLevel) {
		if(generalizationLevel == 0) {
			return new CountryAttribute(attribute_value, label);
		} else if(generalizationLevel == 1) {
			// If generalizing at level 1, split countries in half.
			return new CountryAttribute(attribute_value/COUNTRY_GENERALIZATION_GRANULARITY, label);
		} else {
			// If generalizing at 2, use constant default value.
			return new CountryAttribute(COUNTRY_DEFAULT_VALUE, label);
		}
		
	}

}
