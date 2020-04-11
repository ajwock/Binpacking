package snippet;

/**
 * Handles the nodes at which the knapsack branches at
 * 
 * @author Gabe Reynolds
 *
 */
public class Node {
	/** The level of the tree that this node is at */
	int level;
	/** The value of the knapsack this node represents */
	long profit;
	/** The upper bound of the branch this node represents */
	double bound;
	/** The total weight of all the items in this branch */
	int weight;
	/** The number of items in this branch */
	int numItems;
	// negative if it was a path that removed an item
	// positive if it was a path that added an item
	// 0 if it is neither
	int negation;
	// The item who caused a branch to occur
	Item split;

	/**
	 * Creates a node with the given level, profit, and weight.
	 * 
	 * @param lvl    the given level
	 * @param money  the given profit
	 * @param weight the given weight
	 */
	Node(int lvl, long money, int weight) {
		level = lvl;
		profit = money;
		this.weight = weight;
		bound = 0;
		numItems = 0;
	}

	public int getNumItems() {
		return numItems;
	}

	public void setNumItems(int n) {
		numItems = n;
	}

	public void setNegation(int n) {
		negation = n;
	}

	public void setSplit(Item i) {
		split = i;
	}

	public int getNegation() {
		return negation;
	}

	public Item getSplit() {
		return split;
	}

	public int getLevel() {
		return level;
	}

	public long getProfit() {
		return profit;
	}

	public int getWeight() {
		return this.weight;
	}

	public void setWeight(int w) {
		weight = w;
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

	public void setProfit(long p) {
		profit = p;
	}
}