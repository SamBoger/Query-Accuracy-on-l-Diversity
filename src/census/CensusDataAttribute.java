package census;

public abstract class CensusDataAttribute {

	public int attribute_value;
	public String label;
	
	public CensusDataAttribute(int attributeValue, String lab) {
		attribute_value = attributeValue;
		label = lab;
	}
	
	abstract boolean isValid();
	
	abstract CensusDataAttribute getGeneralization(int generalizationLevel);
}
