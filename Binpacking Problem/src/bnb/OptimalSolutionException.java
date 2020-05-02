package bnb;

import binpacking.interfaces.BinPackingSolution;

/**
 * Exception used to indicate that an optimal solution has been detected.
 * 
 * The most obvious example of this is when an upper bound and a lower bound
 * used to optimize a problem are found to be equal- i.e. the optimal solution
 * can be no better or worse than the current instance.
 * 
 * @author Andrew Wock
 *
 */
public class OptimalSolutionException extends Exception {
	
	/** The optimal bin packing solution*/
	BinPackingSolution solution;
	
	/**
	 * Sets BinPackingSolution to the given solution
	 * @param sln the given solution
	 */
	public OptimalSolutionException(BinPackingSolution sln) {
		solution = sln;
	}
	
	/**
	 * Gets the optimal bin packing solution
	 * @return the optimal bin packing solution
	 */
	public BinPackingSolution getSolution() {
		return solution;
	}

	private static final long serialVersionUID = 1L;

}
