package censusAttributes;

public abstract class CensusDataAttribute {

	public int attribute_value;
	public String label;
	
	public CensusDataAttribute(int attributeValue, String lab) {
		attribute_value = attributeValue;
		label = lab;
	}
	
	public abstract boolean isValid();
	
	public abstract CensusDataAttribute getGeneralization(int generalizationLevel);
}
