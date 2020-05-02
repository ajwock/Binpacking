package bnb.interfaces;

import java.util.Iterator;

/**
 * An interface for BranchingNodes
 * @author Drew Wock
 *
 */
public interface BranchingNode extends Iterator<BranchingNode>, Iterable<BranchingNode> {

	default void destructor() {
	}
	/** True if this node is a leaf, false otherwise*/
	boolean isLeaf();
	/** The upper bound */
	int upperBound();
	/** The lower bound */
	int lowerBound();
	/** The level of the tree that this node is at. */
	int level();
	/** An iterator for BranchingNodes */
	Iterator<BranchingNode> iterator();
	/** True if there is a next bin to check, false otherwise */
	boolean hasNext();
	/** The next BranchingNode to be considered */
	BranchingNode next();

}