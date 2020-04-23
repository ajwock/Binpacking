package binpacking.interfaces;

import java.util.Iterator;

public interface BranchingNode extends Iterator<BranchingNode>, Iterable<BranchingNode> {

	void destructor();
	
	boolean isLeaf();

	int upperBound();

	int lowerBound();

	Iterator<BranchingNode> iterator();

	boolean hasNext();

	BranchingNode next();

}