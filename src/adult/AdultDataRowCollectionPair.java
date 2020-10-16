package adult;

import java.util.Collection;

public class AdultDataRowCollectionPair {

	public Collection<AdultDataRow> row1 = null;
	public Collection<AdultDataRow> row2 = null;
	
	public AdultDataRowCollectionPair(Collection<AdultDataRow> row1, Collection<AdultDataRow> row2) {
		this.row1 = row1;
		this.row2 = row2;
	}
	
	@Override
	public int hashCode() {
		return row1.hashCode() - row2.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof AdultDataRowCollectionPair) {
			return((AdultDataRowCollectionPair)o).hashCode() == hashCode();
		}
		return false;
	}
}
