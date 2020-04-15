package snippet;

import java.util.ArrayList;

public class Bin {
	/** The capacity of the bin */
	private int cap;
	/** The total weight of items that are currently stored in this bin */
	private int weight;
	/** The total number of items that are currently packed in the bin */
	private int packed;
	/** An ArrayList of items in this bin */
	ArrayList<Item> packedList;

	Bin(int capacity) {
		cap = capacity;
		packedList = new ArrayList<Item>();
		packed = 0;
		weight = 0;
	}

	public int remainingSpace() {
		return cap-weight;
	}
	
	private int gotSpace(int itemWeight) {
		int free = cap - weight;
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
