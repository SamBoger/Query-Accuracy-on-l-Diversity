package anonymization;

import java.util.Map;

public class QuasiIdentifier {
	public Map<String, Integer> quasiIdentifierData;
	
	public QuasiIdentifier(Map<String, Integer> quasiIdData) {
		quasiIdentifierData = quasiIdData;
	}
	
	@Override
	public int hashCode() {
		return quasiIdentifierData.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null || !(o instanceof QuasiIdentifier)) {
			return false;
		}
		return equals((QuasiIdentifier) o);		
	}
	
	public boolean equals(QuasiIdentifier otherQid) {
		if(otherQid.quasiIdentifierData == null || otherQid.quasiIdentifierData.size() != quasiIdentifierData.size()) {
			return false;
		}
		boolean same = true;
		for(String key : quasiIdentifierData.keySet()) {
			if(quasiIdentifierData.get(key) != otherQid.quasiIdentifierData.get(key)) {
				same = false;
				break;
			}
		}
		return same;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(String s : quasiIdentifierData.keySet()) {
			sb.append(s).append(": ").append(quasiIdentifierData.get(s)).append("\n");
		}
		return sb.toString();
	}
}
