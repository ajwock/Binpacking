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
	ArrayList<Bins> boxOfBins = new ArrayList<Bins>();
	private int filledWeight = 0;
	private int totalWeight = 0;
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
		totalWeight += g.weight;
	}

	public int getBranches() {
		return branches;
	}

	public int getFilled() {
		return filledWeight;
	}

	public int getTotalWeight() {
		return totalWeight;
	}

	public void addBin() {
		Bins k = new Bins();
		boxOfBins.add(k);
	}
	
	// TODO add branching algorithm here!!!!

	private class Bins {
		/** The total weight of items that are currently stored in this bin */
		private int weight;
		/** The total number of items that are currently packed in the bin */
		private int packed;
		/** An ArrayList of items in this bin */
		ArrayList<Item> packedList;

		Bins() {
			packedList = new ArrayList<Item>();
			packed = 0;
			weight = 0;
		}

		public int gotSpace(int itemWeight) {
			int free = capacity - weight;
			if (free >= itemWeight) {
				return free;
			} else {
				return -1;
			}
		}

		public void addItem(Item g) {
			if (gotSpace(g.getWeight()) != -1) {
				this.weight += g.getWeight();
				packedList.add(g);
				packed++;
			}
		}

		public int getPacked() {
			return this.packed;
		}

		public int getWeight() {
			return weight;
		}

	}

}