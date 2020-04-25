package binpacking.model;

import java.util.ArrayList;

public class Bin {
	/** The remaining space of the bin */
	private Integer remainingSpace;
	/** The total number of items that are currently packed in the bin */
	private Integer packed;
	/** An ArrayList of items in this bin */
	private ArrayList<Item> packedList;
	/** TODO: Change to position. For now will be an index. **/
	private Integer position;

	public Bin(int capacity, int position) {
		packedList = new ArrayList<Item>();
		packed = 0;
		remainingSpace = capacity;
		this.position = position;
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	public Bin(Bin bin) {
		// Intentional copy-by-value use of Integer constructor.
		this.remainingSpace = new Integer(bin.remainingSpace.intValue());
		this.packed = new Integer(bin.packed.intValue());
		this.position = new Integer(bin.position.intValue());
		this.packedList = (ArrayList<Item>) bin.packedList.clone();
	}

	public Integer remainingSpace() {
		return this.remainingSpace;
	}

	public Integer getPacked() {
		return this.packed;
	}

	public Integer getPosition() {
		return this.position;
	}

	public Integer addItem(Item g) {
		packedList.add(g);
		packed++;
		remainingSpace -= g.getWeight();
		int itemPosition = packedList.size() - 1;
		return itemPosition;
	}

	public Item removeItem(int itemPosition) {
		Item item = packedList.remove(itemPosition);
		packed--;
		remainingSpace += item.getWeight();
		return item;
	}

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

}
