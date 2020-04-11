package snippet;

/**
 * Handles the nodes at which the knapsack branches at
 * 
 * @author Gabe Reynolds
 *
 */
public class Node {
	/** The level of the tree that this node is at. Each level represents an item */
	int level;
	/** Number of bins in this branch */
	int bins;
	/** The upper bound of the branch this node represents */
	double bound;

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
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int l) {
		level = l;
	}

	public double getBound() {
		return bound;
	}

	public void setBound(double b) {
		bound = b;
	}
}