package main;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

import adult.AdultDataRow;
import adult.AdultDatabaseUtils;
import adult.ClusterAdultRows;

import static utils.Configuration.*;

public class QueryingAnonymizedDataMain {

	public static void main(String[] args) throws IOException, SQLException {

		long curTime = System.currentTimeMillis();

//		Uncomment this section to parse full adult dataset and write it to SQL database.
//		AdultDatabaseUtils.createSqliteDb("adult.sql");
//		AdultDatabaseUtils.writeCSVDataToDatabase("adult.data", "adult.sql");
		
		Collection<AdultDataRow> adultData = AdultDatabaseUtils.getAllAdultDataRows(INPUT_DATABASE_FILENAME);
		ClusterAdultRows algorithm = new ClusterAdultRows(adultData);
		System.out.println("Clustering done. Took " + ((System.currentTimeMillis() - curTime)/1000) + " seconds.");
		
		curTime = System.currentTimeMillis();	
//		AdultDatabaseUtils.writeClustersNoSwapping("adult_clusters.sql", algorithm.adultDataRowClusters);
		AdultDatabaseUtils.writeClustersSwapped(CLUSTER_SWAPPED_DATABASE_NAME, algorithm.adultDataRowClusters);
		
		System.out.println("Database writing done. Took " + ((System.currentTimeMillis() - curTime)/1000) + " seconds.");
	}
}