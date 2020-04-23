package binpacking;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import binpacking.interfaces.BinPackingInstance;
import binpacking.interfaces.BinPackingSolution;
import binpacking.interfaces.DynamicBinPackingInstance;
import binpacking.interfaces.MutableBinPackingInstance;

public class FirstFitHueristic extends BinPackingHueristic {

	/**
	 * Changes the whole instance, but said changes are recorded in DynamicBinPackingInstance style.
	 * A simple call to popChangeFrame will undo the approximation.
	 * 
	 * @param instance The BP instance.
	 * @return An approximate solution to the BP problem.
	 */
	private BinPackingSolution safe(DynamicBinPackingInstance instance) {
		List<Item> itemList = instance.itemList();
		int binSize = instance.binSize();
		for (Item item : itemList) {
			boolean placed = false;
			for (Bin bin : instance.binList()) {
				if (item.getWeight() < bin.remainingSpace()) {
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
	 * This approximation simply clobbers whatever instance is passed into it with the approximation.
	 * Recommended to use a copy constructor if this is being used for lower bound use.
	 * 
	 * @param instance
	 * @return An approximate solution to the 
	 */
	private BinPackingSolution unsafe(BinPackingInstance instance) {
		List<Bin> binList = instance.binList();
		List<Item> itemQueue = new ArrayList<Item>(instance.itemList());
		int binSize = instance.binSize();
		while (!itemQueue.isEmpty()) {
			Item item = itemQueue.remove(itemQueue.size() - 1);
			boolean placed = false;
			for (Bin bin : binList) {
				if (item.getWeight() < bin.remainingSpace()) {
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
	public BinPackingSolution apply(BinPackingInstance instance) {
		if (instance instanceof DynamicBinPackingInstance) {
			return safe((DynamicBinPackingInstance) instance);
		} else {
			return unsafe(instance);
		}
	}
	
}
