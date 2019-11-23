package generalization;

import org.junit.Assert;
import org.junit.Test;

public class AgeGeneralizationTest {

	@Test
	public void test() {
		AgeGeneralization ageGen = new AgeGeneralization(1);
		Assert.assertSame(1, ageGen.getGeneralizedData(0));
		ageGen = new AgeGeneralization(2);
		Assert.assertSame(2, ageGen.getGeneralizedData(1));
		ageGen = new AgeGeneralization(3);
		Assert.assertSame(3, ageGen.getGeneralizedData(0));
		Assert.assertSame(3, ageGen.getGeneralizedData(3));
		Assert.assertSame(7, ageGen.getGeneralizedData(4));
		Assert.assertSame(7, ageGen.getGeneralizedData(7));
	
	}

}
