package snippet;

import java.util.ArrayList;

import edu.ncsu.csc316.dsa.queue.ArrayBasedQueue;
import edu.ncsu.csc316.dsa.queue.Queue;

/**
 * Handles the nodes at which the knapsack branches at
 * 
 * @author Gabe Reynolds
 *
 */
public class BinManager {

	/** The capacity of a bin */
	private int capacity;
	/** The number of times a branch occurs */
	private int branches = 0;
	/** An array of all items that can be put in to the knapsack */
	Item[] items;
	/** The total number of items to be put in to bins */
	private int numItems;
	/** Handles the bins */
	ArrayList<Bin> boxOfBins = new ArrayList<Bin>();
	ArrayList<Node> boxOfNodes = new ArrayList<Node>();
	private int numBins;

	/**
	 * Creates a new BinManager object, with the given capacity and the number of
	 * items that can be put into each bin that's created.
	 */
	BinManager(int cap, int items) {
		capacity = cap;
		numItems = items;
		this.items = new Item[items];
	}
	
	public int getCapacity() {
		return this.capacity;
	}

	public int getNumBins() {
		return numBins;
	}

	public int getNumItems() {
		return this.numItems;
	}

	public void addItem(Item g) {
		items[g.itemNum - 1] = g;
	}

	public int getBranches() {
		return branches;
	}

	public void addBin() {
		Bin k = new Bin(capacity);
		boxOfBins.add(k);
	}

}