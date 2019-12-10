package anonymization;

import java.util.Arrays;

public class Generalization {

	public Integer[] generalization_levels = null;
	
	public Generalization(Integer[] generalizationLevels) {
		generalization_levels = generalizationLevels;
	}
	
	@Override
	public int hashCode() {
		return Arrays.toString(generalization_levels).hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Generalization)) {
			return false;
		}
		Integer[] otherGeneralizationLevels = ((Generalization)o).generalization_levels;
		if(otherGeneralizationLevels.length != generalization_levels.length) {
			return false;
		}
		for(int i = 0; i < generalization_levels.length; i ++) {
			if(generalization_levels[i] != otherGeneralizationLevels[i]) {
				return false;
			}
		}
		return true;		
	}
	
	@Override
	public String toString() {
		return Arrays.toString(generalization_levels);
	}

	public int getTotalLevels() {
		int total = 0;
		for(Integer i : generalization_levels) {
			total += i;
		}
		return total;
	}
}
