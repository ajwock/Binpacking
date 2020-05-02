package binpacking.model;

import java.util.ArrayList;

/**
 * The bin class that handles the properties of each bin
 * and how items are stored in bins.
 * @author Drew Wock and Gabe Reynolds
 *
 */
public class Bin {
	/** The remaining space of the bin */
	private Integer remainingSpace;
	/** The total number of items that are currently packed in the bin */
	private Integer packed;
	/** An ArrayList of items in this bin */
	private ArrayList<Item> packedList;
	/** The position of this bin **/
	private Integer position;
	
	/**
	 * Creates a new bin with the given capacity and the given position
	 * @param capacity the given capacity
	 * @param position the given position
	 */
	public Bin(int capacity, int position) {
		packedList = new ArrayList<Item>();
		packed = 0;
		remainingSpace = capacity;
		this.position = position;
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	/**
	 * Creates a deep-ish copy of the given bin
	 * @param bin the given bin
	 */
	public Bin(Bin bin) {
		// Intentional copy-by-value use of Integer constructor.
		this.remainingSpace = new Integer(bin.remainingSpace.intValue());
		this.packed = new Integer(bin.packed.intValue());
		this.position = new Integer(bin.position.intValue());
		this.packedList = (ArrayList<Item>) bin.packedList.clone();
	}
	
	/**
	 * Gets the remaining space in this bin
	 * @return the remaining space in this bin
	 */
	public Integer remainingSpace() {
		return this.remainingSpace;
	}
	
	/**
	 * Gets the space packed in this bin
	 * @return the space packed in this bin
	 */
	public Integer getPacked() {
		return this.packed;
	}
	
	/**
	 * Returns the position of this bin
	 * @return the position of this bin
	 */
	public Integer getPosition() {
		return this.position;
	}
	
	/**
	 * Adds the given item to this bin
	 * @param g the given item
	 * @return the position of the added item
	 */
	public Integer addItem(Item g) {
		packedList.add(g);
		packed++;
		remainingSpace -= g.getWeight();
		int itemPosition = packedList.size() - 1;
		return itemPosition;
	}
	
	/**
	 * Removes the item at the given position from this bin
	 * @param itemPosition the given position
	 * @return the removed item
	 */
	public Item removeItem(int itemPosition) {
		Item item = packedList.remove(itemPosition);
		packed--;
		remainingSpace += item.getWeight();
		return item;
	}
	
	/**
	 * Prints out the items stored in this bin,
	 * as well as the position of this bin
	 */
	public void printContents() {
		System.out.println("  Bin #: " + this.position);
		for (int i = 0; i < packedList.size(); i++) {
			System.out.println("		" + packedList.get(i).getItemNum());
		}
	}

	public String toString() {
		return "(Bin: " + position.toString() + " packedList:" + packedList + " )";
	}

	@Override
	public int hashCode() {
		return this.remainingSpace();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bin other = (Bin) obj;
		if (packed == null) {
			if (other.packed != null)
				return false;
		} else if (!packed.equals(other.packed))
			return false;
		if (packedList == null) {
			if (other.packedList != null)
				return false;
		} else if (!packedList.equals(other.packedList))
			return false;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		if (remainingSpace == null) {
			if (other.remainingSpace != null)
				return false;
		} else if (!remainingSpace.equals(other.remainingSpace))
			return false;
		return true;
	}
	
	/**
	 * Sets this bin's position to the given one
	 * @param pos the given bin position
	 */
	public void setPosition(Integer pos) {
		this.position = pos;
	}

}
