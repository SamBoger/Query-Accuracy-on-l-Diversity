package census;

import static org.junit.Assert.*;

import org.junit.Test;

public class ClassAttributeTest {

	@Test
	public void test() {
		ClassAttribute att0 = new ClassAttribute(0, "class");
		ClassAttribute att1 = new ClassAttribute(1, "class");
		ClassAttribute att2 = new ClassAttribute(2, "class");
		ClassAttribute att3 = new ClassAttribute(3, "class");
		ClassAttribute att4 = new ClassAttribute(4, "class");
		ClassAttribute att5 = new ClassAttribute(5, "class");
		ClassAttribute att6 = new ClassAttribute(6, "class");
		ClassAttribute att7 = new ClassAttribute(7, "class");
		ClassAttribute att8 = new ClassAttribute(8, "class");
		ClassAttribute att9 = new ClassAttribute(9, "class");
		ClassAttribute att10 = new ClassAttribute(10, "class");
		ClassAttribute att11 = new ClassAttribute(11, "class");
		ClassAttribute att12 = new ClassAttribute(12, "class");
		assertEquals(0, att0.getGeneralization(0));
		assertEquals(7, att7.getGeneralization(0));
		assertEquals(0, att0.getGeneralization(1));
		assertEquals(0, att1.getGeneralization(1));
		assertEquals(1, att2.getGeneralization(1));
		assertEquals(1, att3.getGeneralization(1));
		assertEquals(0, att0.getGeneralization(2));
		assertEquals(0, att1.getGeneralization(2));
		assertEquals(0, att2.getGeneralization(2));
		assertEquals(0, att3.getGeneralization(2));
		
		assertEquals(2, att11.getGeneralization(2));
		assertEquals(3, att12.getGeneralization(2));
		assertEquals(1, att12.getGeneralization(3));
		assertEquals(0, att12.getGeneralization(4));
	}

}
