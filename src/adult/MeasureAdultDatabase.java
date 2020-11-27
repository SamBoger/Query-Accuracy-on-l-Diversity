package adult;

import static utils.Configuration.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static adult.AdultDatabaseUtils.*;

public class MeasureAdultDatabase {

	public final int NUM_QUERIES = 1000;
	private Random rand;
	
	private final static String[] databaseNames = {
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
	private final static String[] sexes  = {"Female", "Male"};
	private final static String[] educations = {"Bachelors", "Some-college", "11th", "HS-grad", "Assoc-acdm", "Assoc-voc", "Masters"};
	private final static String[] occupations  = {"?", "Craft-repair", "Other-service", "Sales", "Exec-managerial", "Prof-specialty", "Handlers-cleaners", "Machine-op-inspct", "Adm-clerical", "Transport-moving", "Armed-Forces"};
	private final static String[] races = {"White", "Asian-Pac-Islander", "Black"};
	
	// All the values present in the database.
//	private final static String[] educations = {"Bachelors", "Some-college", "11th", "HS-grad", "Prof-school", "Assoc-acdm", "Assoc-voc", "9th", "7th-8th", "12th", "Masters", "1st-4th", "10th", "Doctorate", "5th-6th", "Preschool"};
//	private final static String[] occupations  = {"Tech-support", "Craft-repair", "Other-service", "Sales", "Exec-managerial", "Prof-specialty", "Handlers-cleaners", "Machine-op-inspct", "Adm-clerical", "Farming-fishing", "Transport-moving", "Priv-house-serv", "Protective-serv", "Armed-Forces"};
//	private final static String[] races = {"White", "Asian-Pac-Islander", "Amer-Indian-Eskimo", "Other", "Black"};
	
	private final int minAge = 18;
	private final int maxAge = 90;
	
	public MeasureAdultDatabase() {
		this.rand = new Random();
		
		Map<String, Double> databaseTotalError = new HashMap<String, Double>(databaseNames.length);
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
			System.out.println(databaseName + " total error: " + databaseTotalError.get(databaseName));
		}
	}
	
	public Map<String, Integer> runTestOccupation() {
		return runOccupationsQuery("adult_swapped.sql", 18, 90, "Bachelors", "Female", "White");
	}
	
	public Map<String, Double> runRandomOccupationQueries() {
		int minQueryAge = minAge; // + rand.nextInt(maxAge - minAge);
		int maxQueryAge = maxAge; // + 20;// rand.nextInt(maxAge - minQueryAge);
		String educationQuery = educations[rand.nextInt(educations.length)];
		String sexQuery = sexes[rand.nextInt(sexes.length)];
		String raceQuery = races[rand.nextInt(races.length)];
		// System.out.println("SELECT occupation, COUNT(*) as total FROM adult WHERE age > " + minQueryAge + " and age < " + maxQueryAge + "  and education = " + educationQuery + " and sex  = " + sexQuery + " and race = " + raceQuery + " GROUP BY occupation");
		
		List<Map<String,Integer>> occupationResults = new ArrayList<Map<String,Integer>>(databaseNames.length);
		Map<String, Double> databaseErrors = new HashMap<String, Double>(databaseNames.length);
		
		Map<String,Integer> trueResult = runOccupationsQuery(ORIGINAL_DATABASE, minQueryAge, maxQueryAge, educationQuery, sexQuery, raceQuery);
		Map<String,Integer> baselineResult = runOccupationsQuery(BASIC_SWAPPED_DATABASE, minQueryAge, maxQueryAge, educationQuery, sexQuery, raceQuery);
		double baseDistance = histogramChiSquaredDistance(trueResult, baselineResult);
		
		for(int databaseNum = 0; databaseNum < databaseNames.length; databaseNum++) {
			String databaseName = databaseNames[databaseNum];
			Map<String,Integer> result = runOccupationsQuery(databaseName, minQueryAge, maxQueryAge, educationQuery, sexQuery, raceQuery);
			occupationResults.add(result);
			databaseErrors.put(databaseName, baseDistance - histogramChiSquaredDistance(trueResult, result));
		}
		return databaseErrors;
	}
	
	public int[] runRandomCountQueries() {
		int minQueryAge = minAge; // + rand.nextInt(maxAge - minAge);
		int maxQueryAge = maxAge; // + 20;// rand.nextInt(maxAge - minQueryAge);
		String educationQuery = educations[rand.nextInt(educations.length)];
		String sexQuery = sexes[rand.nextInt(sexes.length)];
		String raceQuery = races[rand.nextInt(races.length)];
		String occupationQuery = occupations[rand.nextInt(occupations.length)];
		// System.out.println("SELECT COUNT(*) as total FROM adult WHERE age > " + minQueryAge + " and age < " + maxQueryAge + "  and education = " + educationQuery + " and sex  = " + sexQuery + " and race = " + raceQuery + " and occupation = " + occupationQuery);
		
		int[] results = new int[databaseNames.length];
		for(int databaseNum = 0; databaseNum < databaseNames.length; databaseNum++) {
			results[databaseNum] = runCountQuery(databaseNames[databaseNum], minQueryAge, maxQueryAge, educationQuery, sexQuery, raceQuery, occupationQuery);
		}
		return results;
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
	
	public static void main(String[] args) {
		new MeasureAdultDatabase();
	}
}
