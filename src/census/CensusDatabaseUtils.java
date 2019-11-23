package census;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

import database.DatabaseUtils;

public class CensusDatabaseUtils {
	private static Connection conn = null;

	public static void createSqliteDb(String databaseFilename) throws SQLException {
		try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + databaseFilename)) {
			if (conn != null) {
				String sql = "CREATE TABLE IF NOT EXISTS census(\n"
						+ "id integer primary key,"
						+ "age integer,"
						+ "anc1 integer,"
						+ "anc2 integer,"
						+ "salary integer,"
						+ "occupation integer);";
				Statement s = conn.createStatement();
				s.execute(sql);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
	}
//	
//	public static void fillSqliteDb(String dataFilename) throws IOException, SQLException {
//		ArrayList<Integer[]> data = DatabaseInput.writeCSVDataToDatabase(dataFilename);
//		fillRows(data);
//	}
//	
	public static void writeCensusDataToDatabase(String databaseFilename, Collection<CensusDataRow> rowsData) throws SQLException {
		ArrayList<Integer[]> dataToWrite = new ArrayList<Integer[]>(DatabaseUtils.MAX_LINES_TO_PROCESS);
		int rowsToWrite = 0;
		for(CensusDataRow dataRow : rowsData) {
			dataToWrite.add(dataRow.raw_data);
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
//		System.out.println("Printing rows: " + rowsData.size());
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
	
	public static CensusDataRow getOneCensusDataRow(String databaseFilename) {
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:" + databaseFilename);
			Statement s = conn.createStatement();
			s.execute("SELECT * FROM census LIMIT 1");
			return parseCensusDataRow(s.getResultSet());
		}  catch (SQLException e) {
            System.out.println(e.getMessage());
        }
		return null;
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
		Integer[] dataInRow = new Integer[DatabaseUtils.NUM_DATABASE_COLUMNS];
		for(int i = 0; i < dataInRow.length; i++) {
			dataInRow[i] = resultSet.getInt(i+1);
		}
		return new CensusDataRow(dataInRow);
	}
	
//	public static void printDatabase(String databaseFilename) {
//		try {
//			conn = DriverManager.getConnection("jdbc:sqlite:" + databaseFilename);
//			Statement s = conn.createStatement();
//			s.execute("SELECT * FROM census");
//			ResultSet r = s.getResultSet();
//			while(r.next()) {
//				int id = r.getInt(1);
//				int age = r.getInt(2);
//				int anc1 = r.getInt(3);
//				int anc2 = r.getInt(4);
//				System.out.println("" + id + ", " + age + ", " + anc1 + ", " + anc2);
//			}
//		}  catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
//	}
}
//DatabaseMetaData meta = conn.getMetaData();