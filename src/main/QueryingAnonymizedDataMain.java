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
		CensusDatabaseUtils.createRawDataSqliteDb("censusRaw.sql");
		DatabaseUtils.writeCSVRawDataToDatabase("USCensus1990Raw.data.txt", "censusRaw.sql");
		
		//		DatabaseConnection.createSqliteDb("census.sql");
//		DatabaseInput.writeCSVDataToDatabase("USCensus1990.data.txt", "census.sql");

//		DatabaseConnection.createSqliteDb("censusOnePercent.sql");
//		DatabaseInput.writeCSVDataToDatabase("USCensus1990.data.txt", "censusOnePercent.sql", true, 100);

//		Collection<CensusDataRow> censusData = CensusDatabaseUtils.getAllCensusDataRows("censusRawTest.sql"); 
//		System.out.println("Got " + censusData.size() + " rows!");
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