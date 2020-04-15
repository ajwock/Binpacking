package snippet;

import java.util.ArrayList;

public class Bin implements Cloneable{
	/** The remaining space of the bin */
	private int remainingSpace;
	/** The total number of items that are currently packed in the bin */
	private int packed;
	/** An ArrayList of items in this bin */
	ArrayList<Item> packedList;

	Bin(int capacity) {
		packedList = new ArrayList<Item>();
		remainingSpace = capacity;
	}

	public int remainingSpace() {
		return remainingSpace;
	}

	public void addItem(Item g) {
		packedList.add(g);
		packed++;
		remainingSpace -= g.getWeight();
	}

	public int getPacked() {
		return this.packed;
	}
}
