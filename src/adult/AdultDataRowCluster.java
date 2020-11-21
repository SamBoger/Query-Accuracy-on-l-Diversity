package adult;

import static utils.Configuration.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;



public class AdultDataRowCluster {

	public Collection<AdultDataRow> rows;
	public double averageAge;
	public double averageSex;
	public double maxFrequency = 0.0;
	public Map<String, Integer> sensitiveValues;
	public ArrayList<HashSet<String>> QISets;
//	public Map<String, Integer> occupations;
//	public Map<String, Integer> races;
	
	public AdultDataRowCluster(Collection<AdultDataRow> rows) {
		sensitiveValues = new HashMap<String, Integer>();
		
		QISets = new ArrayList<HashSet<String>>(QI_COLUMNS.length);
//		occupations = new HashMap<String, Integer>();
//		races = new HashMap<String, Integer>();
		this.rows = rows;
		double totalAge = 0.0;
		double totalSex = 0.0;
		for(String label : QI_COLUMNS) {
			QISets.add(new HashSet<String>());
		}
		for(AdultDataRow row : rows) {
			totalAge += (Integer)row.adult_attributes.get(AGE_LABEL).attribute_value;
			totalSex += ((String)row.adult_attributes.get(SEX_LABEL).attribute_value).equals("Female") ? 1 : 0;
			addToFrequencyMap(row.sensitiveValues(), sensitiveValues);
			
			for(int i = 0; i < QI_COLUMNS.length; i++) {
				addToSet((String)row.adult_attributes.get(QI_COLUMNS[i]).attribute_value, QISets.get(i));
			}
			
//			addToFrequencyMap((String)row.adult_attributes.get(OCCUPATION_LABEL).attribute_value, occupations);
//			addToFrequencyMap((String)row.adult_attributes.get(RACE_LABEL).attribute_value, occupations);
		}
		averageAge = totalAge / (rows.size()*MAX_AGE_VALUE);
		averageSex = totalSex / rows.size();
	}
	
	private void addToSet(String key, HashSet<String> set) {
		set.add(key);
	}

	private void addToFrequencyMap(String key, Map<String, Integer> map) {
		int newVal = 1;
		if(map.containsKey(key)) {
			newVal = map.get(key)+1;
		}
		map.put(key, newVal);
		
	}

	public AdultDataRowCluster(AdultDataRow newCenter) {
		this(createSet(newCenter));
	}

	private static Collection<AdultDataRow> createSet(AdultDataRow newCenter) {
		Collection<AdultDataRow> individualSet = new HashSet<AdultDataRow>();
		individualSet.add(newCenter);
		return individualSet;
	}

	public boolean equals(Object other) {
		if(other instanceof AdultDataRowCluster) {
			AdultDataRowCluster otherCluster = (AdultDataRowCluster) other;
			
			int otherHash = 0;
			for(AdultDataRow row : otherCluster.rows) {
				otherHash += row.hashCode();
			}
			int myHash = 0;
			for(AdultDataRow row : rows) {
				myHash += row.hashCode();
			}
			return otherHash == myHash;
		}
		return false;
	}
	
	public void merge(AdultDataRowCluster other) {
		int newSize = rows.size() + other.rows.size();
		averageAge = (averageAge * rows.size() + other.averageAge() * other.rows.size())/newSize;
		averageSex = (averageSex * rows.size() + other.averageSex() * other.rows.size())/newSize;
		for(AdultDataRow newRow : other.rows) {
			addToFrequencyMap(newRow.sensitiveValues(), sensitiveValues); // TODO: Better merging efficiency?
			for(int i = 0; i < QI_COLUMNS.length; i++) {
				addToSet((String)newRow.adult_attributes.get(QI_COLUMNS[i]).attribute_value, QISets.get(i));
			}
		}
		rows.addAll(other.rows);
	}
	
	public void merge(AdultDataRow other) {
		merge(new AdultDataRowCluster(other));
	}
	
	public double averageAge() {
		return averageAge;
	}
	
	public double averageSex() {
		return averageSex;
	}
	
	public boolean containsSensitiveValueOfRow(AdultDataRow newRow) {
		return sensitiveValues.containsKey(newRow.sensitiveValues());
	}
	
	public double getMaxSensitiveValueFrequency() {
		int maxSensitiveValue = 0;
		for(String sensValKey : sensitiveValues.keySet()) {
			maxSensitiveValue = Math.max(maxSensitiveValue, sensitiveValues.get(sensValKey));
		}
		return (0.0 + maxSensitiveValue) / rows.size();
	}
	
	public int[] getMaxAndTotalSensitiveValues(AdultDataRowCluster otherCluster) {
		int[] results = new int[2]; // TODO: this should be a Pair or other object
		int maxSensitiveValue = 0;
		Set<String> allKeys = new HashSet<String>();
		allKeys.addAll(sensitiveValues.keySet());
		allKeys.addAll(otherCluster.sensitiveValues.keySet());
		for(String sensValKey : allKeys) {
			int numSens1 = sensitiveValues.containsKey(sensValKey) ? sensitiveValues.get(sensValKey) : 0;
			int numSens2 = otherCluster.sensitiveValues.containsKey(sensValKey) ? otherCluster.sensitiveValues.get(sensValKey) : 0;
			maxSensitiveValue = Math.max(maxSensitiveValue, numSens1 + numSens2);
		}
		results[0] = maxSensitiveValue; 
		results[1] = allKeys.size(); // numDistinctSensValues
		return results;
	}

	public int numDistinctSensitiveValues() {
		return sensitiveValues.size();
	}

	public double getMaxSensitiveValueFrequencyWithNewPoint(AdultDataRow potentialNewRow) {
		int maxSensitiveValue = 0;
		String newSensValue = potentialNewRow.sensitiveValues();
		for(String sensValKey : sensitiveValues.keySet()) {
			int numSensValues = sensitiveValues.get(sensValKey);
			if(sensValKey.equals(newSensValue)) {
				numSensValues++;
			}
			maxSensitiveValue = Math.max(maxSensitiveValue, numSensValues);
		}
		return (0.0 + maxSensitiveValue) / rows.size();
	}
}
