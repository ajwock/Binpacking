package binpacking.interfaces;

import general.interfaces.ChangeStack;

/**
 * A bin packing instance that can store and revert ordered sequences of
 * changes.
 * 
 * @author Drew Wock
 *
 */
public interface DynamicBinPackingInstance extends MutableBinPackingInstance, ChangeStack<MutableBinPackingInstance> {
}
