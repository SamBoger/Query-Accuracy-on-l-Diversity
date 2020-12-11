package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utils {
	public static <T> List<T> randomize(List<T> values, Random rand) {
		int[] newIndices = new int[values.size()];
		for(int i = 0; i < newIndices.length; i++) {
			newIndices[i] = i;
		}
		
		for(int i = 0; i < newIndices.length; i++) {
			int swapFrom = i+rand.nextInt(newIndices.length-i);
			int temp = newIndices[i];
			newIndices[i] = newIndices[swapFrom];
			newIndices[swapFrom] = temp;
		}
		
		List<T> randomizedList = new ArrayList<T>(values.size());
		for(int i = 0; i < values.size(); i++) {
			randomizedList.add(values.get(newIndices[i]));
		}
		return randomizedList;
	}
}
