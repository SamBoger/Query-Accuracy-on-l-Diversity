package generalization;

public abstract class IntegerGeneralization {
	// CODE IDEA:
	// For categorical, create map from (raw_data, anon_level -> generalize_data, explanation)
	// Question: how to do for ranges?
	
	
	int generalization_level = 0;
	public IntegerGeneralization(int generalizationLevel) {
		generalization_level = generalizationLevel;
	}
	abstract Integer getGeneralizedData(Integer rawData);
	abstract Integer getMaxGeneralizationLevel();
}