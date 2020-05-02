package binpacking.model;

import java.util.List;

import binpacking.interfaces.BinPackingSolution;

/**
 * Keeps track of the global upper bound as well as the best solution found so far
 * 
 * @author Drew Wock
 */
public class BinPackingModel implements BinPackingSolution {
	
	/** The list of bins of the best solution found so far */
	public List<Bin> solution;
	/** the value of the best solution found so far */
	public int solutionValue;
	/** The global upper bound */
	int upperBound;
	
	/**
	 * Creates a new BinPackingModel object
	 */
	public BinPackingModel() {
		solutionValue = 0x7FFFFFFF;
	}
	
	/**
	 * Checks to see if the given solution is the best solution found so far
	 * @param sol the given solution
	 */
	public void checkSolution(BinPackingSolution sol) {
		List<Bin> solved = sol.getSolution();
		if (solved.size() < solutionValue) {
			solution = solved;
			solutionValue = solved.size();
		}
	}
	
	/**
	 * Gets the value of the best solution found so far
	 * @return the value of the best solution found so far
	 */
	public int bestSolutionValue() {
		return solutionValue;
	}
	
	/**
	 * Gets the list of bins of the best solution found so far
	 * @return the list of bins of the best solution found so far
	 */
	public List<Bin> getSolution() {
		return solution;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((solution == null) ? 0 : solution.hashCode());
		result = prime * result + solutionValue;
		result = prime * result + upperBound;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BinPackingModel other = (BinPackingModel) obj;
		if (solution == null) {
			if (other.solution != null)
				return false;
		} else if (!solution.equals(other.solution))
			return false;
		if (solutionValue != other.solutionValue)
			return false;
		return true;
	}

}

//  [Last modified: 2020 04 20 at 15:10:38 GMT]
