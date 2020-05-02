package binpacking.branching;

import binpacking.interfaces.BinPackingSolution;
import bnb.interfaces.BoundCalculator;

/**
 * Calculates the upper bound for the First Fit heuristic
 * @author Drew Wock
 *
 */
public class SimpleFFUBCalculator implements BoundCalculator<BinPackingNode, Integer> {
	/** the upper bound for the First Fit heuristic */
	int upperBound;

	@Override
	public Integer bound(BinPackingNode instance) {
		// Upper bound can be an expensive operation- some basic memoization in case we
		// want to see it multiple times.
		if (upperBound == -1) {
			// Apply an appromixation hueristic to the remaining instance.
			//BinPackingNode temp = new BinPackingNode(instance);
			BinPackingSolution result = instance.getHueristic().apply(instance);
			/** Pass in the result as a possible solution. */
			instance.getModel().checkSolution(result);
		}
		upperBound = instance.getModel().bestSolutionValue();
		return upperBound;
	}
	/**
	 * Sets the lower bound to "-1" if an invalid bound is found
	 */
	public void invalidateBound() {
		upperBound = -1;
	}

}
