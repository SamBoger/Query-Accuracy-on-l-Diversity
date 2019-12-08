package census;

import static utils.Configuration.ANCESTRY_GENERALIZATION_GRANULARITY;

public class AncestryAttribute extends CensusDataAttribute {

		// dAncestry = 1
		// 1-99 Western Europe Except Spain
		
		// dAncestry = 2
		// 100-180 Eastern Europe and Soviet Union
		// 180 - 199 Europ N.E.C.
		
		// dAncestry = 3
		// 200-299 Hispanic Including Spain
		
		// dAncestry = 4
		// 300-359 West Indies Excluding Hispanic
		
		// dAncestry = 5
		// 360-399 Central and South America except Hispanic
		
		// dAncestry = 6
		// 400-499 North Africa and Southwest Asia
		
		// dAncestry = 7
		// 500-599 Subsaharan Africa
		
		// dAncestry = 8
		// 600-699 South Asia
		
		// dAncestry = 9
		// 700-799 Other Asia
		
		// dAncestry = 10
		// 800-899 Pacific
		
		// dAncestry = 11
		// 900-998 North American and Residual/Other/Non-encoded responses
	public AncestryAttribute(int attributeValue, String lab) {
		super(attributeValue, lab);
		if(attribute_value == 0) {
		} else if(attribute_value < 99) {
			attribute_value = 1;
		} else if (attribute_value < 200) {
			attribute_value = 2;
		} else if (attribute_value < 300) {
			attribute_value = 3;
		} else if (attribute_value < 360) {
			attribute_value = 4;
		} else if (attribute_value < 400) {
			attribute_value = 5;
		} else if (attribute_value < 500) {
			attribute_value = 6;
		} else if (attribute_value < 600) {
			attribute_value = 7;
		} else if (attribute_value < 700) {
			attribute_value = 8;
		} else if (attribute_value < 800) {
			attribute_value = 9;
		} else if (attribute_value < 900) {
			attribute_value = 10;
		} else if (attribute_value < 999) {
			attribute_value = 11;
		} else {
			attribute_value = 0;
		}
	}

	@Override
	boolean isValid() {
		return true;
//		return attribute_value != 0;
	}

	// Max generalization = 4
	// TODO: custom merging of categories?
	@Override
	public CensusDataAttribute getGeneralization(int generalizationLevel) {
		if(generalizationLevel == 0) {
			return new AncestryAttribute(attribute_value, label);
		}
		return new AncestryAttribute(attribute_value/(ANCESTRY_GENERALIZATION_GRANULARITY*(1<<generalizationLevel)),
				label);
	}

}
