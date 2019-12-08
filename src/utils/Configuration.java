package utils;

public class Configuration {
	
	public static class DataSpecification {
		public int csvColumnNum;
		public String label;
		private DataSpecification(int csvCol, String lab) {
			csvColumnNum = csvCol;
			label = lab;
		}
	}
	
	private Configuration() {}

	public static final String[] QUASI_IDENTIFIER_KEYS = {"age", "ancestry", "class"};

	public static final String SENSITIVE_VALUE_KEY = "salary";
	

	/*
	 * Age configurations
	 */
	public static final String AGE_LABEL = "age";
	public static final int AGE_GENERALIZATION_GRANULARITY = 5;
	public static final int MAX_AGE_GENERALIZATION = 2;
	public static final int MAX_AGE_VALUE = Integer.MAX_VALUE;
	public static final int MIN_AGE_VALUE = 21;
	
	/*
	 * Ancestry configurations
	 */
	public static final String ANCESTRY_LABEL = "ancestry";
	public static final int ANCESTRY_GENERALIZATION_GRANULARITY = 1;
	public static final int MAX_ANCESTRY_GENERALIZATION = 1;
	
	
	/*
	 * Class configurations
	 */
	public static final String CLASS_LABEL = "class";
	public static final int CLASS_GENERALIZATION_GRANULARITY = 1;
	public static final int MAX_CLASS_GENERALIZATION = 1;
	
	/*
	 * Occupation configurations
	 */
	public static final String OCCUPATION_LABEL = "occupation";
	public static final int OCCUPATION_GRANULARITY = 20;
	
	/*
	 * Race configurations
	 */
	public static final String RACE_LABEL = "race";
	
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
	
	/*
	 * Gender configurations
	 */
	public static final String SEX_LABEL = "sex";
	public static final int DEFAULT_SEX_ATTRIBUTE = 1;
	
	/*
	 * Education configurations
	 */
	public static final String EDUCATION_LABEL = "education";
	
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
	
	public static final DataSpecification[] DATA_SPECIFICATION = {
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
}
