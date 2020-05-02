package general.interfaces;

/**
 * An item that can produce a deep copy of itself.
 * 
 * @author Drew Wock
 *
 * @param <Self> The type of the invoking object.
 */
public interface DeepCopyable<Self> {
	
	/**
	 * Creates a deep copy of itself
	 * @return the deep copy
	 */
	public Self deepCopy();
	
}
