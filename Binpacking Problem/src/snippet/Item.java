package snippet;

public class Item implements Comparable<Item>{
	/** The weight of this item*/
	public int weight;
	/** The ratio of the item's value over the weight */
	public double ratio;
	/** The unique number assigned to this item */
	public int itemNum;
	
	/**
	 * Creates a new item object with the given item number, value, and weight
	 * @param num the given item number
	 * @param w the given item weight
	 */
	Item(int num, int w) {
		itemNum = num;
		weight = w;
	}
	
	public int getWeight() {
		return this.weight;
	}
	
	public int getItemNum() {
		return this.itemNum;
	}
	
	// Compares the ratio of this item to the given item
	@Override
	public int compareTo(Item o) {
		if (this.weight < o.weight) {
			return 1;
		}
		if (this.weight > o.weight) {
			return -1;
		}
		return 0;
	}
}
