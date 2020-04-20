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
	/** TODO: Change to position.  For now will be an index. **/
	private Integer position;

	Bin(int capacity, int position) {
		packedList = new ArrayList<Item>();
		remainingSpace = capacity;
		this.position = position;
	}
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	Bin(Bin bin) {
		//Intentional copy-by-value use of Integer constructor.
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

	public void addItem(Item g) {
		packedList.add(g);
		packed++;
		remainingSpace -= g.getWeight();
	}

	/**
	 * Interestingly, this hashCode can change since the remainingSpace can change as well.  However, this is relevant to the hueristic where we don't check
	 * bins with the same amount of remaining space for a branch.
	 */
	public int hashCode() {
		return remainingSpace();
	}
}
