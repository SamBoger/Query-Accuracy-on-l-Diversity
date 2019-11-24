package census;

import java.util.HashMap;
import java.util.Map;

import anonymization.QuasiIdentifier;
import database.DatabaseUtils;
import generalization.AgeGeneralization;
import generalization.AncestryOneGeneralization;
import generalization.AncestryTwoGeneralization;

public class CensusDataRow {
	public Map<String, Integer> census_data = null;
	public Integer[] raw_data = null;
	public String[] quasiIdentifiers = null;
	public static final int NUM_ATTRIBUTES = 6;
	
	public CensusDataRow(Integer[] data) {
		if(data.length != NUM_ATTRIBUTES) {
			System.err.println("Incorrect data length for census data row, got " + data.length + " expected " + NUM_ATTRIBUTES);
		}
		raw_data = data;
		census_data = new HashMap<String, Integer>(data.length);
		census_data.put("id", data[0]);
		census_data.put("Age", data[1]);
		census_data.put("Ancestry1", data[2]);
		census_data.put("Ancestry2", data[3]);
		census_data.put("Salary", data[4]);
		census_data.put("Occupation", data[5]);
	}
	
	// Census website : Paper : database
	// 12 AGE : Age : age
	// 35 ANCSTRY1 : Race : ancestry
	// 54 CLASS : Work-class : class
	// 86 OCCUP : Occupation : occupation
	// 89 POB : Country : country
	// 104 RPINCOME : Salary-class : salary
	// 107 RSPOUSE : Marital : marital
	// 112 SEX : Gender : sex
	// 122 YEARSCH : Education : education
	public CensusDataRow(Integer[] data, boolean raw) {
		if(data.length != DatabaseUtils.NUM_RAW_DATABASE_COLUMNS) {
			System.err.println("Incorrect data length for census data row, got " + data.length + " expected " + DatabaseUtils.NUM_RAW_DATABASE_COLUMNS);
		}
		raw_data = data;
		census_data = new HashMap<String, Integer>(data.length);
		census_data.put("age", data[0]);
		census_data.put("ancestry", data[1]);
		census_data.put("class", data[2]);
		census_data.put("occupation", data[3]);
		census_data.put("country", data[4]);
		census_data.put("salary", data[5]);
		census_data.put("marital", data[6]);
		census_data.put("sex", data[7]);
		census_data.put("education", data[8]);
	}
	
	public QuasiIdentifier getQuasiIdentifier(String[] quasiIdentifierKeys) {
		Map<String, Integer> quasiIdentifierData = new HashMap<String, Integer>(quasiIdentifierKeys.length);
		for(String s : quasiIdentifierKeys) {
			Integer data = census_data.get(s);
			if(data==null) {
				System.err.println("Error, quasiIdentifier key " + s + " has null data.");
				return null;
			}
			quasiIdentifierData.put(s, data);
		}
		return new QuasiIdentifier(quasiIdentifierData);
	}
	
	public Integer getSensitiveValue(String sensitiveValueKey) {
		Integer data = census_data.get(sensitiveValueKey);
		if(data == null) {
			System.err.println("Error, sensitiveValueKey " + sensitiveValueKey + " has null data.");
			return null;
		}
		return data;
	}
	
	public CensusDataRow getGeneralizedDataRow(Integer[] generalizationLevels) {
		if(generalizationLevels.length !=3) {
			System.err.println("Generalization levels incorrect length, got " + generalizationLevels.length + " wanted 3");
			return null;
		}
		AgeGeneralization ageGen = new AgeGeneralization(generalizationLevels[0]);
		AncestryOneGeneralization anc1Gen = new AncestryOneGeneralization(generalizationLevels[1]);
		AncestryTwoGeneralization anc2Gen = new AncestryTwoGeneralization(generalizationLevels[2]);
		Integer[] newRowData = new Integer[census_data.size()];
		newRowData[0] = census_data.get("id");
		newRowData[1] = ageGen.getGeneralizedData(census_data.get("Age"));
		newRowData[2] = anc1Gen.getGeneralizedData(census_data.get("Ancestry1"));
		newRowData[3] = anc2Gen.getGeneralizedData(census_data.get("Ancestry2"));
		newRowData[4] = census_data.get("Salary");
		newRowData[5] = census_data.get("Occupation");
		return new CensusDataRow(newRowData);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(String s : census_data.keySet()) {
			sb.append(s).append(": ").append(census_data.get(s)).append("\n");
		}
		return sb.toString();
	}
}
