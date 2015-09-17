import java.util.Vector;

import org.omg.CORBA.Object;

/**
 * class that is used as the elements to my hashTable
 * @author Chase
 *
 */
public class HashElem implements nameHash<HashElem> {

	private Vector<Long> offsets;
	private String abvName;
	
	public HashElem() {
		offsets = new Vector<Long>();
	}
	public HashElem(String x,Long y) {
		offsets = new Vector<Long>();
		abvName = x;
		offsets.add(y);
	}
	//readable diplay of it's contents
	public String toString() {
		StringBuilder temp = new StringBuilder();
		temp.append("Name: " + abvName + " Offset(s): ");
		for(int i = 0; i< offsets.size();i++) {
			temp.append(offsets.get(i) + ",");
		}
		return (temp.toString().substring(0, temp.length()-1)) + "\n";
	}
	public void setOffset(Long y) {
		offsets.set(0, y);
	}
	public void setName(String x) {
		abvName = x;
	}
	public String getName() {
		return abvName;
	}
	@Override
	public Long getOffset() {
		return offsets.get(0);
	}
	public boolean equals(Object x) {
		if(x.getClass() == this.getClass()) {
			if(this.getName().equals(((HashElem) x).getName())) {
				return true;
			}
		}
		return false;
	}
	@Override
	public boolean addOff(Long x) {
		offsets.add(x);
		return true;
	}

	@Override
	public Vector<Long> getOffsets() {
		return offsets;
	}

}
