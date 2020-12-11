package adult;

import static utils.Configuration.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import utils.Utils;

import static adult.AdultDatabaseUtils.*;

public class MeasureAdultDatabase {


	public static final int NUM_QUERIES = 1000;
	
	private Random rand;
	
	private final static String[] databaseNames = {
			"adult.sql",
			"adult_swapped.sql",
			"adult_swapped_age3.sql",
			"adult_swapped_age5.sql",
			"adult_swapped_sex3.sql",
			"adult_swapped_sex5.sql",
			"adult_swapped_race3.sql",
			"adult_swapped_race5.sql",
			"adult_swapped_education3.sql",
			"adult_swapped_education5.sql"};
	
	// These are the values limited to the options with at least 1000 entries each. When selection conjunctions from these it is still likely to get
	// more than a handful of rows.
//	private final static String[] sexes  = {"Female", "Male"};
//	private final static String[] educations = {"Bachelors", "Some-college", "11th", "HS-grad", "Assoc-acdm", "Assoc-voc", "Masters"};
//	private final static String[] occupations  = {"?", "Craft-repair", "Other-service", "Sales", "Exec-managerial", "Prof-specialty", "Handlers-cleaners", "Machine-op-inspct", "Adm-clerical", "Transport-moving", "Armed-Forces"};
//	private final static String[] races = {"White", "Asian-Pac-Islander", "Black"};
	
	// All the values present in the database.
	private final static String[] sexes  = {"Female", "Male"};
	private final static String[] educations = {"Bachelors", "Some-college", "11th", "HS-grad", "Prof-school", "Assoc-acdm", "Assoc-voc", "9th", "7th-8th", "12th", "Masters", "1st-4th", "10th", "Doctorate", "5th-6th", "Preschool"};
//	private final static String[] occupations  = {"Tech-support", "Craft-repair", "Other-service", "Sales", "Exec-managerial", "Prof-specialty", "Handlers-cleaners", "Machine-op-inspct", "Adm-clerical", "Farming-fishing", "Transport-moving", "Priv-house-serv", "Protective-serv", "Armed-Forces"};
	private final static String[] races = {"White", "Asian-Pac-Islander", "Amer-Indian-Eskimo", "Other", "Black"};

	private static final int MIN_AGE = 17;
	private static final int MAX_AGE = 91; // Max age is exclusive, so set to 91
	private static final int AGE_RANGE = MAX_AGE - MIN_AGE;
	private static final int AGE_GRANULARITY = 5;
	
	private final int default_education_selectivity = 8; // 8
	private final int default_sex_selectivity = 2;  // 2
	private final int default_race_selectivity = 3;  // 3
	private final int default_age_selectivity = 35;  // 35
	
	private int education_selectivity  = default_education_selectivity;
	private int sex_selectivity = default_sex_selectivity;
	private int race_selectivity = default_race_selectivity;
	private int age_selectivity = default_age_selectivity;

	private int num_experiments = -1;
	private static final int QUERY_DIMENSIONALITY = 3;

	
	public MeasureAdultDatabase() throws SQLException {
		this.rand = new Random();

//		compareToGroundTruth();
		
		runEducationSelectivityExperiment();

		runRaceSelectivityExperiment();

		runAgeSelectivityExperiment();

		runSexSelectivityExperiment();
	}

	private void compareToGroundTruth() throws SQLException {
		resetSensitivities();
		num_experiments = 1;
		double[][] allResults = new double[num_experiments][databaseNames.length];
		for(int numAgesIncluded = 0; numAgesIncluded < num_experiments; numAgesIncluded++) {
			//age_selectivity = (numAgesIncluded+1) * AGE_GRANULARITY;
			System.out.println("BASE comparison selectivity");
			allResults[numAgesIncluded] = runExperiment();
		}
		System.out.println("BASE selectivity results:");
		printExperiments(allResults);
	}

	private void resetSensitivities() {
		education_selectivity  = default_education_selectivity;
		sex_selectivity = default_sex_selectivity;
		race_selectivity = default_race_selectivity;
		age_selectivity = default_age_selectivity;
	}
	
	public void runAgeSelectivityExperiment() throws SQLException {
		resetSensitivities();
		num_experiments = AGE_RANGE / AGE_GRANULARITY;
		double[][] allResults = new double[num_experiments][databaseNames.length];
		for(int numAgesIncluded = 0; numAgesIncluded < num_experiments; numAgesIncluded++) {
			age_selectivity = (numAgesIncluded+1) * AGE_GRANULARITY;
			System.out.println("EXPERIMENT age selectivity: " + age_selectivity + " out of " + AGE_RANGE);
			allResults[numAgesIncluded] = runExperiment();
		}
		System.out.println("AGE selectivity results:");
		printExperiments(allResults);
	}

	public void runEducationSelectivityExperiment() throws SQLException {
		resetSensitivities();
		num_experiments = educations.length;
		double[][] allResults = new double[num_experiments][databaseNames.length];
		for(int numEducationsIncluded = 0; numEducationsIncluded < num_experiments; numEducationsIncluded++) {
			education_selectivity = numEducationsIncluded+1; // selectivities[numEducationsIncluded];
			System.out.println("EXPERIMENT education selectivity: " + education_selectivity + " out of " + educations.length);
			allResults[numEducationsIncluded] = runExperiment();
		}
		System.out.println("EDUCATION selectivity results:");
		printExperiments(allResults);
	}
	
	public void runSexSelectivityExperiment() throws SQLException {
		resetSensitivities();
		num_experiments = sexes.length;
		double[][] allResults = new double[num_experiments][databaseNames.length];
		for(int numSexesIncluded = 0; numSexesIncluded < num_experiments; numSexesIncluded++) {
			sex_selectivity = numSexesIncluded+1;
			System.out.println("EXPERIMENT sex selectivity: " + sex_selectivity + " out of " + sexes.length);
			allResults[numSexesIncluded] = runExperiment();
		}
		System.out.println("SEX selectivity results:");
		printExperiments(allResults);
	}
	
	public void runRaceSelectivityExperiment() throws SQLException {
		resetSensitivities();
		num_experiments = races.length;
		double[][] allResults = new double[num_experiments][databaseNames.length];
		for(int numRacesIncluded = 0; numRacesIncluded < num_experiments; numRacesIncluded++) {
			race_selectivity = numRacesIncluded+1;
			System.out.println("EXPERIMENT race selectivity: " + race_selectivity + " out of " + races.length);
			allResults[numRacesIncluded] = runExperiment();
		}
		System.out.println("RACE selectivity results:");
		printExperiments(allResults);
	}
	
	public double[] runExperiment() throws SQLException {
		Map<String, Double> databaseTotalError = new HashMap<String, Double>(databaseNames.length);
		double[] results = new double[databaseNames.length];
		for(int queryNum = 0; queryNum < NUM_QUERIES; queryNum++) {
			Map<String, Double> occupationResults = runRandomOccupationQueries();
		
		
			for(String databaseName : occupationResults.keySet()) {
				double databaseDistanceFromBaseline = occupationResults.get(databaseName);
				if(databaseTotalError.containsKey(databaseName)) {
					databaseDistanceFromBaseline += databaseTotalError.get(databaseName);
				}
				databaseTotalError.put(databaseName, databaseDistanceFromBaseline);
			}
		}
		for(String databaseName : databaseTotalError.keySet()) {
			System.out.println(databaseName + ", " + databaseTotalError.get(databaseName));
		}
		int i = 0;
		for(String databaseName : databaseTotalError.keySet()) {
			results[i++] = databaseTotalError.get(databaseName);
		}
		return results;
	}
	
	private void printExperiments(double[][] allResults) {
		for(int dbIndex = 0; dbIndex < allResults[0].length; dbIndex++) {
			for(int experimentNum = 0; experimentNum < allResults.length; experimentNum++) {
				System.out.print(allResults[experimentNum][dbIndex]);
				if(experimentNum < allResults.length - 1) {
					System.out.print(", ");
				}
			}
			System.out.println();
			
		}
	}
	
	private List<String> getRandomElements(String[] strings, int numValues) {
		List<String> returnVals = Utils.randomize(Arrays.asList(strings), rand);
		return returnVals.subList(0, numValues);
	}
	
	private int getNumRandomElements(int setSize, double sensitivity) {
		// From: Xiaokui Xiao and Yufei Tao. Anatomy: Simple and effective privacy preservation, 2006.
		// b = ceil(|A|*s^(1/(qd+1)))
		return (int)Math.ceil(setSize * Math.pow(sensitivity, 1.0/(QUERY_DIMENSIONALITY+1)));
	}

	public Map<String, Integer> runTestOccupation() {
		return runOccupationsQuery("adult_swapped.sql", 18, 90, "Bachelors", "Female", "White");
	}
	
	public Map<String, Double> runRandomOccupationQueries() throws SQLException {
		//int ageRange = 2;//getNumRandomElements(maxAge-minAge);
		int minQueryAge = MIN_AGE;
		int randomizedMinOffset = MAX_AGE - MIN_AGE - age_selectivity;
		if(randomizedMinOffset > 0) {
			minQueryAge += rand.nextInt(randomizedMinOffset);
		}
		int maxQueryAge = minQueryAge + age_selectivity;
		
		List<String> educationsInQuery = getRandomElements(educations, education_selectivity);
		List<String> sexesInQuery =  getRandomElements(sexes, sex_selectivity);
		List<String> racesInQuery = getRandomElements(races, race_selectivity);
		
		List<Map<String,Integer>> occupationResults = new ArrayList<Map<String,Integer>>(databaseNames.length);
		Map<String, Double> databaseErrors = new HashMap<String, Double>(databaseNames.length);
		
		Map<String,Integer> trueResult = runOccupationsQuery(ORIGINAL_DATABASE, minQueryAge, maxQueryAge, educationsInQuery, sexesInQuery, racesInQuery);
		
		for(int databaseNum = 0; databaseNum < databaseNames.length; databaseNum++) {
			String databaseName = databaseNames[databaseNum];
			Map<String,Integer> result = runOccupationsQuery(databaseName, minQueryAge, maxQueryAge, educationsInQuery, sexesInQuery, racesInQuery);
			occupationResults.add(result);
			databaseErrors.put(databaseName, histogramChiSquaredDistance(trueResult, result));
		}
		return databaseErrors;
	}
	
	public Map<String, Double> runRandomOccupationQueriesSingletons() {
		int minQueryAge = MIN_AGE; // + rand.nextInt(maxAge - minAge);
		int maxQueryAge = MAX_AGE; // + 20;// rand.nextInt(maxAge - minQueryAge);
		String educationQuery = educations[rand.nextInt(educations.length)];
		String sexQuery = sexes[rand.nextInt(sexes.length)];
		String raceQuery = races[rand.nextInt(races.length)];
		
		List<Map<String,Integer>> occupationResults = new ArrayList<Map<String,Integer>>(databaseNames.length);
		Map<String, Double> databaseErrors = new HashMap<String, Double>(databaseNames.length);
		
		Map<String,Integer> trueResult = runOccupationsQuery(ORIGINAL_DATABASE, minQueryAge, maxQueryAge, educationQuery, sexQuery, raceQuery);
		Map<String,Integer> baselineResult = runOccupationsQuery(BASIC_SWAPPED_DATABASE, minQueryAge, maxQueryAge, educationQuery, sexQuery, raceQuery);
		double baseDistance = histogramChiSquaredDistance(trueResult, baselineResult);
		int trueNumberOfResults = 0;
		for(Integer occupationCount : trueResult.values()) {
			trueNumberOfResults += occupationCount;
		}
		
		for(int databaseNum = 0; databaseNum < databaseNames.length; databaseNum++) {
			String databaseName = databaseNames[databaseNum];
			Map<String,Integer> result = runOccupationsQuery(databaseName, minQueryAge, maxQueryAge, educationQuery, sexQuery, raceQuery);
			occupationResults.add(result);
			databaseErrors.put(databaseName, (baseDistance - histogramChiSquaredDistance(trueResult, result))/trueNumberOfResults);
		}
		return databaseErrors;
	}
	
	private double histogramChiSquaredDistance(Map<String, Integer> histogram1, Map<String, Integer> histogram2) {
		// Chi-squared distance
		double totalDistance = 0.0;
		Set<String> allKeys = new HashSet<String>(histogram1.size() + histogram2.size());
		allKeys.addAll(histogram1.keySet());
		allKeys.addAll(histogram2.keySet());
		for(String key : allKeys) {
			int histogram1Value = 0;
			int histogram2Value = 0;
			if(histogram1.containsKey(key)) {
				histogram1Value = histogram1.get(key);
			}
			if(histogram2.containsKey(key)) {
				histogram2Value = histogram2.get(key);
			}
			int valuesDifference = histogram1Value - histogram2Value;
			totalDistance += (valuesDifference*valuesDifference)/(histogram1Value+histogram2Value);
		}
		return 0.5 * totalDistance;
	}
	
	public static void main(String[] args) throws SQLException {
		new MeasureAdultDatabase();
	}
}
