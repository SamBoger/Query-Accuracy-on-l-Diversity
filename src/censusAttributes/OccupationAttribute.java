package censusAttributes;

import static utils.Configuration.OCCUPATION_GRANULARITY;

public class OccupationAttribute extends CensusDataAttribute {

	public OccupationAttribute(int attributeValue, String lab) {
		super(attributeValue, lab);
		
		attribute_value = attribute_value/OCCUPATION_GRANULARITY;
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public CensusDataAttribute getGeneralization(int generalizationLevel) {
		return new OccupationAttribute(attribute_value, label);
	}
}
