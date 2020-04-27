package bnb.interfaces;

/**
 * Class used to calculate solution bounds
 * 
 * @author Andrew Wock
 *
 * @param <Instance> The type of the problem instance to calculate a bound on.
 * @param <BoundType> The type representing the bound (probably a numeric type).
 */
public interface BoundCalculator<Instance, BoundType> {
	
	/**
	 * Calculate a solution bound for a problem instance.
	 * 
	 * @param instance The problem instance to calculate a solution bound for.
	 * @return The bound that was calculated.
	 */
	public abstract BoundType bound(Instance instance);
	
	/**
	 * For memoization, some bound calculators may want to have memorized bounds
	 * invalidated when some things happen.
	 */
	public default void invalidateBound() {
	}

}
