package snippet;

public class Branching {

	int branches;
	
	public Branching() {
		branches = 0;
	}
	
    /**
     * Note: A node is responsible for keeping track of possible children.
     * The Iterable interface for a Node returns a sequence of choices that
     * lead to children.
     */
	void branch(BranchingNode node) throws OptimalSolutionException {
		branches++;
		for (BranchingNode newBranch : node) {
			if (!newBranch.isLeaf()) {
				int lb = newBranch.lowerBound();
				int ub = newBranch.upperBound();
	
				if (lb < ub) {
					branch(newBranch);
				}
			}
			//More advanced nodes may want to be able to restore shared resources.
			node.destructor();
		}
	}

}