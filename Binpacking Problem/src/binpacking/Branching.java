package binpacking;

import binpacking.interfaces.BranchingNode;

/**
 * 
 * Note: Code duplication with branching methods is to reduce speed overhead as
 * much as possible.
 * 
 * @author Andrew Wock
 *
 */
public class Branching {

	int branches;

	StringBuilder treeTracer;

	BoundType ubcalc;
	BoundType lbcalc;
	/**
	 * Determines exact branching behavior.
	 * 
	 * Currently the only different branching behavior is
	 * 
	 */
	Brancher brancher;

	/**
	 * Class used to execute branching for branch and bound algorithms with problems
	 * that can be described with the BranchingNode interface.
	 * 
	 * @param traceTree          Determines whether to keep track of the tree.
	 * @param parentalUpperBound For some algorithms, the upper bound for
	 *                           determining the child node's branching behavior can
	 *                           be determined by the parent's upper bound. This can
	 *                           be a considerable speedup if upper bound
	 *                           calculations are expensive.
	 * @param parentalLowerBound For other algorithms, the lower bound is like that.
	 */
	public Branching(boolean traceTree, boolean parentalUpperBound, boolean parentalLowerBound) {
		branches = 0;
		if (traceTree) {
			treeTracer = new StringBuilder();
			brancher = new TracingBrancher();
		} else {
			brancher = new NoTraceBrancher();
		}
		if (parentalUpperBound) {
			ubcalc = new BoundType() {
				@Override
				public int bound(BranchingNode parent, BranchingNode child) {
					return parent.upperBound();
				}
			};
		} else {
			ubcalc = new BoundType() {
				@Override
				public int bound(BranchingNode parent, BranchingNode child) {
					return child.upperBound();
				}
			};
		}
		if (parentalLowerBound) {
			lbcalc = new BoundType() {
				@Override
				public int bound(BranchingNode parent, BranchingNode child) {
					return parent.lowerBound();
				}
			};
		} else {
			lbcalc = new BoundType() {
				@Override
				public int bound(BranchingNode parent, BranchingNode child) {
					return child.lowerBound();
				}
			};
		}

	}

	private interface BoundType {
		public int bound(BranchingNode parent, BranchingNode child);
	}

	private void traceNodeMidString(BranchingNode node, String ms) {
		for (int i = 0; i < node.level(); i++) {
			treeTracer.append("--");
		}

		treeTracer.append(": ");
		treeTracer.append(node);
		treeTracer.append("\n");
	}

	public void traceNode(BranchingNode node, int lb, int ub) {
		String ms = "" + lb + " <? " + ub;
		traceNodeMidString(node, ms);
	}

	public void traceNode(BranchingNode node) {
		traceNodeMidString(node, "leaf");
	}

	private interface Brancher {
		public void branch(BranchingNode node);
	}

	private class NoTraceBrancher implements Brancher {
		/**
		 * Note: A node is responsible for keeping track of possible children. The
		 * Iterable interface for a Node returns a sequence of choices that lead to
		 * children.
		 */
		public void branch(BranchingNode node) {
			branches++;
			for (BranchingNode newBranch : node) {
				if (!newBranch.isLeaf()) {
					int lb = lbcalc.bound(node, newBranch);
					int ub = ubcalc.bound(node, newBranch);

					if (lb < ub) {
						branch(newBranch);
					}
				}
				// More advanced nodes may want to be able to restore shared resources.
				newBranch.destructor();
			}
		}
	}

	private class TracingBrancher implements Brancher {
		/**
		 * Note: A node is responsible for keeping track of possible children. The
		 * Iterable interface for a Node returns a sequence of choices that lead to
		 * children.
		 */
		public void branch(BranchingNode node) {
			branches++;
			for (BranchingNode newBranch : node) {
				if (!newBranch.isLeaf()) {
					int lb = lbcalc.bound(node, newBranch);
					int ub = ubcalc.bound(node, newBranch);

					traceNode(newBranch, lb, ub);

					if (lb < ub) {
						branch(newBranch);
					}
				} else {
					traceNode(newBranch);
				}
				// More advanced nodes may want to be able to restore shared resources.
				newBranch.destructor();
			}
		}
	}

	public void branch(BranchingNode node) throws OptimalSolutionException {
		brancher.branch(node);
	}

}