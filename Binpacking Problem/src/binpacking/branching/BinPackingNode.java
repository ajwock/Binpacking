package binpacking.branching;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import binpacking.interfaces.BinPackingHueristic;
import binpacking.interfaces.BinPackingInstance;
import binpacking.interfaces.BinPackingSolution;
import binpacking.interfaces.DeepCopyableMutableBinPackingInstance;
import binpacking.interfaces.MutableBinPackingInstance;
import binpacking.model.Bin;
import binpacking.model.BinPackingModel;
import binpacking.model.FirstFitHueristic;
import binpacking.model.Item;
import bnb.interfaces.BoundCalculator;
import bnb.interfaces.BranchingNode;
import general.interfaces.Change;
import general.interfaces.DeepCopyable;

/**
 * Handles the nodes at which the knapsack branches at
 * 
 * @author Gabe Reynolds and Drew Wock
 *
 */
public class BinPackingNode implements DeepCopyableMutableBinPackingInstance, Iterable<BranchingNode>,
		Iterator<BranchingNode>, BinPackingSolution, BranchingNode {
	/** The level of the tree that this node is at. */
	int level;
	/** capacity of an empty bin */
	int binSize;
	BinPackingModel model;
	BinPackingInstance problem;

	/** Item that is chosen by the parent instance to create this node */
	Item item;

	List<Bin> binList;

	public List<Item> itemList;

	List<Change<MutableBinPackingInstance>> changes;

	int binIndex;
	Bin nextBin;
	boolean done;
	boolean isLeaf;

	int upperBound;
	int lowerBound;
	BinPackingHueristic hueristic;
	int remainingItemWeight;
	int remainingSpace;

	BoundCalculator<BinPackingNode, Integer> upperBoundCalculator;
	BoundCalculator<BinPackingNode, Integer> lowerBoundCalculator;

	public int level() {
		return level;
	}

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

	public List<Bin> binListDirect() {
		return binList;
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

	public List<Item> remainingItemList() {
		List<Item> smallList = new ArrayList<Item>(itemList.size() - level - 1);
		int top = itemList.size() - level;
		for (int i = 0; i < top; i++) {
			smallList.add(itemList.get(i));
		}
		return smallList;
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

	public void assignItemPositions(List<Item> items) {
		Collections.sort(items);
		for (int i = 0; i < items.size(); i++) {
			items.get(i).setPosition(i);
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
	public BinPackingNode(BinPackingInstance problem, BinPackingModel model, int lvl,
			List<Change<MutableBinPackingInstance>> changes, BinPackingHueristic hueristic, int remainingItemWeight,
			int remainingSpace) {
		this.problem = problem;
		this.model = model;
		level = lvl;
		itemList = problem.itemList();
		binList = problem.binList();
		binSize = problem.binSize();
		isLeaf = level >= itemList.size();

		binIndex = 0;

		this.hueristic = hueristic;
		this.remainingItemWeight = remainingItemWeight;
		this.remainingSpace = remainingSpace;
		upperBound = -1;
		lowerBound = -1;

		this.changes = changes;
		applyChanges(changes);

		if (level == 0) {
			assignItemPositions(itemList);
		}
		if (isLeaf) {
			model.checkSolution(this);
		}
		selectItem();
	}

	protected void initializeBoundCalculators() {
		upperBoundCalculator = new SimpleFFUBCalculator();
		lowerBoundCalculator = new SimpleFFLBCalculator();
	}

	public void setUpperBoundCalculator(BoundCalculator<BinPackingNode, Integer> ubc) {
		this.upperBoundCalculator = ubc;
	}

	public void setLowerBoundCalculator(BoundCalculator<BinPackingNode, Integer> lbc) {
		this.lowerBoundCalculator = lbc;
	}

	/**
	 * Creates a node with the given problem, model, and level.
	 * 
	 * @param problem The bin packing instance to branch from.
	 * @param model   The model containing what is known so far about the current
	 *                solution.
	 * @param lvl     The level on the tree of this node.
	 */
	public BinPackingNode(BinPackingInstance problem, BinPackingModel model, int lvl, int remainingWeight,
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
	public BinPackingNode(BinPackingInstance problem, BinPackingModel model, int lvl, BinPackingHueristic hueristic,
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
	public BinPackingNode(BinPackingInstance problem, BinPackingModel model, int lvl) {
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
	public BinPackingNode(BinPackingInstance problem) {
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
	public int upperBound() {
		return upperBoundCalculator.bound(this);
	}

	@Override
	public int lowerBound() {
		return lowerBoundCalculator.bound(this);
	}

	public int getRemainingItemWeight() {
		return remainingItemWeight;
	}

	public int getRemainingSpace() {
		return remainingSpace;
	}

	public void invalidateBounds() {
		upperBoundCalculator.invalidateBound();
		lowerBoundCalculator.invalidateBound();
	}

	public void setRemainingItemWeight(int weight) {
//		if (weight < 0) {
//			throw new IllegalArgumentException();
//		}
		this.remainingItemWeight = weight;
	}

	/**
	 * Only removes the item weight.
	 */
	@Override
	public Item removeItem(int index) {
		Item removed = itemList.get(index);
		setRemainingItemWeight(remainingItemWeight - removed.getWeight());
		return removed;
	}

	/**
	 * Simply adds the weight back.
	 */
	@Override
	public void addItem(Item addition) {
//		invalidateBounds();
//		itemList.add(addition);
		setRemainingItemWeight(remainingItemWeight + addition.getWeight());
	}

	public void setRemainingSpace(int space) {

		this.remainingSpace = space;
	}

	@Override
	public int addToBin(Bin bin, Item addition) {
		invalidateBounds();
		Bin localBin = binList.get(bin.getPosition());
		setRemainingSpace(remainingSpace - addition.getWeight());
		return localBin.addItem(addition);
	}

	@Override
	public void removeFromBin(Bin bin, int itemPosition) {
		invalidateBounds();
		Bin localBin = binList.get(bin.getPosition());
		setRemainingSpace(remainingSpace + localBin.removeItem(itemPosition).getWeight());
	}

	@Override
	public void addToNewBin(Item addition) {
		invalidateBounds();
		Bin newBin = new Bin(binSize(), binList.size());
		newBin.addItem(addition);
		setRemainingSpace(remainingSpace + newBin.remainingSpace());
		binList.add(newBin);
	}

	@Override
	public void removeLastBin() {
		invalidateBounds();
		setRemainingSpace(remainingSpace - binList.get(binList.size() - 1).remainingSpace());
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

	public String toString() {
		return "(Level " + level + " Node: " + changes + " solution: " + model.bestSolutionValue() + ")";
	}

	public BinPackingHueristic getHueristic() {
		return hueristic;
	}

	public BinPackingNode deepCopy() {
		return new BinPackingNode(this);
	}

}