package generalization;

public class AncestryTwoGeneralization extends IntegerGeneralization {
	
	// dAncestry = 0
	// 0
	
	// dAncestry = 1
	// 999
	
	// dAncestry = 2
	// 1-99 Western Europe Except Spain
	
	// dAncestry = 3
	// 100-180 Eastern Europe and Soviet Union
	// 180 - 199 Europ N.E.C.
	
	// dAncestry = 4
	// 200-299 Hispanic Including Spain
	
	// dAncestry = 5
	// 300-359 West Indies Excluding Hispanic
	
	// dAncestry = 6
	// 360-399 Central and South America except Hispanic
	
	// dAncestry = 7
	// 400-499 North Africa and Southwest Asia
	
	// dAncestry = 8
	// 500-599 Subsaharan Africa
	
	// dAncestry = 9
	// 600-699 South Asia
	
	// dAncestry = 10
	// 700-799 Other Asia
	
	// dAncestry = 11
	// 800-899 Pacific
	
	// dAncestry = 12
	// 900-998 North American and Residual/Other/Non-encoded responses
	
	public AncestryTwoGeneralization(int generalizationLevel) {
		super(generalizationLevel);
	}

	@Override
	public Integer getGeneralizedData(Integer rawData) {
		if(generalization_level == 0) {
			return rawData;
		}
		if(generalization_level == 1) { 
			return 0;
		}
		return rawData;
	}

}
