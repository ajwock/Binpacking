package snippet;

import java.util.List;

public abstract class BinPackingHueristic {

	/**
	 * Applies the Bin Packing approximation hueristic onto the instance.  May clobber the instance.
	 * 
	 * @param instance The instance to approximate on.
	 * @return The approximate solution.
	 */
	public abstract BinPackingSolution apply(BinPackingInstance instance);

}
