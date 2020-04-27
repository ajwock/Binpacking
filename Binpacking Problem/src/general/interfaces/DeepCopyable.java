package general.interfaces;

/**
 * An item that can produce a deep copy of itself.
 * 
 * @author dzdt
 *
 * @param <Self> The type of the invoking object.
 */
public interface DeepCopyable<Self> {

	public Self deepCopy();
	
}
