package binpacking.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import binpacking.interfaces.BinPackingHueristic;
import binpacking.interfaces.BinPackingSolution;
import binpacking.interfaces.DeepCopyableMutableBinPackingInstance;
import binpacking.interfaces.DynamicBinPackingInstance;
import binpacking.interfaces.MutableBinPackingInstance;
import general.interfaces.DeepCopyable;

/**
 * The implementation of the First Fit Heuristic
 * @author Drew Wock
 *
 */
public class FirstFitHueristic implements BinPackingHueristic {

	/**
	 * Changes the whole instance, but said changes are revertible in
	 * ChangeStack style. A simple call to popChangeFrame will undo
	 * the approximation.
	 * 
	 * @param instance The BP instance.
	 * @return An approximate solution to the BP problem.
	 */
	private BinPackingSolution revertible(DynamicBinPackingInstance instance) {
		Stack<Item> itemStack = new Stack<Item>();
		for (Item item : instance.remainingItemList()) {
			itemStack.push(item);
		}

		instance.newChangeFrame();
		while (!itemStack.isEmpty()) {
			Item item = itemStack.pop();
			boolean placed = false;
			for (Bin bin : instance.binList()) {
				if (item.getWeight() <= bin.remainingSpace()) {
					instance.pushChange(new MutableBinPackingInstance.AddToBin(bin, item));
					placed = true;
					break;
				}
			}
			if (!placed) {
				instance.pushChange(new MutableBinPackingInstance.NewBin(item));
			}
		}
		return instance;
	}

	/**
	 * Called if the instance is known to be deepCopyable.
	 * This will copy the instance before passing it to clobber.
	 * 
	 * @param instance The instance to approximate.
	 * @return The approximate solution.
	 */
	private BinPackingSolution noClobber(DeepCopyableMutableBinPackingInstance instance) {
		return clobber(instance.deepCopy());
	}

	/**
	 * This approximation simply clobbers whatever instance is passed into it with
	 * the approximation. Recommended to use a copy constructor if this is being
	 * used for lower bound use.
	 * 
	 * @param instance
	 * @return An approximate solution to the
	 */
	private BinPackingSolution clobber(MutableBinPackingInstance instance) {
		List<Bin> binList = instance.binList();
		List<Item> itemQueue = new ArrayList<Item>(instance.remainingItemList());
		int binSize = instance.binSize();
		while (!itemQueue.isEmpty()) {
			Item item = itemQueue.remove(itemQueue.size() - 1);
			boolean placed = false;
			for (Bin bin : binList) {
				if (item.getWeight() <= bin.remainingSpace()) {
					bin.addItem(item);
					placed = true;
					break;
				}
			}
			if (!placed) {
				Bin newBin = new Bin(binSize, binList.size());
				newBin.addItem(item);
				binList.add(newBin);
			}
		}
		BinPackingSolution solved = new BinPackingSolution() {
			@Override
			public List<Bin> getSolution() {
				// TODO Auto-generated method stub
				return binList;
			}
		};
		return solved;
	}

	@Override
	public BinPackingSolution apply(MutableBinPackingInstance instance) {
		if (instance instanceof DynamicBinPackingInstance) {
			return revertible((DynamicBinPackingInstance) instance);
		} else if (instance instanceof DeepCopyableMutableBinPackingInstance) {
			return noClobber((DeepCopyableMutableBinPackingInstance) instance);
			
		} else {
			return clobber(instance);
		}
	}

}
