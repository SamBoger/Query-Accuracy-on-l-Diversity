package censusAttributes;

import static utils.Configuration.SEX_DEFAULT_VALUE;;

public class SexAttribute extends CensusDataAttribute {

	public SexAttribute(int attributeValue, String lab) {
		super(attributeValue, lab);
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public CensusDataAttribute getGeneralization(int generalizationLevel) {
		if(generalizationLevel == 0) {
			return new SexAttribute(attribute_value, label);
		} else {
			return new SexAttribute(SEX_DEFAULT_VALUE, label);
		}
	}

}
