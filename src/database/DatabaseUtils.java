package database;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;

import census.CensusDatabaseUtils;

public class DatabaseUtils {
	public static final int MAX_LINES_TO_PROCESS = 1000;
	public static final int NUM_DATABASE_COLUMNS = 6;
	private static final int[] DATA_INDICIES = {0,1,2,3,17,35};
	// 0 - caseid
	// 1 - age
	// 2 - anc1
	// 3 - anc2
	// 17 - dIncome1
	// 35 - dOccup
	
	public static void writeCSVDataToDatabase(String filename, String databaseFilename, boolean sampleRows, int samplingModifier) throws IOException, SQLException {
		File f = new File(filename);
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line;
		ArrayList<Integer[]> dataToWrite = new ArrayList<Integer[]>();
		while((line = br.readLine()) != null) {
			Integer[] dataRow = processCSVRow(line, sampleRows, samplingModifier);
			if(dataRow == null) {
				continue;
			}
			dataToWrite.add(dataRow);
			if(dataToWrite.size() >= MAX_LINES_TO_PROCESS) {
				CensusDatabaseUtils.fillRows(databaseFilename, dataToWrite);
				dataToWrite.clear();
			}
		}
		if(dataToWrite.size() > 0) {
			CensusDatabaseUtils.fillRows(databaseFilename, dataToWrite);
		}
		br.close();
	}
	
	private static Integer[] processCSVRow(String line, boolean sampleRows, int samplingModifier) {
		Integer[] dataRow = new Integer[NUM_DATABASE_COLUMNS];
		int databaseColumn = 0;
		String[] columns = line.split(","); 
		
		for(Integer i : DATA_INDICIES) {
			int val = 0;
			try{ 
				val = Integer.parseInt(columns[i]);
			} catch (NumberFormatException e) {
				return null;
			}
			dataRow[databaseColumn++] = val;
		}
		// If sampling, skip this row if the index doesn't match modifier
		if(sampleRows && samplingModifier > 0 && dataRow[0] % samplingModifier != 0) {
			return null;
		} 
		return dataRow;
	}

	public static void writeCSVDataToDatabase(String filename, String databaseFilename) throws IOException, SQLException {
		writeCSVDataToDatabase(filename, databaseFilename, false, 0);
	}
}
