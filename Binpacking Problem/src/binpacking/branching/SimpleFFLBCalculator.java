package binpacking.branching;

import bnb.interfaces.BoundCalculator;

/**
 * Calculates the lower bound using item weights and remaining space in Bins.
 * 
 * @author Drew Wock
 *
 */
public class SimpleFFLBCalculator implements BoundCalculator<BinPackingNode, Integer> {
	/** The lower bound for the First Fit heuristic*/
	int lowerBound;

	@Override
	public Integer bound(BinPackingNode instance) {
		if (lowerBound == -1) {
			int difference = instance.getRemainingItemWeight() - instance.getRemainingSpace();
			int minExtraBins = difference > 0 ? (int) Math.ceil((double) difference / instance.binSize()) : 0;
			lowerBound = minExtraBins + instance.binListDirect().size();
		}
		return lowerBound;
	}

	/**
	 * Sets the lower bound to "-1" if an invalid bound is found
	 */
	public void invalidateBound() {
		lowerBound = -1;
	}

}