package adult;

import static utils.Configuration.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import adultAttributes.AdultDataAttribute;



public class AdultDataRowCluster {

	public Collection<AdultDataRow> rows;
	public double averageAge;
	public double averageSex;
	public double maxFrequency = 0.0;
	public Map<String, Integer> sensitiveValues;
	public ArrayList<HashSet<String>> QISets;
	
	
	public AdultDataRowCluster(Collection<AdultDataRow> rows) {
		sensitiveValues = new HashMap<String, Integer>();
		
		QISets = new ArrayList<HashSet<String>>(QI_COLUMNS.length);
		this.rows = rows;
		double totalAge = 0.0;
		double totalSex = 0.0;
		for(int i = 0; i < QI_COLUMNS.length; i++) {
			QISets.add(new HashSet<String>());
		}
		for(AdultDataRow row : rows) {
			totalAge += (Integer)row.adult_attributes.get(AGE_LABEL).attribute_value;
			totalSex += ((String)row.adult_attributes.get(SEX_LABEL).attribute_value).equals("Female") ? 1 : 0;
			addToFrequencyMap(row.sensitiveValues(), sensitiveValues);
			
			for(int i = 0; i < QI_COLUMNS.length; i++) {
				addToSet((String)row.adult_attributes.get(QI_COLUMNS[i]).attribute_value, QISets.get(i));
			}
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
	
	public List<AdultDataRow> getSwappedRows(Random rand) {
		List<AdultDataRow> swappedRows = new ArrayList<AdultDataRow>(rows.size());
		List<List<AdultDataAttribute>> nonSensitiveValues = new ArrayList<List<AdultDataAttribute>>();
		List<List<AdultDataAttribute>> sensitiveValues = new ArrayList<List<AdultDataAttribute>>();
	
		for(AdultDataRow row : rows) {
			sensitiveValues.add(row.getSensitiveAttributes());
			nonSensitiveValues.add(row.getNonSensitiveAttributes());
		}
		sensitiveValues = randomize(sensitiveValues, rand);
		nonSensitiveValues = randomize(nonSensitiveValues, rand);
		
		for(int i = 0; i < sensitiveValues.size(); i++) {
			List<AdultDataAttribute> allAtributes = new ArrayList<AdultDataAttribute>();
			allAtributes.addAll(sensitiveValues.get(i));
			allAtributes.addAll(nonSensitiveValues.get(i));
			swappedRows.add(new AdultDataRow(allAtributes));
		}
		
		return swappedRows;
	}

	private <T> List<T> randomize(List<T> values, Random rand) {
		int[] newIndices = new int[values.size()];
		for(int i = 0; i < newIndices.length; i++) {
			newIndices[i] = i;
		}
		
		for(int i = 0; i < newIndices.length; i++) {
			int swapFrom = i+rand.nextInt(newIndices.length-i);
			int temp = newIndices[i];
			newIndices[i] = newIndices[swapFrom];
			newIndices[swapFrom] = temp;
		}
		
		List<T> randomizedList = new ArrayList<T>(values.size());
		for(int i = 0; i < values.size(); i++) {
			randomizedList.add(values.get(newIndices[i]));
		}
		return randomizedList;
	}
	
	private static <T> List<T> randomizeStatic(List<T> values, Random rand) {
		int[] newIndices = new int[values.size()];
		for(int i = 0; i < newIndices.length; i++) {
			newIndices[i] = i;
		}
		
		for(int i = 0; i < newIndices.length; i++) {
			int swapFrom = i+rand.nextInt(newIndices.length-i);
			int temp = newIndices[i];
			newIndices[i] = newIndices[swapFrom];
			newIndices[swapFrom] = temp;
		}
		
		List<T> randomizedList = new ArrayList<T>(values.size());
		for(int i = 0; i < values.size(); i++) {
			randomizedList.add(values.get(newIndices[i]));
		}
		return randomizedList;
	}
	
	public static void main(String[] args) {
		ArrayList<Integer> myList = new ArrayList<Integer>();
		myList.add(1);
		myList.add(2);
		myList.add(3);
		myList.add(4);
		myList.add(5);
		myList.add(6);
		myList.add(7);
		myList.add(8);
		myList.add(9);
		myList.add(10);
		System.out.println(randomizeStatic(myList, new Random()));
	}
}
