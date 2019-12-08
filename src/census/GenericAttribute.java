package census;

public class GenericAttribute extends CensusDataAttribute {

	public GenericAttribute(int attributeValue, String lab) {
		super(attributeValue, lab);
		// TODO Auto-generated constructor stub
	}

	@Override
	boolean isValid() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	CensusDataAttribute getGeneralization(int generalizationLevel) {
		// TODO Auto-generated method stub
		return new GenericAttribute(attribute_value, label);
	}

}
