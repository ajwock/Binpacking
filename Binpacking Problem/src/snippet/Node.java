package snippet;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Handles the nodes at which the knapsack branches at
 * 
 * @author Gabe Reynolds
 *
 */
public class Node implements Iterable<Node>, Iterator<Node> {
	/** The level of the tree that this node is at. Each level represents an item */
	int level;
	/** The upper bound of the branch this node represents */
	double bound;
	
	Item item;
	
	ArrayList<Bin> binList;
	Iterator<Bin> binsListIterator;
	Bin nextBin;

	/**
	 * Creates a node with the given level, profit, and weight.
	 * 
	 * @param lvl    the given level
	 * @param money  the given profit
	 * @param weight the given weight
	 */
	Node(int lvl, int bins) {
		level = lvl;
		bound = 0;
		binsListIterator = binList.iterator();
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int l) {
		level = l;
	}

	public double getLowerBound() {
		return bound;
	}

	public void setBound(double b) {
		bound = b;
	}

	@Override
	public Iterator<Node> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		return nextBin == null;
	}

	@Override
	public Node next() {
		Bin currBin = nextBin;
		while (binsListIterator.hasNext()) {
			nextBin = binsListIterator.next();
			if (item.getWeight() <= nextBin.remainingSpace())
				break;
		}
		if (!binsListIterator.hasNext()) {
			nextBin = null;
		}
		//TODO This is far from correct but still useful
		return new Node(0, 0);
	}
}