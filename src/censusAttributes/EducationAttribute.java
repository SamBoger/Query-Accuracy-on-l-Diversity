package censusAttributes;

import static utils.Configuration.EDUCATION_GENERALIZATION_GRANULARITY;

public class EducationAttribute extends CensusDataAttribute {

	public EducationAttribute(int attributeValue, String lab) {
		super(attributeValue, lab);
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public CensusDataAttribute getGeneralization(int generalizationLevel) {
		if(generalizationLevel == 0) {
			return new EducationAttribute(attribute_value, label);
		}
		// Divide by 2*(2^(level-1))
		return new EducationAttribute(attribute_value/(EDUCATION_GENERALIZATION_GRANULARITY*(1<<(generalizationLevel-1))),
				label);
	}

}
