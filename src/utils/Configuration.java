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
	
	// generalizations: DataSpecification, curGeneralization, maxGeneralization
	
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
	
	public static DataSpecification[] DATA_SPECIFICATION = {
			new DataSpecification(12, "age"),
			new DataSpecification(35, "ancestry"),
			new DataSpecification(54, "class"),
			new DataSpecification(86, "occupation"),
			new DataSpecification(94, "race"),
			new DataSpecification(104, "salary"),
			new DataSpecification(107, "marital"),
			new DataSpecification(112, "gender"),
			new DataSpecification(122, "education")
			};
	
	/*
	 * Age configurations
	 */
	public static int AGE_GENERALIZATION_GRANULARITY = 5;
	public static int MAX_AGE_GENERALIZATION = 2;
	public static int MAX_AGE_VALUE = Integer.MAX_VALUE;
	public static int MIN_AGE_VALUE = 21;
	
	/*
	 * Ancestry configurations
	 */
	public static int MAX_ANCESTRY_GENERALIZATION = 1;
	
	
	/*
	 * Class configurations
	 */
	public static int CLASS_GENERALIZATION_GRANULARITY = 3;
	public static int MAX_CLASS_GENERALIZATION = 1;
	
	/*
	 * Occupation configurations
	 */
	
	/*
	 * Race configurations
	 */
	
	/*
	 * Salary configurations
	 */
	public static int MAX_SALARY_GENERALIZATION = 2;
	public static int MAX_SALARY_VALUE = 500000;
	public static int MIN_SALARY_VALUE = 0;
	public static int SALARY_GRANULARITY = 10000;
	
	/*
	 * Marital configurations
	 */
	
	/*
	 * Gender configurations
	 */
	
	/*
	 * Education configurations
	 */
}
