package anonymization;

import java.util.Map;

public class QuasiIdentifier {
	public Map<String, Integer> quasi_identifier_data;
	
	public QuasiIdentifier(Map<String, Integer> quasiIdData) {
		quasi_identifier_data = quasiIdData;
	}
	
	@Override
	public int hashCode() {
		return quasi_identifier_data.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null || !(o instanceof QuasiIdentifier)) {
			return false;
		}
		return equals((QuasiIdentifier) o);		
	}
	
	public boolean equals(QuasiIdentifier otherQid) {
		if(otherQid.quasi_identifier_data == null || otherQid.quasi_identifier_data.size() != quasi_identifier_data.size()) {
			return false;
		}
		boolean same = true;
		for(String key : quasi_identifier_data.keySet()) {
			if(quasi_identifier_data.get(key) != otherQid.quasi_identifier_data.get(key)) {
				same = false;
				break;
			}
		}
		return same;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(String s : quasi_identifier_data.keySet()) {
			sb.append(s).append(": ").append(quasi_identifier_data.get(s)).append("\n");
		}
		return sb.toString();
	}
}
