package bnb;

import bnb.interfaces.BranchingNode;

/**
 * 
 * Note: Code duplication with branching methods is to reduce speed overhead as
 * much as possible.
 * 
 * @author Drew Wock
 *
 */
public class Branching {
	
	/** The number of times the program branched*/
	int branches;
	
	/** Responsible for finding all the items in the optimal solution,
	 * and what bins they're packed in */
	TreeTracer treeTracer;
	
	/** The current upper bound*/
	BoundType ubcalc;
	/** The current lower bound */
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
			treeTracer = new PrintingTreeTracer();
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
	
	/**
	 * Creates a new branching object, with the traceTree being the only parameter,
	 * the other two parameters are false by default
	 * @param traceTree determines the value of traceTree in branching
	 */
	public Branching(boolean traceTree) {
		this(traceTree, false, false);
	}
	
	/**
	 * Creates a new branching object with all of the parameters set to false
	 */
	public Branching() {
		this(false);
	}
	
	/**
	 * An interface for calculating the bound
	 * @author Drew Wock
	 *
	 */
	private interface BoundType {
		/**
		 * Calculates the bounds for the given child by passing the upper
		 * bound of the given parent onto it
		 * @param parent the given parent
		 * @param child the given child
		 * @return the set upper bound of the child
		 */
		public int bound(BranchingNode parent, BranchingNode child);
	}
	
	/**
	 * An interface for the implementation of TreeTracer
	 * @author Drew Wock
	 *
	 */
	private interface TreeTracer {
		/**
		 * Traces the solution path down the tree starting at the given node
		 * using the given upper and lower bounds
		 * @param node the given node
		 * @param lb the given lower bound
		 * @param ub the given upper bound
		 */
		public void traceNode(BranchingNode node, int lb, int ub);
		
		/**
		 * Traces the optimal solution path down the tree starting at the given node
		 * @param node the given node
		 */
		public void traceNode(BranchingNode node);
	}

	/**
	 * This class is used to print the branching tree as branching occurs. Useful
	 * for debugging because of the real time updates.
	 * 
	 * @author Drew Wock
	 *
	 */
	private class PrintingTreeTracer implements TreeTracer {
		
		/** The string builder object for the output of TreeTracer*/
		StringBuilder sb;
		
		/**
		 * Traces the given node, except adds it on to the given string
		 * @param node the given node
		 * @param ms the given string
		 */
		private void traceNodeMidString(BranchingNode node, StringBuilder ms) {
			sb = new StringBuilder();
			for (int i = 0; i < node.level(); i++) {
				sb.insert(0, "--");
			}
			sb.append(ms);
			sb.append(": ");
			sb.append(node);
			sb.append("\n");
			System.out.print(sb);
		}
		
		public void traceNode(BranchingNode node, int lb, int ub) {
			StringBuilder ms = new StringBuilder();
			ms.append(lb);
			ms.append(" <? ");
			ms.append(ub);
			traceNodeMidString(node, ms);
		}

		public void traceNode(BranchingNode node) {
			traceNodeMidString(node, new StringBuilder("leaf"));
		}

	}
	
	/**
	 * An interface for branching at a node
	 * @author Drew Wock
	 *
	 */
	private interface Brancher {
		/**
		 * Branches at the given node
		 * @param node the given node
		 */
		public void branch(BranchingNode node);
	}
	
	/**
	 * Branches without keeping a tree tracer
	 * @author Drew Wock
	 *
	 */
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
	
	/**
	 * Branches while keeping track of a tree tracer
	 * @author Drew Wock
	 *
	 */
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

					treeTracer.traceNode(newBranch, lb, ub);

					if (lb < ub) {
						branch(newBranch);
					}
				} else {
					treeTracer.traceNode(newBranch);
				}
				// More advanced nodes may want to be able to restore shared resources.
				newBranch.destructor();
			}
		}
	}
	
	/**
	 * Branches from the given node
	 * @param node the given node
	 * @throws OptimalSolutionException thrown if the optimal solution has already been found and branching is unnecessary
	 */
	public void branch(BranchingNode node) throws OptimalSolutionException {
		brancher.branch(node);
	}
	
	/**
	 * Gets the number of branches that occurred in this instance
	 * @return the number of branches that occurred in this instance
	 */
	public int getBranches() {
		return branches;
	}

}