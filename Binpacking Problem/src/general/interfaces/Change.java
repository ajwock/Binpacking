package general.interfaces;

public interface Change<T> {

	/**
	 * Applies the change to the instance.
	 * 
	 * @param instance The instance to apply the change to.
	 */
	public abstract void applyChange(T instance);

	/**
	 * Reverses the change to the instance. Usually dependent on that other changes
	 * made afterwards were reverted before this one.
	 * 
	 * @param instance The instance to reverse the change to.
	 */
	public abstract void reverseChange(T instance);
}
