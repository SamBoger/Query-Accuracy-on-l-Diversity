package census;

import static utils.Configuration.*;;

public class SalaryAttribute extends CensusDataAttribute {
	
	// Salary range is normalized to fall within range (0, 500,000) in 50 buckets
	public SalaryAttribute(int attributeValue, String lab) {
		super(attributeValue, lab);
		attribute_value = Math.max(MIN_SALARY_VALUE, attribute_value);
		attribute_value = Math.min(MAX_SALARY_VALUE, attribute_value);
		attribute_value = (attribute_value/SALARY_GRANULARITY)*SALARY_GRANULARITY;
	}

	@Override
	boolean isValid() {
		return true;
	}

	@Override
	CensusDataAttribute getGeneralization(int generalizationLevel) {
		return new SalaryAttribute(attribute_value, label);
	}

}
