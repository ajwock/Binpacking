package binpacking.branching;

import binpacking.interfaces.BinPackingSolution;
import bnb.interfaces.BoundCalculator;

public class SimpleFFUBCalculator implements BoundCalculator<BinPackingNode, Integer> {
	
	int upperBound;

	@Override
	public Integer bound(BinPackingNode instance) {
		// Upper bound can be an expensive operation- some basic memoization in case we
		// want to see it multiple times.
		if (upperBound == -1) {
			// Apply an appromixation hueristic to the remaining instance.
			BinPackingNode temp = new BinPackingNode(instance);
			BinPackingSolution result = instance.getHueristic().apply(temp);
			/** Pass in the result as a possible solution. */
			instance.getModel().checkSolution(result);
		}
		upperBound = instance.getModel().bestSolutionValue();
		return upperBound;
	}
	
	public void invalidateBound() {
		upperBound = -1;
	}

}
