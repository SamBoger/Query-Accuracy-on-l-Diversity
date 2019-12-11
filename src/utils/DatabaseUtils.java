package utils;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;

import census.CensusDatabaseUtils;
import static utils.Configuration.*;

public class DatabaseUtils {
	public static final int MAX_LINES_TO_PROCESS = 1000;
	
	
	public static void writeCSVDataToDatabase(String filename, String databaseFilename, String delim, boolean sampleRows, int samplingModifier) throws IOException, SQLException {
		File f = new File(filename);
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line;
		ArrayList<Integer[]> dataToWrite = new ArrayList<Integer[]>();
		while((line = br.readLine()) != null) {
			Integer[] dataRow = processCSVRow(line, delim, sampleRows, samplingModifier);
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
	
	private static Integer[] processCSVRow(String line, String delim, boolean sampleRows, int samplingModifier) {
		Integer[] dataRow = new Integer[DATA_SPECIFICATION.length];
		int databaseColumn = 0;
		String[] columns = line.split(delim); 
		
		for(int i = 0; i < DATA_SPECIFICATION.length; i++) {
			int index = DATA_SPECIFICATION[i].csvColumnNum;
			if (index >= columns.length) {
				return null;
			}
			int val = 0;
			try{
				val = Integer.parseInt(columns[index]);
			} catch (NumberFormatException e) {
				return null;
			}
			dataRow[databaseColumn++] = val;
		}
		if(sampleRows && samplingModifier > 0 && dataRow[0] % samplingModifier != 0) {
			return null;
		}
		return dataRow;
	}
	
	public static void writeCSVDataToDatabase(String filename, String databaseFilename, boolean sampled, int samplingModifier) throws IOException, SQLException {
		writeCSVDataToDatabase(filename, databaseFilename, "\\s+", sampled, samplingModifier);
	}
	
	public static void writeCSVDataToDatabase(String filename, String databaseFilename) throws IOException, SQLException {
		writeCSVDataToDatabase(filename, databaseFilename, false, 0);
	}
}
