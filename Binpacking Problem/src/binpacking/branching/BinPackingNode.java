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
	/** The type of heuristic/algorithm that will be used to solve the binpacking problem*/
	BinPackingModel model;
	/** The master problem instance that this node is a part of*/
	BinPackingInstance problem;

	/** Item that is chosen by the parent instance to create this node */
	Item item;
	
	/** The list of bins */
	List<Bin> binList;
	
	/** the list of all items */
	public List<Item> itemList;
	
	/** List of changes from its parent node */
	List<Change<MutableBinPackingInstance>> changes;
	
	/** The index of the next bin to be checked in the bin list */
	int binIndex;
	/** The next bin that is being checked*/
	Bin nextBin;
	/** True if all the items have been packed, false otherwise */
	boolean done;
	/** True if this node is a leaf, false otherwise*/
	boolean isLeaf;

	/** The upper bound */
	int upperBound;
	/** The lower bound */
	int lowerBound;
	/** The type of heuristic used to calculate the solution */
	BinPackingHueristic hueristic;
	/** The remaining combined weight of all items not yet packed*/
	int remainingItemWeight;
	/** The remaining space of the bins */
	int remainingSpace;
	
	/** The upper bound calculator for this node */
	BoundCalculator<BinPackingNode, Integer> upperBoundCalculator;
	/** The lower bound calculator for this node */
	BoundCalculator<BinPackingNode, Integer> lowerBoundCalculator;
	
	/**
	 * Returns the level of this node
	 * @return the level of this node
	 */
	public int level() {
		return level;
	}
	
	/**
	 * Creates a list of bins
	 * @return the created list of bins
	 */
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
	
	/**
	 * Directly returns this node's bin list
	 * @return this node's bin list
	 */
	public List<Bin> binListDirect() {
		return binList;
	}

	/**
	 * Gets the complete item list
	 * @return the complete item list
	 */
	public List<Item> itemList() {
//		ArrayList<Item> newList = new ArrayList<Item>(itemList.size());
//		for (Item item : itemList) {
//			// Items thus far have only static state associated with them. No need to clone.
//			newList.add(item);
//		}
//		return newList;
		return itemList;
	}
	
	/**
	 * Gets the list of unpacked items
	 * @return the list of unpacked item
	 */
	public List<Item> remainingItemList() {
		List<Item> smallList = new ArrayList<Item>(itemList.size() - level - 1);
		int top = itemList.size() - level;
		for (int i = 0; i < top; i++) {
			smallList.add(itemList.get(i));
		}
		return smallList;
	}
	
	/**
	 * The size of a bin
	 * @return the size of a bin
	 */
	public int binSize() {
		return binSize;
	}
	
	/**
	 * Updates the node with the given changes
	 * @param changes the given changes
	 */
	public void applyChanges(List<Change<MutableBinPackingInstance>> changes) {
		for (Change<MutableBinPackingInstance> change : changes) {
			change.applyChange(this);
		}
	}
	
	/**
	 * Figures out the next item to be added to this instance
	 */
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
	 * Sorts the given list of items in descending order and then assigns them positions in said order 
	 * @param items the given list of items
	 */
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

		if (problem instanceof BinPackingNode) {
			BinPackingNode node = (BinPackingNode) problem;
			if (node.getUpperBoundCalculator() != null) {
				this.setUpperBoundCalculator(((BinPackingNode) problem).getUpperBoundCalculator());
			} else {
				initializeUpperBoundCalculator();
			}
			if (node.getLowerBoundCalculator() != null) {
				this.setLowerBoundCalculator(node.getLowerBoundCalculator());
			} else {
				initializeLowerBoundCalculator();
			}
		} else {
			initializeUpperBoundCalculator();
			initializeLowerBoundCalculator();
		}
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
	
	/**
	 * Initializes the upper bound calculator
	 */
	protected void initializeUpperBoundCalculator() {
		upperBoundCalculator = new SimpleFFUBCalculator();
	}
	
	/**
	 * Initializes the lower bound calculator
	 */
	protected void initializeLowerBoundCalculator() {
		lowerBoundCalculator = new SimpleFFLBCalculator();
	}
	
	/**
	 * Gets the upper bound calculator
	 * @return the upper bound calculator
	 */
	public BoundCalculator<BinPackingNode, Integer> getUpperBoundCalculator() {
		return this.upperBoundCalculator;
	}
	
	/**
	 * Sets the upper bound calculator to the given one
	 * @param ubc the upper bound calculator to set this to
	 */
	public void setUpperBoundCalculator(BoundCalculator<BinPackingNode, Integer> ubc) {
		this.upperBoundCalculator = ubc;
	}
	
	/**
	 * Gets the lower bound calculator
	 * @return the lower bound calculator
	 */
	public BoundCalculator<BinPackingNode, Integer> getLowerBoundCalculator() {
		return this.lowerBoundCalculator;
	}
	
	/**
	 * Sets the lower bound calculator to the given one
	 * @param lbc the lower bound calculator to set this to
	 */
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
	
	/**
	 * Gets the remaining weight of all items that still  have to be packed
	 * @return the remaining weight of all items that still  have to be packed
	 */
	public int getRemainingItemWeight() {
		return remainingItemWeight;
	}
	
	/**
	 * Gets the remaining free space of all bins
	 * @return the remaining free space of all bins
	 */
	public int getRemainingSpace() {
		return remainingSpace;
	}
	
	/**
	 * Invalidates the upper bound and the lower bound by setting them both to -1
	 */
	public void invalidateBounds() {
		upperBoundCalculator.invalidateBound();
		lowerBoundCalculator.invalidateBound();
	}
	
	/**
	 * Sets the remaining weight of all items that still  have to be packed
	 * @param weight the remaining weight of all items that still  have to be packed
	 */
	public void setRemainingItemWeight(int weight) {
//		if (weight < 0) {
//			throw new IllegalArgumentException();
//		}
		this.remainingItemWeight = weight;
	}

	/**
	 * Only removes the item weight.
	 * @param index the index of the item whose weight will be removed
	 */
	@Override
	public Item removeItem(int index) {
		Item removed = itemList.get(index);
		setRemainingItemWeight(remainingItemWeight - removed.getWeight());
		return removed;
	}

	/**
	 * Simply adds the weight back.
	 * @param addition the item whose weight is to be added back
	 */
	@Override
	public void addItem(Item addition) {
//		invalidateBounds();
//		itemList.add(addition);
		setRemainingItemWeight(remainingItemWeight + addition.getWeight());
	}
	
	/**
	 * Sets the remaining space to the given amount
	 * @param space the given amount of remaining space
	 */
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
	
	/**
	 * Adds the next item to the next available bin
	 * @return the bin where the item was added to
	 */
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
	
	/**
	 * Creates a new node with the given parameters
	 * @param problem The master problem instance that this node is a part of
	 * @param model The type of heuristic/algorithm that will be used to solve the binpacking problem
	 * @param lvl The level of the tree that this node is at.
	 * @param changes List of changes from its parent node
	 * @param hueristic The type of heuristic used to calculate the solution
	 * @param remainingItemWeight The remaining combined weight of all items not yet packed
	 * @param remainingSpace  The remaining space of the bins
	 * @return
	 */
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
	
	/**
	 * Returns true if this node is a leaf, false otherwise
	 * @return true if this node is a leaf, false otherwise
	 */
	public boolean isLeaf() {
		return isLeaf;
	}
	
	/**
	 * Gets the level that this node is at
	 * @return the level that this node is at
	 */
	public int getLevel() {
		return level;
	}
	
	/**
	 * Sets the level of this instance to the given level
	 * @param l the given level
	 */
	public void setLevel(int l) {
		level = l;
	}

	@Override
	public List<Bin> getSolution() {
		return binList;
	}
	
	/**
	 * Returns the binpacking model this node is a part of
	 * @return the binpacking model this node is a part of
	 */
	public BinPackingModel getModel() {
		return model;
	}
	
	/**
	 * Returns the level, changes, and the best solution value of this instance
	 */
	public String toString() {
		return "(Level " + level + " Node: " + changes + " solution: " + model.bestSolutionValue() + ")";
	}

	/**
	 * Returns the heuristic used 
	 * @return the heuristic used
	 */
	public BinPackingHueristic getHueristic() {
		return hueristic;
	}
	
	/**
	 * Creates a deep copy of this node
	 * @return a deep copy of this node
	 */
	public BinPackingNode deepCopy() {
		return new BinPackingNode(this);
	}

}