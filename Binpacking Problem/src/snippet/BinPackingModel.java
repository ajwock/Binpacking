package snippet;

import java.util.List;

public class BinPackingModel {

	public List<Bin> solution;
	int solutionValue;
	int upperBound;
	

	/**
	 * The "goodness" of a solution is inversely proportional to how large it is.
	 * The upper bound for how good the optimal solution would be the best solution found so far.
	 * 
	 * @return
	 */
	public int getUpperBound() {
		return 0;
	}
	
	public void trySetUpperBound(int ub) {
		upperBound = ub > upperBound ? ub : upperBound;
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
}
