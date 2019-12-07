package census;

import utils.Configuration;

public class SalaryAttribute extends CensusDataAttribute {
	
	// Salary range is normalized to fall within range (0, 500,000) in 50 buckets
	public SalaryAttribute(int attributeValue, String lab) {
		super(attributeValue, lab);
		attribute_value = Math.max(Configuration.MIN_SALARY_VALUE, attribute_value);
		attribute_value = Math.min(Configuration.MAX_SALARY_VALUE, attribute_value);
		attribute_value = (attribute_value/Configuration.SALARY_GRANULARITY)*Configuration.SALARY_GRANULARITY;
	}

	@Override
	boolean isValid() {
		return true;
	}

	@Override
	int getGeneralization(int generalizationLevel) {
		return 0;
	}

}
