package binpacking.interfaces;

import java.util.List;

import binpacking.model.Bin;
import binpacking.model.Item;

/**
 * Interface that represents an instance of the Bin Packing problem.
 * 
 * The reason for this class's extensibility is that some instances will be
 * sub-instances while others will be base instances, which may have
 * significantly different behavior.
 * 
 * @author Andrew Wock
 *
 */
public interface MutableBinPackingInstance extends BinPackingSolution, BinPackingInstance {

	public abstract int addToBin(Bin bin, Item addition);

	public abstract void addToNewBin(Item addition);

	public abstract Item removeItem(int index);

	public abstract void addItem(Item addition);

	public abstract void removeFromBin(Bin bin, int itemPosition);

	public abstract void removeLastBin();

	public abstract List<Item> remainingItemList();

	/**
	 * Change in which the bin has the item added to it.
	 * 
	 * @author Andrew Wock
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

		public void applyChange(MutableBinPackingInstance instance) {
			itemPosition = instance.addToBin(bin, addition);
			instance.removeItem(addition.getPosition());
		}

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
	 * @author Andrew Wock
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

		public void applyChange(MutableBinPackingInstance instance) {
			instance.addToNewBin(addition);
			instance.removeItem(addition.getPosition());
		}

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
