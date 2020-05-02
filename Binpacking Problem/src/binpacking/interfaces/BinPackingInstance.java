package binpacking.interfaces;

import java.util.List;

import binpacking.model.Bin;
import binpacking.model.Item;

/**
 * An interface for BinPackingInstance objects
 * @author Drew Wock
 *
 */
public interface BinPackingInstance {

	/**
	 * Returns the list of bins in the instance. At this level of abstraction,
	 * whether this is a clone of the list of bins or a shared internal list is
	 * ambiguous.
	 * 
	 * @return The list of bins in the instance.
	 */
	List<Bin> binList();

	/**
	 * Returns the list of items in the instance. At this level of abstraction,
	 * whether this is a clone of the list of bins or a shared internal list is
	 * ambiguous.
	 * 
	 * @return The list of items in the instance.
	 */
	List<Item> itemList();

	/**
	 * Returns the size of empty bins in the instance.
	 * 
	 * @return the size of empty bins in the instance.
	 */
	int binSize();

}