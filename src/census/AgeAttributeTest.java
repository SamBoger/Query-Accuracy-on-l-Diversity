package census;

import static org.junit.Assert.*;

import org.junit.Test;

public class AgeAttributeTest {

	@Test
	public void test() {
		AgeAttribute att21 = new AgeAttribute(21, "age");
		AgeAttribute att26 = new AgeAttribute(26, "age");
		AgeAttribute att83 = new AgeAttribute(83, "age");
		AgeAttribute att90 = new AgeAttribute(90, "age");
		assertSame(21, att21.getGeneralization(0));
		assertSame(4, att21.getGeneralization(1));
		assertSame(2, att21.getGeneralization(2));
		assertSame(1, att21.getGeneralization(3));
		assertSame(0, att21.getGeneralization(4));
		assertSame(0, att21.getGeneralization(5));
		assertSame(0, att21.getGeneralization(6));
		
		assertSame(26, att26.getGeneralization(0));
		assertSame(5, att26.getGeneralization(1));
		assertSame(2, att26.getGeneralization(2));
		assertSame(1, att26.getGeneralization(3));
		assertSame(0, att26.getGeneralization(4));
		assertSame(0, att26.getGeneralization(5));
		assertSame(0, att26.getGeneralization(6));
		
		assertSame(83, att83.getGeneralization(0));
		assertSame(16, att83.getGeneralization(1));
		assertSame(8, att83.getGeneralization(2));
		assertSame(4, att83.getGeneralization(3));
		assertSame(2, att83.getGeneralization(4));
		assertSame(1, att83.getGeneralization(5));
		assertSame(0, att83.getGeneralization(6));
		
		assertSame(90, att90.getGeneralization(0));
		assertSame(18, att90.getGeneralization(1));
		assertSame(9, att90.getGeneralization(2));
		assertSame(4, att90.getGeneralization(3));
		assertSame(2, att90.getGeneralization(4));
		assertSame(1, att90.getGeneralization(5));
		assertSame(0, att90.getGeneralization(6));
	}

}
