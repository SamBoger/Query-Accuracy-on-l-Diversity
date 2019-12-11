package censusAttributes;

import static org.junit.Assert.*;

import org.junit.Test;

public class AncestryAttributeTest {

	@Test
	public void test() {
		AncestryAttribute att0 = new AncestryAttribute(0, "ancestry");
		AncestryAttribute att1 = new AncestryAttribute(50, "ancestry");
		AncestryAttribute att2 = new AncestryAttribute(120, "ancestry");
		AncestryAttribute att3 = new AncestryAttribute(201, "ancestry");
		AncestryAttribute att4 = new AncestryAttribute(359, "ancestry");
		AncestryAttribute att5 = new AncestryAttribute(360, "ancestry");
		AncestryAttribute att6 = new AncestryAttribute(425, "ancestry");
		AncestryAttribute att7 = new AncestryAttribute(501, "ancestry");
		AncestryAttribute att8 = new AncestryAttribute(607, "ancestry");
		AncestryAttribute att9 = new AncestryAttribute(729, "ancestry");
		AncestryAttribute att10 = new AncestryAttribute(855, "ancestry");
		AncestryAttribute att11 = new AncestryAttribute(900, "ancestry");
		assertEquals(0, att0.getGeneralization(0).attribute_value);
		assertEquals(7, att7.getGeneralization(0).attribute_value);
		assertEquals(0, att0.getGeneralization(1).attribute_value);
		assertEquals(0, att1.getGeneralization(1).attribute_value);
		assertEquals(1, att2.getGeneralization(1).attribute_value);
		assertEquals(1, att3.getGeneralization(1).attribute_value);
		assertEquals(0, att0.getGeneralization(2).attribute_value);
		assertEquals(0, att1.getGeneralization(2).attribute_value);
		assertEquals(0, att2.getGeneralization(2).attribute_value);
		assertEquals(0, att3.getGeneralization(2).attribute_value);
		assertEquals(2, att11.getGeneralization(2).attribute_value);
		assertEquals(1, att11.getGeneralization(3).attribute_value);
		assertEquals(0, att11.getGeneralization(4).attribute_value);
	}

}
