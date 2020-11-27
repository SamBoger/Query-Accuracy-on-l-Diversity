package utils;

public class Configuration {
	
	public static class DataSpecification {
		public int csvColumnNum;
		public String label;
		public boolean isInt;
		private DataSpecification(int csvCol, String lab) {
			csvColumnNum = csvCol;
			label = lab;
		}
		private DataSpecification(int csvCol, String lab, boolean isInt) {
			csvColumnNum = csvCol;
			label = lab;
			this.isInt = isInt;
		}
	}
	
	private Configuration() {}

	/*
	 * Age configurations
	 */
	public static final String AGE_LABEL = "age";
	public static final int AGE_GENERALIZATION_GRANULARITY = 5;
	public static final int MAX_AGE_GENERALIZATION = 2;
//	public static final int MAX_AGE_VALUE = Integer.MAX_VALUE;
	public static final int MAX_AGE_VALUE = 90;
	public static final int MIN_AGE_VALUE = 17;
	
	/*
	 * Ancestry configurations
	 */
	public static final String ANCESTRY_LABEL = "ancestry";
	public static final int ANCESTRY_GENERALIZATION_GRANULARITY = 2;
	public static final int MAX_ANCESTRY_GENERALIZATION = 1;
	
	
	/*
	 * Class configurations
	 */
	public static final String CLASS_LABEL = "class";
	public static final int CLASS_GENERALIZATION_GRANULARITY = 2;
	public static final int MAX_CLASS_GENERALIZATION = 1;
	
	/*
	 * Occupation configurations
	 */
	public static final String OCCUPATION_LABEL = "occupation";
	public static final int OCCUPATION_GRANULARITY = 20;
	
	/*
	 * Country configurations
	 */
	public static final String COUNTRY_LABEL = "country";
	public static final int COUNTRY_GRANULARITY = 10;
	public static final int COUNTRY_GENERALIZATION_GRANULARITY = 500;
	public static final int COUNTRY_DEFAULT_VALUE = 0;
	
	/*
	 * Race configurations
	 */
	public static final String RACE_LABEL = "race";
	public static final int RACE_GRANULARITY = 100;
	public static final int RACE_DEFAULT_VALUE = 0;
	
	/*
	 * Salary configurations
	 */
	public static final String SALARY_LABEL = "salary";
	public static final int MAX_SALARY_GENERALIZATION = 2;
	public static final int MAX_SALARY_VALUE = 500000;
	public static final int MIN_SALARY_VALUE = 0;
	public static final int SALARY_GRANULARITY = 10000;
	
	/*
	 * Marital configurations
	 */
	public static final String MARITAL_LABEL = "marital";
	public static final int MARITAL_GENERALIZATION_GRANULARITY = 2;
	public static final int MARITAL_DEFAULT_VALUE = 0;
	
	/*
	 * Sex configurations
	 */
	public static final String SEX_LABEL = "sex";
	public static final int SEX_DEFAULT_VALUE = 1;
	
	/*
	 * Education configurations
	 */
	public static final String EDUCATION_LABEL = "education";
	public static final int EDUCATION_GENERALIZATION_GRANULARITY = 2;
	
	/*
	 * Work class configurations
	 */
	public static final String WORK_CLASS_LABEL = "work_class";
	
	/*
	 * Education configurations
	 */
	public static final String HOURS_LABEL = "hours";
	
	public static final String FIFTY_THOUSAND_LABEL = "fiftyk";
	
	// Census website : Paper : database
	//
	// 12 AGE : Age : age
	// 35 ANCSTRY1 : Race : ancestry
	// 54 CLASS : Work-class : class
	// 86 OCCUP : Occupation : occupation
	// 89 POB : Country : country
	// 104 RPINCOME : Salary-class : salary
	// 107 RSPOUSE : Marital : marital
	// 112 SEX : Gender : sex
	// 122 YEARSCH : Education : education
	
	public static final DataSpecification[] CENSUS_DATA_SPECIFICATION = {
			new DataSpecification(12, AGE_LABEL),
			new DataSpecification(35, ANCESTRY_LABEL),
			new DataSpecification(54, CLASS_LABEL),
			new DataSpecification(86, OCCUPATION_LABEL),
			new DataSpecification(94, RACE_LABEL),
			new DataSpecification(104, SALARY_LABEL),
			new DataSpecification(107, MARITAL_LABEL),
			new DataSpecification(112, SEX_LABEL),
			new DataSpecification(122, EDUCATION_LABEL)
			};
	private static final boolean IS_INT = true;
	
	
	public static final DataSpecification[] ADULT_DATA_SPECIFICATION = {
			new DataSpecification(0, AGE_LABEL, IS_INT),
			new DataSpecification(13, COUNTRY_LABEL, !IS_INT),
			new DataSpecification(3, EDUCATION_LABEL, !IS_INT),
			new DataSpecification(5, MARITAL_LABEL, !IS_INT),
			new DataSpecification(6, OCCUPATION_LABEL, !IS_INT),
			new DataSpecification(8, RACE_LABEL, !IS_INT),
			new DataSpecification(9, SEX_LABEL, !IS_INT), // bool
			new DataSpecification(1, WORK_CLASS_LABEL, !IS_INT),
			new DataSpecification(14, FIFTY_THOUSAND_LABEL, !IS_INT), // bool
			
			};
	
	public static final int L_DIVERSITY_REQUIREMENT = 10;
	
	// age, ancestry, class --> [0, 4, 4], [1, 1, 4], [3, 4, 1], [4, 1, 1], [5, 0, 4]

	// This defines the Quasi Identifier attributes to consider for l-diversity.
	// These will be the ones generalized if computing generalizations.
//	public static final String[] QUASI_IDENTIFIER_KEYS = {AGE_LABEL, ANCESTRY_LABEL, CLASS_LABEL};
	public static final String[] QUASI_IDENTIFIER_KEYS = {AGE_LABEL, ANCESTRY_LABEL, CLASS_LABEL, MARITAL_LABEL};
	
	// This must be the same length as QUASI_IDENTIFIER_KEYS and refers to the max generalization values
	// to attempt, in order of the fields in QUASI_IDENTIFIER_KEYS.
//	public static final Integer[] QUASI_IDNETIFIER_MAX_GENERALIZATIONS = {6, 6, 6};
	public static final Integer[] QUASI_IDENTIFIER_MAX_GENERALIZATIONS = {5, 5, 3, 2};

	// This is the data attribute to use as the sensitive value.
	public static final String SENSITIVE_VALUE_KEY = OCCUPATION_LABEL;
	
	public static final String INPUT_DATABASE_FILENAME = "adult.sql";
	
	public static final String CLUSTER_SWAPPED_DATABASE_NAME = "adult_swapped_education5.sql";//"adult_swapped_race_weight.sql";
	public static final String ORIGINAL_DATABASE = "adult.sql";
	public static final String BASIC_SWAPPED_DATABASE = "adult_swapped.sql";
	public static final String[] QI_COLUMNS = {
			RACE_LABEL,
			EDUCATION_LABEL
	};
	public static final Double[] QI_DISTANCE_WEIGHTS = {
			1.0,
			5.0
	};
	
	public static final Double AGE_WEIGHT = 1.0;
	public static final Double SEX_WEIGHT = 1.0;
	
	public static final int K_FOR_ANONYMITY = 10;
	public static final int L_FOR_DIVERSITY = 5;
	public static final double THETA_FOR_FREQUENCY = 0.3;
	
}
