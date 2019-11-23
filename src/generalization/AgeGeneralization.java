package generalization;

public class AgeGeneralization extends IntegerGeneralization {
	// Age goes from 0-7
	// Level 0 is {i} -> i
	// Level 1 is {0,1} -> 1, {2,3} -> 3 ....
	// Level 2 is {0,1,2} -> 2, {3,4,5} -> 5  ....
	// Level 3 is {0,1,2,3} -> 3, {4,5,6,7} -> 7
	
	int[][] ageGen =
	{
			{0,1,2,3,4,5,6,7},
			{1,1,3,3,5,5,7,7},
			{2,2,2,5,5,5,7,7},
			{3,3,3,3,7,7,7,7}
	};
	
	
	public AgeGeneralization(int generalizationLevel) {
		super(generalizationLevel);
	}
	
	@Override
	public Integer getGeneralizedData(Integer rawData) {
		if(generalization_level == 0) {
			return rawData;
		}
		return ageGen[generalization_level][rawData];
	}
}
