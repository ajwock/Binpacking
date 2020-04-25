package binpacking.model;

import java.util.List;

import binpacking.interfaces.BinPackingSolution;

/**
 * Keeps track of the global upper bound.
 */
public class BinPackingModel {

	public List<Bin> solution;
	public int solutionValue;
	int upperBound;

	public BinPackingModel() {
		solutionValue = 0x7FFFFFFF;
	}

	public void checkSolution(BinPackingSolution sol) {
		List<Bin> solved = sol.getSolution();
		if (solved.size() < solutionValue) {
			solution = solved;
			solutionValue = solved.size();
		}
	}

	public int bestSolutionValue() {
		return solutionValue;
	}

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
