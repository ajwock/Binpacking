package snippet;

import java.util.List;

/**
 * Abstract class that represents an instance of the Bin Packing problem.
 * 
 * The reason for this class's extensibility is that some instances will be sub-instances while others will be base instances, which may
 * have significantly different behavior.
 * 
 * @author Andrew Wock
 *
 */
public abstract class BinPackingInstance {
	
	/**
	 * Returns the list of bins in the instance.  At this level of abstraction, whether this is a clone of the list of bins or a shared internal list is ambiguous.
	 * @return The list of bins in the instance.
	 */
	public abstract List<Bin> binList();
	
	/**
	 * Returns the list of items in the instance.  At this level of abstraction, whether this is a clone of the list of bins or a shared internal list is ambiguous.
	 * @return The list of items in the instance.
	 */
	public abstract List<Item> itemList();
	
	public abstract int binSize();

	/**
	 * Calculates the upper bound for how many bins could be in the optimal solution.
	 * In other words, this value is how "bad" the optimal solution could be based on known information.
	 * 
	 * @return The upper bound.
	 */
	public abstract int upperBound();

	/**
	 * Calculates the lower bound for how many bins could be in the optimal solution.
	 * In other words, based on what is known so far- particularly, what is known about the current instance-
	 * the optimal solution could be no "better" than this value.
	 * 
	 * @return The lower bound.
	 */
	public abstract int lowerBound();

	public abstract void addToBin(Bin bin, Item addition);

	public abstract void addToNewBin(Item addition);
	
	public abstract void removeItem(Item removal);

	/**
	 * Interface for applying various changes to a BinPackingInstance.  When this is available is up to subclasses-
	 * This will be available in a branch and bound node at construction time, for example.
	 * 
	 * In faster implementations, the instance may store the change and reverse it at destruction time.
	 * 
	 * Instances may not want to use the standard "applyChange" method, so it's best to allow liberal accessibility in
	 * in subclasses.
	 * 
	 * @author Andrew Wock
	 *
	 */
	protected abstract class Change {
		/**
		 * Applies the change to the given instance of a bin packing problem.
		 * 
		 * @param instance 
		 */
		public abstract void applyChange(BinPackingInstance instance);
	}

	/**
	 * Change in which the bin has the item added to it.
	 * 
	 * @author Andrew Wock
	 */
	protected class AddToBin extends Change {
		public Bin bin;
		public Item addition;

		/**
		 * Construct an AddToBin change.
		 * 
		 * @param bin The Bin to add to.
		 * @param addition The item to add.
		 */
		public AddToBin(Bin bin, Item addition) {
			this.bin = bin;
			this.addition = addition;
		}

		public void applyChange(BinPackingInstance instance) {
			instance.addToBin(bin, addition);
			instance.removeItem(addition);
		}

	}

	/**
	 * Creates a new bin and adds the item to it.
	 * 
	 * @author Andrew Wock
	 *
	 */
	protected class NewBin extends Change {
		public Item addition;

		/**
		 * Construct a NewBin change.
		 * 
		 * @param addition The item to add.
		 */
		public NewBin(Item addition) {
			this.addition = addition;
		}
		
		public void applyChange(BinPackingInstance instance) {
			instance.addToNewBin(addition);
			instance.removeItem(addition);
		}
	}

}
