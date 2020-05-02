package binpacking.interfaces;

import java.util.List;

import binpacking.model.Bin;
import binpacking.model.Item;
import general.interfaces.Change;

/**
 * Interface that represents an instance of the Bin Packing problem.
 * 
 * The reason for this class's extensibility is that some instances will be
 * sub-instances while others will be base instances, which may have
 * significantly different behavior.
 * 
 * @author Drew Wock
 *
 */
public interface MutableBinPackingInstance extends BinPackingSolution, BinPackingInstance, Cloneable {
	/**
	 * Adds the item with the given position to the given bin
	 * @param bin the given bin
	 * @param addition the given item's position
	 * @return the position of the added item
	 */
	public abstract int addToBin(Bin bin, Item addition);
	
	/**
	 * Creates a new bin, and then adds the given item
	 * to the new bin
	 * @param addition the given item's position
	 */
	public abstract void addToNewBin(Item addition);
	
	/**
	 * Removes the given item from the item list
	 * @param index the given item's position
	 * @return the removed item
	 */
	public abstract Item removeItem(int index);
	
	/**
	 * Adds the given item to the item list
	 * @param addition the given item
	 */
	public abstract void addItem(Item addition);
	
	/**
	 * Removes the given item from the given bin
	 * @param bin the given bin
	 * @param itemPosition the position of the given item
	 */
	public abstract void removeFromBin(Bin bin, int itemPosition);
	
	/**
	 * Removes the last bin from the list
	 */
	public abstract void removeLastBin();
	
	/**
	 * Gets the list of items that still need to be packed
	 * @return the list of items that still need to be packed
	 */
	public abstract List<Item> remainingItemList();

	/**
	 * Change in which the bin has the item added to it.
	 * 
	 * @author Drew Wock
	 */
	class AddToBin implements Change<MutableBinPackingInstance> {
		public Bin bin;
		public Item addition;
		int itemPosition;

		/**
		 * Construct an AddToBin change.
		 * 
		 * @param bin      The Bin to add to.
		 * @param addition The item to add.
		 */
		public AddToBin(Bin bin, Item addition) {
			this.bin = bin;
			this.addition = addition;
		}
		
		/**
		 * Applies the changes to the given bin packing instance
		 * @param instance the given bin packing instance
		 */
		public void applyChange(MutableBinPackingInstance instance) {
			itemPosition = instance.addToBin(bin, addition);
			instance.removeItem(addition.getPosition());
		}
		
		/**
		 * Reverses the changes to the given bin packing instance
		 * @param instance the given bin packing instance
		 */
		public void reverseChange(MutableBinPackingInstance instance) {
			instance.addItem(addition);
			instance.removeFromBin(bin, itemPosition);
		}

		public String toString() {
			return "(AddToBin: " + addition + " to bin " + bin.getPosition() + ")";
		}

	}

	/**
	 * Creates a new bin and adds the item to it.
	 * 
	 * @author Drew Wock
	 *
	 */
	class NewBin implements Change<MutableBinPackingInstance> {
		public Item addition;

		/**
		 * Construct a NewBin change.
		 * 
		 * @param addition The item to add.
		 */
		public NewBin(Item addition) {
			this.addition = addition;
		}
		
		/**
		 * Applies the changes to the given bin packing instance
		 * @param instance the given bin packing instance
		 */
		public void applyChange(MutableBinPackingInstance instance) {
			instance.addToNewBin(addition);
			instance.removeItem(addition.getPosition());
		}
		
		/**
		 * Reverses the changes to the given bin packing instance
		 * @param instance the given bin packing instance
		 */
		@Override
		public void reverseChange(MutableBinPackingInstance instance) {
			instance.addItem(addition);
			instance.removeLastBin();
		}

		public String toString() {
			return "(NewBin: " + addition + ")";
		}
	}

}
