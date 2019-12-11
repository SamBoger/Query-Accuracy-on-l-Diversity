package anonymization;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

public class GeneralizationTest {

	@Test
	public void test() {
		Integer[] genLevels = {0,5,7};
		Generalization g = new Generalization(genLevels);
		Generalization g2 = new Generalization(genLevels);
		assertTrue(g.equals(g2));
		assertTrue(g2.equals(g));
		assertTrue(g.hashCode() == g2.hashCode());
		HashMap<Generalization, Integer> map = new HashMap<Generalization, Integer>();
		map.put(g, 1);
		assertTrue(map.containsKey(g));
		assertTrue(map.containsKey(g2));
	}

}
