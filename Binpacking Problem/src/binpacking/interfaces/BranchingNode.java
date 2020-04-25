package binpacking.interfaces;

import java.util.Iterator;

public interface BranchingNode extends Iterator<BranchingNode>, Iterable<BranchingNode> {

	void destructor();
	
	boolean isLeaf();

	int upperBound();

	int lowerBound();
	
	int level();

	Iterator<BranchingNode> iterator();

	boolean hasNext();

	BranchingNode next();

}