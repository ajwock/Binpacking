package binpacking.branching;

import bnb.interfaces.BoundCalculator;

public class SimpleFFLBCalculator implements BoundCalculator<BinPackingNode, Integer> {

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

	public void invalidateBound() {
		lowerBound = -1;
	}

}