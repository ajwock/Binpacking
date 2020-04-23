package snippet;

import java.util.List;

public class BinPackingModel {

	public List<Bin> solution;
	int solutionValue;
	int upperBound;
	
	public BinPackingModel() {
		solutionValue = 0x7FFFFFFF;
		upperBound = 0x7FFFFFFF;
	}
	
	/**
	 * The "goodness" of a solution is inversely proportional to how large it is.
	 * The upper bound for how good the optimal solution would be the best solution found so far.
	 * 
	 * @return The upper bound.
	 */
	public int getUpperBound() {
		return upperBound;
	}
	
	public void trySetUpperBound(int ub) {
		upperBound = ub < upperBound ? ub : upperBound;
	}

	public void checkSolution(BinPackingSolution sol) {
		List<Bin> solved = sol.getSolution();
		if (solved.size() < solutionValue) {
			solution = solved;
			solutionValue = solved.size();
			trySetUpperBound(solutionValue);
		}
	}
	
	public int bestSolutionValue() {
		return solutionValue;
	}
	
	public List<Bin> getSolution() {
		return solution;
	}
}
