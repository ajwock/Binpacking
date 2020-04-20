package snippet;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class FirstFitHueristic extends BinPackingHueristic {

	private List<Bin> unrefined(BinPackingInstance instance) {
		List<Bin> binList = instance.binList();
		Queue<Item> itemQueue = (Queue<Item>) new ArrayList(instance.itemList());
		int binSize = instance.binSize();
		while (!itemQueue.isEmpty()) {
			Item item = itemQueue.remove();
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
		return binList;
	}
	
	@Override
	public List<Bin> apply(BinPackingInstance instance) {
		return unrefined(instance);
	}
	
}
