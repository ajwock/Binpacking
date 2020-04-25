package binpacking;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import binpacking.interfaces.BinPackingInstance;
import binpacking.interfaces.BinPackingSolution;
import binpacking.interfaces.BranchingNode;
import binpacking.interfaces.Change;
import binpacking.interfaces.MutableBinPackingInstance;

/**
 * Handles the nodes at which the knapsack branches at
 * 
 * @author Gabe Reynolds and Drew Wock
 *
 */
public class BinPackingNode implements MutableBinPackingInstance, Iterable<BranchingNode>, Iterator<BranchingNode>,
		BinPackingSolution, BranchingNode {
	/** The level of the tree that this node is at. */
	int level;
	/** capacity of an empty bin */
	int binSize;
	BinPackingModel model;
	BinPackingInstance problem;

	/** Item that is chosen by the parent instance to create this node */
	Item item;

	List<Bin> binList;

	List<Item> itemList;

	int binIndex;
	Bin nextBin;
	boolean done;
	boolean isLeaf;

	int upperBound;
	int lowerBound;
	BinPackingHueristic hueristic;
	int remainingItemWeight;
	int remainingSpace;

	public List<Bin> binList() {
		// Adds an extra slot in case an addition need be made.
		ArrayList<Bin> newList = new ArrayList<Bin>(binList.size() + 1);

		for (Bin bin : binList) {
			// Deep clone because bins can change in ways we don't want to put the time into
			// tracking just yet.
			// Bin has a copy constructor.
			newList.add(new Bin(bin));
		}
		return newList;
	}

	public List<Item> itemList() {
//		ArrayList<Item> newList = new ArrayList<Item>(itemList.size());
//		for (Item item : itemList) {
//			// Items thus far have only static state associated with them. No need to clone.
//			newList.add(item);
//		}
//		return newList;
		return itemList;
	}

	public int binSize() {
		return binSize;
	}

	public void applyChanges(List<Change<MutableBinPackingInstance>> changes) {
		for (Change<MutableBinPackingInstance> change : changes) {
			change.applyChange(this);
		}
	}

	public void selectItem() {
		if (!isLeaf) {
			item = itemList.get(itemList.size() - level - 1);
			nextBin();
		} else {
			done = true;
			item = null;
		}
	}

	/**
	 * Creates a node with the given problem, model, and level, as well as a list of
	 * changes to apply to the instance.
	 * 
	 * @param problem The bin packing instance to branch from.
	 * @param model   The model containing what is known so far about the current
	 *                solution.
	 * @param lvl     The level on the tree of this node.
	 * @param changes The set of changes that make this node different from the
	 *                previous.
	 */
	BinPackingNode(BinPackingInstance problem, BinPackingModel model, int lvl,
			List<Change<MutableBinPackingInstance>> changes, BinPackingHueristic hueristic, int remainingItemWeight,
			int remainingSpace) {
		this.problem = problem;
		this.model = model;
		binList = problem.binList();
		binIndex = 0;
		itemList = problem.itemList();
		binSize = problem.binSize();
		this.hueristic = hueristic;
		this.remainingItemWeight = remainingItemWeight;
		this.remainingSpace = remainingSpace;
		upperBound = -1;
		lowerBound = -1;

		applyChanges(changes);
		level = lvl;
		isLeaf = level >= itemList.size();
		if (isLeaf) {
			model.checkSolution(this);
		}
		selectItem();
	}

	/**
	 * Creates a node with the given problem, model, and level.
	 * 
	 * @param problem The bin packing instance to branch from.
	 * @param model   The model containing what is known so far about the current
	 *                solution.
	 * @param lvl     The level on the tree of this node.
	 */
	BinPackingNode(BinPackingInstance problem, BinPackingModel model, int lvl, int remainingWeight,
			int remainingSpace) {
		this(problem, model, lvl, new ArrayList<Change<MutableBinPackingInstance>>(0), new FirstFitHueristic(),
				remainingWeight, remainingSpace);
	}

	/**
	 * Creates a node with the given problem, model, and level.
	 * 
	 * @param problem The bin packing instance to branch from.
	 * @param model   The model containing what is known so far about the current
	 *                solution.
	 * @param lvl     The level on the tree of this node.
	 */
	BinPackingNode(BinPackingInstance problem, BinPackingModel model, int lvl, BinPackingHueristic hueristic,
			int remainingWeight, int remainingSpace) {
		this(problem, model, lvl, new ArrayList<Change<MutableBinPackingInstance>>(0), hueristic, remainingWeight,
				remainingSpace);
	}

	/**
	 * Creates a node with the given problem, model, and level.
	 * 
	 * @param problem The bin packing instance to branch from.
	 * @param model   The model containing what is known so far about the current
	 *                solution.
	 * @param lvl     The level on the tree of this node.
	 */
	BinPackingNode(BinPackingInstance problem, BinPackingModel model, int lvl) {
		this(problem, model, lvl, new ArrayList<Change<MutableBinPackingInstance>>(0), new FirstFitHueristic(), 0, 0);
		for (Item item : itemList) {
			remainingItemWeight += item.getWeight();
		}
		for (Bin bin : binList) {
			remainingSpace += bin.remainingSpace();
		}
	}

	/**
	 * Creates a node with the given problem, model, and level.
	 * 
	 * @param problem The bin packing instance to branch from.
	 * @param model   The model containing what is known so far about the current
	 *                solution.
	 * @param lvl     The level on the tree of this node.
	 */
	BinPackingNode(BinPackingInstance problem) {
		this(problem, new BinPackingModel(), 0, new ArrayList<Change<MutableBinPackingInstance>>(0),
				new FirstFitHueristic(), 0, 0);
		for (Item item : itemList) {
			remainingItemWeight += item.getWeight();
		}
		for (Bin bin : binList) {
			remainingSpace += bin.remainingSpace();
		}
	}

	@Override
	public void destructor() {
		;
	}

	@Override
	public int upperBound() {
		// Upper bound can be an expensive operation- some basic memoization in case we
		// want to see it multiple times.
		if (upperBound == -1) {
			// Apply an appromixation hueristic to the remaining instance.
			BinPackingNode temp = new BinPackingNode(this, model, level, hueristic, remainingItemWeight,
					remainingSpace);
			BinPackingSolution result = hueristic.apply(temp);
			List<Bin> solutionList = result.getSolution();
			int ub = solutionList.size();
			/** Pass in the result as a possible solution. */
			model.checkSolution(result);
			model.trySetUpperBound(ub);
			upperBound = model.getUpperBound();
		}
		return upperBound;
	}

	@Override
	public int lowerBound() {
		if (lowerBound == -1) {
			int difference = remainingItemWeight - remainingSpace;
			int minExtraBins = difference > 0 ? (int) Math.ceil((double) difference / binSize()) : 0;
			lowerBound = minExtraBins + binList.size();
		}
		return lowerBound;
	}

	public void invalidateBounds() {
		upperBound = -1;
		lowerBound = -1;
	}

	/**
	 * Only removes the item weight.
	 */
	@Override
	public Item removeItem(int index) {
		Item removed = itemList.get(index);
		remainingItemWeight -= removed.getWeight();
		return removed;
	}

	/**
	 * Simply adds the weight back.
	 */
	@Override
	public void addItem(Item addition) {
//		invalidateBounds();
//		itemList.add(addition);
		remainingItemWeight += addition.getWeight();
	}

	@Override
	public int addToBin(Bin bin, Item addition) {
		invalidateBounds();
		Bin localBin = binList.get(bin.getPosition());
		remainingSpace -= addition.getWeight();
		return localBin.addItem(addition);
	}

	@Override
	public void removeFromBin(Bin bin, int itemPosition) {
		invalidateBounds();
		Bin localBin = binList.get(bin.getPosition());
		remainingSpace += localBin.removeItem(itemPosition).getWeight();
	}

	@Override
	public void addToNewBin(Item addition) {
		invalidateBounds();
		remainingSpace += binSize();
		Bin newBin = new Bin(binSize(), binList.size());
		newBin.addItem(addition);
		binList.add(newBin);
	}

	@Override
	public void removeLastBin() {
		invalidateBounds();
		binList.remove(binList.size() - 1);
	}

	@Override
	public Iterator<BranchingNode> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		return !done;
	}

	public Bin nextBin() {
		Bin currBin = nextBin;
		boolean hasNextBin = false;
		while (binIndex < binList.size()) {
			nextBin = binList.get(binIndex);
			binIndex++;
			if (item.getWeight() <= nextBin.remainingSpace()) {
				hasNextBin = true;
				break;
			}
		}
		if (!hasNextBin) {
			nextBin = null;
		}
		return currBin;
	}

	public BinPackingNode newNode(BinPackingInstance problem, BinPackingModel model, int lvl,
			List<Change<MutableBinPackingInstance>> changes, BinPackingHueristic hueristic, int remainingItemWeight,
			int remainingSpace) {
		return new BinPackingNode(problem, model, lvl, changes, hueristic, remainingItemWeight, remainingSpace);
	}

	@Override
	public BinPackingNode next() {
		Bin bin = nextBin();
		ArrayList<Change<MutableBinPackingInstance>> changes = new ArrayList<Change<MutableBinPackingInstance>>(1);
		if (bin != null) {
			changes.add(new AddToBin(bin, item));
			return newNode(this, this.model, level + 1, changes, hueristic, remainingItemWeight, remainingSpace);
		} else if (!done) {
			done = true;
			changes.add(new NewBin(item));
			return newNode(this, this.model, level + 1, changes, hueristic, remainingItemWeight, remainingSpace);
		}
		throw new NoSuchElementException();
	}

	public boolean isLeaf() {
		return isLeaf;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int l) {
		level = l;
	}

	@Override
	public List<Bin> getSolution() {
		return binList;
	}

	public BinPackingModel getModel() {
		return model;
	}

}