package snippet;

import java.util.ArrayList;
import java.lang.Integer;

public class Bin {
	/** The remaining space of the bin */
	private Integer remainingSpace;
	/** The total number of items that are currently packed in the bin */
	private Integer packed;
	/** An ArrayList of items in this bin */
	private ArrayList<Item> packedList;

	Bin(int capacity) {
		packedList = new ArrayList<Item>();
		remainingSpace = capacity;
	}
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	Bin(Bin bin) {
		//Intentional copy-by-value use of Integer constructor.
		this.remainingSpace = new Integer(bin.remainingSpace.intValue());
		this.packed = new Integer(bin.packed.intValue());
		this.packedList = (ArrayList<Item>) bin.packedList.clone();
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
