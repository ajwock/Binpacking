package binpacking.model;
/**
 * Handles the representation of items
 * @author Drew Wock and Gabe Reynolds
 *
 */
public class Item implements Comparable<Item> {
	/** The weight of this item */
	public int weight;
	/** The ratio of the item's value over the weight */
	public double ratio;
	/** The unique number assigned to this item */
	public int itemNum;
	/** The position of the item in the itemList. */
	public Integer position;
	
	/**
	 * Gets the position of this item
	 * @return the position of this item
	 */
	public Integer getPosition() {
		return position;
	}

	/**
	 * Creates a new item object with the given item number, value, and weight
	 * 
	 * @param num the given item number
	 * @param w   the given item weight
	 */
	public Item(int num, int w) {
		itemNum = num;
		weight = w;
	}
	
	/**
	 * Gets the weight of this item
	 * @return the weight of this item
	 */
	public int getWeight() {
		return this.weight;
	}
	
	/**
	 * Gets the item number for this item
	 * @return the item number for this item
	 */
	public int getItemNum() {
		return this.itemNum;
	}

	// Compares the ratio of this item to the given item
	@Override
	public int compareTo(Item o) {
		if (this.weight < o.weight) {
			return -1;
		}
		if (this.weight > o.weight) {
			return 1;
		}
		return 0;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Item)) {
			return false;
		}
		Item other = (Item) o;
		if (this.compareTo(other) == 0) {
			return true;
		}
		System.out.println("Difference detected- expected: " + this + " Got: " + other);
		return false;
	}

	/**
	 * A perfect hash, particularly for sets. Effective equality is determined
	 * exactly by this hash.
	 */
	@Override
	public int hashCode() {
		return getWeight();
	}

	public String toString() {
		return "(Item: " + itemNum + " " + weight + ")";
	}
	
	/**
	 * Sets the position of this item to the given position
	 * @param pos the given position
	 */
	public void setPosition(int pos) {
		this.position = pos;
	}

}
