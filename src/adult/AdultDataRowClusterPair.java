package adult;

import java.util.Collection;

public class AdultDataRowClusterPair {

	public AdultDataRowCluster cluster1 = null;
	public AdultDataRowCluster cluster2 = null;
	
	public AdultDataRowClusterPair(AdultDataRowCluster cluster1, AdultDataRowCluster cluster2) {
		this.cluster1 = cluster1;
		this.cluster2 = cluster2;
	}
	
//	@Override
//	public int hashCode() {
//		return cluster1.hashCode() - cluster2.hashCode();
//	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof AdultDataRowClusterPair) {
			AdultDataRowClusterPair other = (AdultDataRowClusterPair) o;
			return (other.cluster1.hashCode() == cluster1.hashCode() && other.cluster2.hashCode() == cluster2.hashCode()) ||
					(other.cluster1.hashCode() == cluster2.hashCode() && other.cluster2.hashCode() == cluster1.hashCode());
		}
		return false;
	}
}
