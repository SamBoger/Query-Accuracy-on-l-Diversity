package generalization;

import org.junit.Assert;
import org.junit.Test;

public class AncestryTwoGeneralizationTest {

	@Test
	public void test() {
		AncestryTwoGeneralization ancestryGen = new AncestryTwoGeneralization(0);
		Assert.assertSame(7, ancestryGen.getGeneralizedData(7));
		ancestryGen = new AncestryTwoGeneralization(1);
		Assert.assertSame(0, ancestryGen.getGeneralizedData(7));
	}

}
