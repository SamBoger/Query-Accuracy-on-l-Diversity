package census;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

import censusAttributes.AgeAttribute;
import censusAttributes.AncestryAttribute;
import censusAttributes.CensusDataAttribute;
import censusAttributes.ClassAttribute;
import censusAttributes.CountryAttribute;
import censusAttributes.GenericAttribute;
import censusAttributes.MaritalAttribute;
import censusAttributes.OccupationAttribute;
import censusAttributes.RaceAttribute;
import censusAttributes.SalaryAttribute;
import censusAttributes.SexAttribute;

import static utils.Configuration.*;

public class CensusDatabaseUtils {
	private static Connection conn = null;
	
	public static void createSqliteDb(String databaseFilename) throws SQLException {
		try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + databaseFilename)) {
			if (conn != null) {
				StringBuilder sqlQuery = new StringBuilder();
				sqlQuery.append("CREATE TABLE IF NOT EXISTS census(\n");
				for(int i = 0; i < CENSUS_DATA_SPECIFICATION.length; i++) {
					sqlQuery.append(CENSUS_DATA_SPECIFICATION[i].label).append(" integer");
					if(i < CENSUS_DATA_SPECIFICATION.length-1) {
						sqlQuery.append(",");
					}
				}
				sqlQuery.append(");");
				Statement s = conn.createStatement();
				s.execute(sqlQuery.toString());
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
	}

	public static void writeCensusDataToDatabase(String databaseFilename, Collection<CensusDataRow> rowsData) throws SQLException {
		ArrayList<Integer[]> dataToWrite = new ArrayList<Integer[]>(CensusDatabaseUtils.MAX_LINES_TO_PROCESS);
		int rowsToWrite = 0;
		for(CensusDataRow dataRow : rowsData) {
			Integer[] dataRowValues = new Integer[CENSUS_DATA_SPECIFICATION.length];
			for(int i = 0; i < CENSUS_DATA_SPECIFICATION.length; i++) {
				dataRowValues[i] = dataRow.census_attributes.get(CENSUS_DATA_SPECIFICATION[i].label).attribute_value;
			}
			dataToWrite.add(dataRowValues);
			rowsToWrite++;
			if(rowsToWrite >= CensusDatabaseUtils.MAX_LINES_TO_PROCESS) {
				fillRows(databaseFilename, dataToWrite);
				dataToWrite.clear();
				rowsToWrite = 0;
			}
		}
		if(rowsToWrite > 0) {
			fillRows(databaseFilename, dataToWrite);
		}
	}
	
	public static void fillRows(String databaseFilename, ArrayList<Integer[]> rowsData) throws SQLException {
		PreparedStatement ps = null;
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:" + databaseFilename);
			conn.setAutoCommit(false);
			if (conn != null) {
				if(rowsData == null || rowsData.size() == 0) {
					return;
				}
				int numColumns = rowsData.get(0).length;
				StringBuilder sb = new StringBuilder();
				sb.append("INSERT INTO census VALUES (");
				for(int i = 0; i < numColumns; i++) {
					if(i < numColumns-1) {
						sb.append("?, ");
					} else {
						sb.append("?");
					}
				}
				sb.append(");");
				ps = conn.prepareStatement(sb.toString());
				for(int row = 0; row < rowsData.size(); row ++) {
					for (int i = 0; i < rowsData.get(row).length; i++) {
						ps.setInt(i+1, rowsData.get(row)[i]);
					}
					ps.executeUpdate();
				}
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
        	if(ps!=null) {
        		ps.close();
        	}
        	conn.commit();
        	conn.setAutoCommit(true);
        }
	}

	
	public static Collection<CensusDataRow> getAllCensusDataRows(String databaseFilename) {
		Collection<CensusDataRow> dataRows = new ArrayList<CensusDataRow>();
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:" + databaseFilename);
			Statement queryStatement = conn.createStatement();
			queryStatement.execute("SELECT * FROM census");
			ResultSet resultSet = queryStatement.getResultSet();
			while(resultSet.next()) {
				CensusDataRow dataRow = parseCensusDataRow(resultSet);				
				if(dataRow != null) {
					dataRows.add(dataRow);
				}
			}
		}  catch (SQLException e) {
            System.out.println(e.getMessage());
        }
		return dataRows;
	}
	
	private static CensusDataRow parseCensusDataRow(ResultSet resultSet) throws SQLException {
		ArrayList<CensusDataAttribute> dataInRow = new ArrayList<CensusDataAttribute>(CENSUS_DATA_SPECIFICATION.length);
		for(int i = 0; i < CENSUS_DATA_SPECIFICATION.length; i++) {
			dataInRow.add(getCensusAttribute(CENSUS_DATA_SPECIFICATION[i].label, resultSet.getInt(i+1)));
		}
		CensusDataRow censusRow = new CensusDataRow(dataInRow);
		if(censusRow.isValid()) {
			return censusRow;
		}
		return null;
	}

	private static CensusDataAttribute getCensusAttribute(String label, int value) {
		switch(label) {
			case AGE_LABEL:
				return new AgeAttribute(value, label);
			case ANCESTRY_LABEL:
				return new AncestryAttribute(value, label);
			case CLASS_LABEL:
				return new ClassAttribute(value, label);
			case COUNTRY_LABEL:
				return new CountryAttribute(value, label);
			case MARITAL_LABEL:
				return new MaritalAttribute(value, label);
			case OCCUPATION_LABEL:
				return new OccupationAttribute(value, label);
			case RACE_LABEL:
				return new RaceAttribute(value, label);
			case SEX_LABEL:
				return new SexAttribute(value, label);
			case SALARY_LABEL:
				return new SalaryAttribute(value, label);
			default:
				return new GenericAttribute(value, label);
		}
	}
	
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
		Integer[] dataRow = new Integer[CENSUS_DATA_SPECIFICATION.length];
		int databaseColumn = 0;
		String[] columns = line.split(delim); 
		
		for(int i = 0; i < CENSUS_DATA_SPECIFICATION.length; i++) {
			int index = CENSUS_DATA_SPECIFICATION[i].csvColumnNum;
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