package main;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import adult.AdultDataRow;
import adult.AdultDatabaseUtils;
import adult.ClusterAdultRows;
import anonymization.AnonymizationUtils;
import census.CensusDataRow;
import census.CensusDatabaseUtils;
import census.CensusGeneralization;
import utils.DatabaseUtils;

import static utils.Configuration.*;

public class QueryingAnonymizedDataMain {

	public static void main(String[] args) throws IOException, SQLException {
//		Uncomment this section to parse test dataset and write it to SQL database.
//		CensusDatabaseUtils.createSqliteDb("censusTest.sql");
//		DatabaseUtils.writeCSVDataToDatabase("testDataRaw", "censusTest.sql");

//		Uncomment this section to parse full dataset and write it to SQL database.
//		CensusDatabaseUtils.createSqliteDb("census.sql");
//		DatabaseUtils.writeCSVDataToDatabase("USCensus1990Raw.data.txt", "census.sql");

		long curTime = System.currentTimeMillis();
		
		
		//Uncomment this section to read the census database and compute generalizations.
//		Collection<CensusDataRow> censusData = CensusDatabaseUtils.getAllCensusDataRows(INPUT_DATABASE_FILENAME); 
//		System.out.println("Got " + censusData.size() + " rows!");
//		CensusGeneralization censusGeneralization = new CensusGeneralization();
//		censusGeneralization.tryAllGeneralizations(censusData);
//		censusGeneralization.printMinGeneralization();

// 		Uncomment and modify this section to write out generalized results to a SQL database.		
//		Collection<CensusDataRow> generalized = CensusGeneralization.getCensusGeneralizedData(censusData, generalizationLevels);
//		CensusDatabaseUtils.createSqliteDb("censusGeneralized211.sql");
//		CensusDatabaseUtils.writeCensusDataToDatabase("censusGeneralized211.sql", generalized);
		
//		Uncomment this section to parse full adult dataset and write it to SQL database.
//		AdultDatabaseUtils.createSqliteDb("adult.sql");
//		AdultDatabaseUtils.writeCSVDataToDatabase("adult.data", "adult.sql");
		
		
		Collection<AdultDataRow> adultData = AdultDatabaseUtils.getAllAdultDataRows(INPUT_DATABASE_FILENAME);
		new ClusterAdultRows(adultData);
		
		
		System.out.println("DONE took " + ((System.currentTimeMillis() - curTime)/1000) + " seconds.");
	}
}