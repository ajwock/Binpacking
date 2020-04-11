package snippet;

public class Item implements Comparable<Item>{
	/** The weight of this item*/
	public int weight;
	/** The value of this item */
	public long value;
	/** The ratio of the item's value over the weight */
	public double ratio;
	/** The unique number assigned to this item */
	public int itemNum;
	
	/**
	 * Creates a new item object with the given item number, value, and weight
	 * @param num the given item number
	 * @param v the given item value
	 * @param w the given item weight
	 */
	Item(int num, int v, int w) {
		itemNum = num;
		value = v;
		weight = w;
		ratio = (double) v / (double) w;
	}
	
	public int getWeight() {
		return this.weight;
	}
	
	public long getValue() {
		return this.value;
	}
	
	public double getRatio() {
		return this.ratio;
	}
	
	public int getItemNum() {
		return this.itemNum;
	}
	
	// Compares the ratio of this item to the given item
	@Override
	public int compareTo(Item o) {
		if (this.ratio < o.ratio) {
			return 1;
		}
		if (this.ratio > o.ratio) {
			return -1;
		}
		return 0;
	}
}
