package binpacking.interfaces;

import java.util.List;

import binpacking.model.Bin;

/**
 * An interface for BinPackingSolution objects
 * @author Drew Wock
 *
 */
public interface BinPackingSolution {
	
	/**
	 * Gets the list of bins that is a solution to the given problem
	 * @return the list of bins that is a solution to the given problem
	 */
	List<Bin> getSolution();

}
