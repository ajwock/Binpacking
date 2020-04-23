package snippet;

import java.util.Iterator;

public interface Node extends Iterator<Node>, Iterable<Node> {

	void destructor();
	
	boolean isLeaf();

	int upperBound();

	int lowerBound();

	Iterator<Node> iterator();

	boolean hasNext();

	Node next();

}