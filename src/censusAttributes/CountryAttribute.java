package censusAttributes;

import static utils.Configuration.COUNTRY_DEFAULT_VALUE;
import static utils.Configuration.COUNTRY_GRANULARITY;
import static utils.Configuration.COUNTRY_GENERALIZATION_GRANULARITY;

public class CountryAttribute extends CensusDataAttribute {

	public CountryAttribute(int attributeValue, String lab) {
		super(attributeValue, lab);
		attribute_value = attribute_value / COUNTRY_GRANULARITY;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public CensusDataAttribute getGeneralization(int generalizationLevel) {
		if(generalizationLevel == 0) {
			return new CountryAttribute(attribute_value, label);
		} else if(generalizationLevel == 1) {
			return new CountryAttribute(attribute_value/COUNTRY_GENERALIZATION_GRANULARITY, label);
		} else {
			return new CountryAttribute(COUNTRY_DEFAULT_VALUE, label);
		}
		
	}

}
