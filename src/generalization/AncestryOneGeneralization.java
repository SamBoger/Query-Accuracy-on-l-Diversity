package generalization;

public class AncestryOneGeneralization extends IntegerGeneralization {

	// dAncestry = 0
	// 999
	
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
	
	
	
	public AncestryOneGeneralization(int generalizationLevel) {
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
