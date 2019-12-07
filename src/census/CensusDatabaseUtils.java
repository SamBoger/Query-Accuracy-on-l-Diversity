package census;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

import database.DatabaseUtils;
import utils.Configuration;

public class CensusDatabaseUtils {
	private static Connection conn = null;
	
	// 12 AGE : Age : age
	// 35 ANCSTRY1 : Race : ancestry
	// 54 CLASS : Work-class : class
	// 86 OCCUP : Occupation : occupation
	// 89 POB : Country : country
	// 104 RPINCOME : Salary-class : salary
	// 107 RSPOUSE : Marital : marital
	// 112 SEX : Gender : sex
	// 122 YEARSCH : Education : education
	public static void createSqliteDb(String databaseFilename) throws SQLException {
		try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + databaseFilename)) {
			if (conn != null) {
				StringBuilder sqlQuery = new StringBuilder();
				sqlQuery.append("CREATE TABLE IF NOT EXISTS census(\n");
				for(int i = 0; i < Configuration.DATA_SPECIFICATION.length; i++) {
					sqlQuery.append(Configuration.DATA_SPECIFICATION[i].label).append(" integer");
					if(i < Configuration.DATA_SPECIFICATION.length-1) {
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
		ArrayList<Integer[]> dataToWrite = new ArrayList<Integer[]>(DatabaseUtils.MAX_LINES_TO_PROCESS);
		int rowsToWrite = 0;
		for(CensusDataRow dataRow : rowsData) {
			Integer[] dataRowValues = new Integer[dataRow.census_attributes.length];
			for(int i = 0; i < dataRow.census_attributes.length; i++) {
				dataRowValues[i] = dataRow.census_attributes[i].attribute_value;
			}
			dataToWrite.add(dataRowValues);
			rowsToWrite++;
			if(rowsToWrite >= DatabaseUtils.MAX_LINES_TO_PROCESS) {
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
		CensusDataAttribute[] dataInRow = new CensusDataAttribute[Configuration.DATA_SPECIFICATION.length];
		for(int i = 0; i < dataInRow.length; i++) {
			dataInRow[i] = getCensusAttribute(Configuration.DATA_SPECIFICATION[i].label, resultSet.getInt(i+1));
		}
		CensusDataRow censusRow = new CensusDataRow(dataInRow);
		if(censusRow.isValid()) {
			return censusRow;
		}
		return null;
	}

	private static CensusDataAttribute getCensusAttribute(String label, int value) {
		switch(label) {
			case "age":
				return new AgeAttribute(value, label);
			case "ancestry":
				return new AncestryAttribute(value, label);
			case "class":
				return new ClassAttribute(value, label);
			default:
				return new GenericAttribute(value, label);
		}
	}
}