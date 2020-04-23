package binpacking.interfaces;

/**
 * A bin packing instance that can store and revert ordered sequences of changes.
 * 
 * @author Andrew Wock
 *
 */
public interface DynamicBinPackingInstance extends MutableBinPackingInstance, ChangeStack<MutableBinPackingInstance> {
}
