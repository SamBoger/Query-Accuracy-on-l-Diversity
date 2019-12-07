package main;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

import anonymization.AnonymizationUtils;
import census.CensusDataRow;
import census.CensusDatabaseUtils;
import census.CensusGeneralization;
import database.DatabaseUtils;

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

//		Collection<CensusDataRow> censusData = CensusDatabaseUtils.getAllCensusDataRows("census.sql"); 
//		System.out.println("Got " + censusData.size() + " rows!");
//		String[] quasiIdentifiers = {"class", "ancestry"};
//		String sensitiveValue = "salary";
//		int kAnon = AnonymizationUtils.measureKAnonymity(CensusGeneralization.getCensusEquivalenceClasses(censusData, quasiIdentifiers, sensitiveValue));
//		System.out.println("K-Anonymity: " + kAnon);
//		CensusGeneralization.tryAllGeneralizations(censusData);
		
		
//		AnonymizationUtils.analyzeCensusData(censusData);
//		
//		Integer[] generalizationLevels = {1,0,0};
//		Collection<CensusDataRow> generalizedData = CensusGeneralization.getCensusGeneralizedData(censusData, generalizationLevels);
//		AnonymizationUtils.analyzeCensusData(generalizedData);

//		DatabaseConnection.createSqliteDb("censusGeneralized100.sql");
//		DatabaseConnection.writeCensusDataToDatabase("censusGeneralized100.sql", generalizedData);
		
		System.out.println("DONE");
//		DatabaseConnection.printDatabase();
	}
}