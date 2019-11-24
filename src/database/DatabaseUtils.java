package database;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;

import census.CensusDatabaseUtils;

public class DatabaseUtils {
	public static final int MAX_LINES_TO_PROCESS = 1000;
	public static final int NUM_DATABASE_COLUMNS = 6;
	public static final int NUM_RAW_DATABASE_COLUMNS = 8;
	// 0 - caseid
	// 1 - age
	// 2 - anc1
	// 3 - anc2
	// 17 - dIncome1
	// 35 - dOccup
	private static final int[] DATA_INDICIES = {0,1,2,3,17,35};
	
	// 12 AGE : Age
	// 35 ANCSTRY1 : Race
	// 54 CLASS : Work-class
	// 86 OCCUP : Occupation
	// 89 POB : Country
	// 104 RPINCOME : Salary-class
	// 107 RSPOUSE : Marital
	// 112 SEX : Gender
	// 122 YEARSCH : Education
	private static final int[] RAW_DATA_INDICIES = {12, 35, 54, 86, 89, 104, 107, 112, 122};
	
	
	public static void writeCSVDataToDatabase(String filename, String databaseFilename, int[] indicies, String delim, boolean sampleRows, int samplingModifier) throws IOException, SQLException {
		File f = new File(filename);
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line;
		ArrayList<Integer[]> dataToWrite = new ArrayList<Integer[]>();
		while((line = br.readLine()) != null) {
			Integer[] dataRow = processCSVRow(line, indicies, delim, sampleRows, samplingModifier);
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
	
	private static Integer[] processCSVRow(String line, int[] indicies, String delim, boolean sampleRows, int samplingModifier) {
		Integer[] dataRow = new Integer[indicies.length];
		int databaseColumn = 0;
		String[] columns = line.split(delim); 
		
		for(Integer i : indicies) {
			if (i >= columns.length) {
				return null;
			}
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
	

	public static void writeCSVProcessedDataToDatabase(String filename, String databaseFilename) throws IOException, SQLException {
		writeCSVDataToDatabase(filename, databaseFilename, DATA_INDICIES, ",", false, 0);
	}
	
	public static void writeCSVProcessedDataToDatabase(String filename, String databaseFilename, boolean sampled, int samplingModifier) throws IOException, SQLException {
		writeCSVProcessedDataToDatabase(filename, databaseFilename, sampled, samplingModifier);
	}
	
	public static void writeCSVRawDataToDatabase(String filename, String databaseFilename, boolean sampled, int samplingModifier) throws IOException, SQLException {
		writeCSVDataToDatabase(filename, databaseFilename, RAW_DATA_INDICIES, "\\s+", sampled, samplingModifier);
	}
	
	public static void writeCSVRawDataToDatabase(String filename, String databaseFilename) throws IOException, SQLException {
		writeCSVRawDataToDatabase(filename, databaseFilename, false, 0);
	}
}
