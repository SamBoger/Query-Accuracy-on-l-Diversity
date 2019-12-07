package generalization;

import utils.Configuration;

public class ClassGeneralization extends IntegerGeneralization {
	
	public ClassGeneralization(int generalizationLevel) {
		super(generalizationLevel);
	}

	@Override
	public
	Integer getGeneralizedData(Integer rawData) {
		if(generalization_level == 0) {
			return rawData;
		}
		int granularity = Configuration.CLASS_GENERALIZATION_GRANULARITY * generalization_level;
		return (rawData/granularity)*granularity;
	}

	@Override
	Integer getMaxGeneralizationLevel() {
		return 3;
	}

}
