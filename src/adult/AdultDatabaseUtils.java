package adult;

import static utils.Configuration.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import adultAttributes.*;

public class AdultDatabaseUtils {
	private static Connection conn = null;
	public static final int MAX_LINES_TO_PROCESS = 1000;
	private static Random rand = new Random(0);
	
	public static void createSqliteDb(String databaseFilename) throws SQLException {
		try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + databaseFilename)) {
			if (conn != null) {
				StringBuilder sqlQuery = new StringBuilder();
				sqlQuery.append("CREATE TABLE IF NOT EXISTS adult(\n");
				for(int i = 0; i < ADULT_DATA_SPECIFICATION.length; i++) {
					String type = ADULT_DATA_SPECIFICATION[i].isInt ? " integer" : " string";
					sqlQuery.append(ADULT_DATA_SPECIFICATION[i].label).append(type);
					if(i < ADULT_DATA_SPECIFICATION.length-1) {
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
	
	public static void writeCSVDataToDatabase(String filename, String databaseFilename, String delim, boolean sampleRows, int samplingModifier) throws IOException, SQLException {
		File f = new File(filename);
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line;
		ArrayList<Object[]> dataToWrite = new ArrayList<Object[]>();
		while((line = br.readLine()) != null) {
			Object[] dataRow = processCSVRow(line, delim, sampleRows, samplingModifier);
			if(dataRow == null) {
				continue;
			}
			dataToWrite.add(dataRow);
			if(dataToWrite.size() >= MAX_LINES_TO_PROCESS) {
				AdultDatabaseUtils.fillRows(databaseFilename, dataToWrite);
				dataToWrite.clear();
			}
		}
		if(dataToWrite.size() > 0) {
			AdultDatabaseUtils.fillRows(databaseFilename, dataToWrite);
		}
		br.close();
	}
	
	public static void fillRows(String databaseFilename, ArrayList<Object[]> dataToWrite) throws SQLException {
		PreparedStatement ps = null;
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:" + databaseFilename);
			conn.setAutoCommit(false);
			if (conn != null) {
				if(dataToWrite == null || dataToWrite.size() == 0) {
					return;
				}
				int numColumns = dataToWrite.get(0).length;
				StringBuilder sb = new StringBuilder();
				sb.append("INSERT INTO adult VALUES (");
				for(int i = 0; i < numColumns; i++) {
					if(i < numColumns-1) {
						sb.append("?, ");
					} else {
						sb.append("?");
					}
				}
				sb.append(");");
				ps = conn.prepareStatement(sb.toString());
				for(int row = 0; row < dataToWrite.size(); row ++) {
					for (int i = 0; i < dataToWrite.get(row).length; i++) {
						Object toWrite = dataToWrite.get(row)[i];
						if(toWrite instanceof Integer) {
							ps.setInt(i+1, (Integer)dataToWrite.get(row)[i]);
						} else if(toWrite instanceof String) {
							ps.setString(i+1, (String)dataToWrite.get(row)[i]);
						}
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

	private static Object[] processCSVRow(String line, String delim, boolean sampleRows, int samplingModifier) {
		Object[] dataRow = new Object[ADULT_DATA_SPECIFICATION.length];
		int databaseColumn = 0;
		String[] columns = line.split(delim); 
		
		for(int i = 0; i < ADULT_DATA_SPECIFICATION.length; i++) {
			int index = ADULT_DATA_SPECIFICATION[i].csvColumnNum;
			if (index >= columns.length) {
				return null;
			}
			Object val = columns[index];
			try{
				val = Integer.parseInt(columns[index]);
			} catch (NumberFormatException e) {
			}
			if(val == null) {
				return null;
			}
			dataRow[databaseColumn++] = val;
		}
//		if(sampleRows && samplingModifier > 0 && dataRow[0] % samplingModifier != 0) {
//	 		return null;
//		}
		return dataRow;
	}
	
	public static Collection<AdultDataRow> getAllAdultDataRows(String databaseFilename) {
		Collection<AdultDataRow> dataRows = new ArrayList<AdultDataRow>();
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:" + databaseFilename);
			Statement queryStatement = conn.createStatement();
			queryStatement.execute("SELECT * FROM adult");
			ResultSet resultSet = queryStatement.getResultSet();
			while(resultSet.next()) {
				AdultDataRow dataRow = parseAdultDataRow(resultSet);				
				if(dataRow != null) {
					dataRows.add(dataRow);
				}
			}
		}  catch (SQLException e) {
            System.out.println(e.getMessage());
        }
		return dataRows;
	}
	
	private static AdultDataRow parseAdultDataRow(ResultSet resultSet) throws SQLException {
		ArrayList<AdultDataAttribute> dataInRow = new ArrayList<AdultDataAttribute>(ADULT_DATA_SPECIFICATION.length);
		for(int i = 0; i < ADULT_DATA_SPECIFICATION.length; i++) {
			dataInRow.add(getAdultAttribute(ADULT_DATA_SPECIFICATION[i].label, resultSet.getObject(i+1)));
		}
		AdultDataRow adultRow = new AdultDataRow(dataInRow);
		if(adultRow.isValid()) {
			return adultRow;
		}
		return null;
	}

	private static AdultDataAttribute getAdultAttribute(String label, Object value) {
		switch(label) {
			case AGE_LABEL:
				return new AgeAttribute(value, label);
			case COUNTRY_LABEL:
				return new CountryAttribute(value, label);
			case EDUCATION_LABEL:
				return new EducationAttribute(value, label);
			case FIFTY_THOUSAND_LABEL:
				return new FiftyThousandAttribute(value, label);
			case MARITAL_LABEL:
				return new MaritalAttribute(value, label);
			case OCCUPATION_LABEL:
				return new OccupationAttribute(value, label);
			case RACE_LABEL:
				return new RaceAttribute(value, label);
			case SEX_LABEL:
				return new SexAttribute(value, label);
			case WORK_CLASS_LABEL:
				return new WorkClassAttribute(value, label);
			default:
				return null;
		}
	}
	
	public static void writeAdultDataToDatabase(String databaseFilename, Collection<AdultDataRow> rowsData) throws SQLException {
		ArrayList<Object[]> dataToWrite = new ArrayList<Object[]>(AdultDatabaseUtils.MAX_LINES_TO_PROCESS);
		int rowsToWrite = 0;
		for(AdultDataRow dataRow : rowsData) {
			Object[] dataRowValues = new Object[ADULT_DATA_SPECIFICATION.length];
			for(int i = 0; i < ADULT_DATA_SPECIFICATION.length; i++) {
				dataRowValues[i] = dataRow.adult_attributes.get(ADULT_DATA_SPECIFICATION[i].label).attribute_value;
			}
			dataToWrite.add(dataRowValues);
			rowsToWrite++;
			if(rowsToWrite >= AdultDatabaseUtils.MAX_LINES_TO_PROCESS) {
				fillRows(databaseFilename, dataToWrite);
				dataToWrite.clear();
				rowsToWrite = 0;
			}
		}
		if(rowsToWrite > 0) {
			fillRows(databaseFilename, dataToWrite);
		}
	}
	
	public static Map<String, Integer> runOccupationsQuery(String databaseFilename, int minAge, int maxAge, String education, String sex, String race) {
		Map<String, Integer> occupationCounts = new HashMap<String, Integer>();
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:" + databaseFilename);
			PreparedStatement ps = conn.prepareStatement("SELECT occupation, COUNT(*) as total FROM adult WHERE age > ? and age < ?  and education = ? and sex  = ? and race = ? GROUP BY occupation");
			ps.setInt(1, minAge);
			ps.setInt(2, maxAge);
			ps.setString(3, education);
			ps.setString(4, sex);
			ps.setString(5, race);
			ps.execute();
			
			ResultSet resultSet = ps.getResultSet();
			while(resultSet.next()) {
				
				String occupation = resultSet.getString("occupation");
				int total = resultSet.getInt("total");
				occupationCounts.put(occupation, total);
			}
		}  catch (SQLException e) {
            System.out.println(e.getMessage());
        }
		return occupationCounts;
	}
	
	public static int runCountQuery(String databaseFilename, int minAge, int maxAge, String education, String sex, String race, String occupation) {
		int returnVal = -1;
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:" + databaseFilename);
			PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) as total FROM adult WHERE age > ? and age < ?  and education = ? and sex  = ? and race = ? and occupation = ?");
			ps.setInt(1, minAge);
			ps.setInt(2, maxAge);
			ps.setString(3, education);
			ps.setString(4, sex);
			ps.setString(5, race);
			ps.setString(6, occupation);
			ps.execute();
			
			ResultSet resultSet = ps.getResultSet();
			if(resultSet.next()) {
				returnVal = resultSet.getInt("total");
			}
		}  catch (SQLException e) {
            System.out.println(e.getMessage());
        }
		return returnVal;
	}
	
	public static void writeCSVDataToDatabase(String filename, String databaseFilename, boolean sampled, int samplingModifier) throws IOException, SQLException {
		writeCSVDataToDatabase(filename, databaseFilename, ",\\s*", sampled, samplingModifier);
	}
	
	public static void writeCSVDataToDatabase(String filename, String databaseFilename) throws IOException, SQLException {
		writeCSVDataToDatabase(filename, databaseFilename, false, 0);
	}

	public static void writeClustersNoSwapping(String filename, List<AdultDataRowCluster> adultDataRowClusters) throws SQLException {
		createSqliteDb(filename);
		for(AdultDataRowCluster cluster : adultDataRowClusters) {
			writeAdultDataToDatabase(filename, cluster.rows);
		}
	}

	public static void writeClustersSwapped(String filename, List<AdultDataRowCluster> adultDataRowClusters) throws SQLException {
		createSqliteDb(filename);
		int numRows = 0;
		for(AdultDataRowCluster cluster : adultDataRowClusters) {
			List<AdultDataRow> swappedCluster = cluster.getSwappedRows(rand);
			writeAdultDataToDatabase(filename, swappedCluster);
			numRows += swappedCluster.size();
			
		}
		System.out.println("Wrote " + numRows + " swapped rows");
	}
}
