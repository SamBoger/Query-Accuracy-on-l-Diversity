package anonymization;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class QuasiIdentifierTest {

	@Test
	public void test() {
		Map<String, Integer> qid1Data = new HashMap<String, Integer>();
		qid1Data.put("Age", 1);
		qid1Data.put("Ancestry1", 1);
		Map<String, Integer> qid2Data = new HashMap<String, Integer>();
		qid2Data.put("Age", 1);
		qid2Data.put("Ancestry1", 1);
		QuasiIdentifier qid1 = new QuasiIdentifier(qid1Data);
		QuasiIdentifier qid2 = new QuasiIdentifier(qid2Data);
		assertEquals(qid1, qid2);
		Map<QuasiIdentifier, Integer> testMap = new HashMap<QuasiIdentifier, Integer>();
		testMap.put(qid1, 0);
		assertTrue(testMap.containsKey(qid1));
		assertTrue(testMap.containsKey(qid2));
		testMap.put(qid2, 0);
		assertEquals(1, testMap.size());
	}

}
