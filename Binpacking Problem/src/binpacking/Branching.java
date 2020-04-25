package binpacking;

import binpacking.interfaces.BranchingNode;

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
				BinPackingNode x = (BinPackingNode) newBranch;
				for (int i = 0; i < x.level; i++) {
					System.out.print("--");
				}
				System.out.print("" + lb + " " + ub + x.binList + "\n");

				if (lb < ub) {
					branch(newBranch);
				}
			}
			//More advanced nodes may want to be able to restore shared resources.
			node.destructor();
		}
	}

}