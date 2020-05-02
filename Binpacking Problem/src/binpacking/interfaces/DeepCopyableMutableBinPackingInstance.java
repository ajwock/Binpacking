package binpacking.interfaces;

import general.interfaces.DeepCopyable;

/**
 * An interface for making deep copies of a bin packing instance.
 * Used primarily for when a subinstance of a problem is created for the
 * branching part of branch and bound.
 * @author Drew Wock
 *
 */
public interface DeepCopyableMutableBinPackingInstance extends MutableBinPackingInstance, DeepCopyable<DeepCopyableMutableBinPackingInstance> {
}
