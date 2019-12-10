package main;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import anonymization.AnonymizationUtils;
import census.CensusDataRow;
import census.CensusDatabaseUtils;
import census.CensusGeneralization;
import database.DatabaseUtils;

import static utils.Configuration.*;

public class QueryingAnonymizedDataMain {

	public static void main(String[] args) throws IOException, SQLException {
//		CensusDatabaseUtils.createSqliteDb("censusTest.sql");
//		DatabaseUtils.writeCSVDataToDatabase("testDataRaw", "censusTest.sql");
//
//		Collection<CensusDataRow> censusData = CensusDatabaseUtils.getAllCensusDataRows("censusTest.sql"); 
//		System.out.println("Got " + censusData.size() + " rows!");
//		CensusGeneralization.tryAllGeneralizations(censusData);
		
		
//		CensusDatabaseUtils.createSqliteDb("census.sql");
//		DatabaseUtils.writeCSVDataToDatabase("USCensus1990Raw.data.txt", "census.sql");

		Collection<CensusDataRow> censusData = CensusDatabaseUtils.getAllCensusDataRows(INPUT_DATABASE_FILENAME); 
		System.out.println("Got " + censusData.size() + " rows!");
//		String[] quasiIdentifiers = {"class", "ancestry"};
//		String sensitiveValue = "salary";
//		int kAnon = AnonymizationUtils.measureKAnonymity(CensusGeneralization.getCensusEquivalenceClasses(censusData, quasiIdentifiers, sensitiveValue));
//		System.out.println("K-Anonymity: " + kAnon);
//		int[] good = {0,4,4};
//		int[] bad1 = {0,4,3};
//		int[] bad2 = {0,3,4};
		CensusGeneralization cenGen = new CensusGeneralization();
//		System.out.println(cenGen.tryGeneralization(censusData, good));
//		System.out.println(cenGen.tryGeneralization(censusData, bad1));
//		System.out.println(cenGen.tryGeneralization(censusData, bad2));
		cenGen.tryAllGeneralizations(censusData);
		cenGen.printMinGeneralization();
//		Map<String, Integer> generalizationLevels = new HashMap<String, Integer>();
//		generalizationLevels.put("age", 2);
//		generalizationLevels.put("ancestry", 1);
//		generalizationLevels.put("class", 1);
//		Collection<CensusDataRow> generalized = CensusGeneralization.getCensusGeneralizedData(censusData, generalizationLevels);
//		CensusDatabaseUtils.createSqliteDb("censusGeneralized211.sql");
//		CensusDatabaseUtils.writeCensusDataToDatabase("censusGeneralized211.sql", generalized);
		
//		AnonymizationUtils.analyzeCensusData(censusData);
//		
//		Integer[] generalizationLevels = {1,0,0};
//		Collection<CensusDataRow> generalizedData = CensusGeneralization.getCensusGeneralizedData(censusData, generalizationLevels);
//		AnonymizationUtils.analyzeCensusData(generalizedData);

//		CensusDatabaseUtils.createSqliteDb("censusGeneralized100.sql");
//		DatabaseConnection.writeCensusDataToDatabase("censusGeneralized100.sql", generalizedData);
		
		System.out.println("DONE");
	}
}