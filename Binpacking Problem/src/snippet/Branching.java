package snippet;

public class Branching {

	int branches;
	
	public Branching() {
		branches = 0;
	}
	
	void branch(Node node) throws OptimalSolutionException {
		branches++;
		for (Node newBranch : node) {
			if (!newBranch.isLeaf()) {
				int lb = newBranch.lowerBound();
				int ub = newBranch.upperBound();
	
				if (lb <= ub) {
					branch(newBranch);
				}
			}
			//More advanced nodes may want to be able to restore shared resources.
			node.destructor();
		}
	}

}
