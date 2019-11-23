package generalization;

import org.junit.Assert;
import org.junit.Test;

public class AncestryOneGeneralizationTest {

	@Test
	public void test() {
		AncestryOneGeneralization ancestryGen = new AncestryOneGeneralization(0);
		Assert.assertSame(7, ancestryGen.getGeneralizedData(7));
		ancestryGen = new AncestryOneGeneralization(1);
		Assert.assertSame(0, ancestryGen.getGeneralizedData(7));
	}

}
