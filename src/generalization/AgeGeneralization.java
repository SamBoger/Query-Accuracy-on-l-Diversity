package generalization;

public class AgeGeneralization extends IntegerGeneralization {
	// Age goes from 21-99
	
	public AgeGeneralization(int generalizationLevel) {
		super(generalizationLevel);
	}
	
	@Override
	public Integer getGeneralizedData(Integer rawData) {
		if(generalization_level == 0) {
			return rawData;
		}
		int granularity = utils.Configuration.AGE_GENERALIZATION_GRANULARITY * generalization_level;
		return (rawData/granularity)*granularity;
	}

	@Override
	Integer getMaxGeneralizationLevel() {
		return 3;
	}
}
