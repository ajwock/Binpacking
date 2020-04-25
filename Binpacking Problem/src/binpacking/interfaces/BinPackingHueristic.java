package binpacking.interfaces;

public interface BinPackingHueristic {

	/**
	 * Applies the Bin Packing approximation hueristic onto the instance. May
	 * clobber the instance.
	 * 
	 * @param instance The instance to approximate on.
	 * @return The approximate solution.
	 */
	public abstract BinPackingSolution apply(MutableBinPackingInstance instance);

}
