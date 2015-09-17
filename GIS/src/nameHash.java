import java.util.Vector;

/**
 * interface for the hasTable
 * @author Chase
 *
 * @param <T>
 */
public interface nameHash<T>  {
	public Long getOffset();
	//do nothing and return false if not allowing duplicates
	public boolean addOff(Long x);
	//return vector with single long offset if not allowing duplicates
	public Vector<Long> getOffsets();
	public String getName();
}
