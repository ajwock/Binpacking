package snippet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Handles the nodes at which the knapsack branches at
 * 
 * @author Gabe Reynolds
 *
 */
public class Node extends BinPackingInstance implements Iterable<Node>, Iterator<Node>, BinPackingSolution {
	/** The level of the tree that this node is at. Each level represents an item */
	int level;
	int binSize;
	BinPackingModel model;
	BinPackingInstance problem;
	Item item;
	
	ArrayList<Bin> binList;
	
	ArrayList<Item> itemList;
	
	ListIterator<Bin> binsListIterator;
	Bin nextBin;
	boolean done;
	boolean isLeaf;

	int upperBound;
	int lowerBound;


	public ArrayList<Bin> binList() {
		//Adds an extra slot in case an addition need be made.
		ArrayList<Bin> newList = new ArrayList<Bin>(binList.size() + 1);
		for (Bin bin : binList) {
			//Deep clone because bins can change in ways we don't want to put the time into tracking just yet.
			//Bin has a copy constructor.
			newList.add(new Bin(bin));
		}
		return newList;
	}
	
	public ArrayList<Item> itemList() {
		ArrayList<Item> newList = new ArrayList<Item>(itemList.size());
		for (Item item : itemList) {
			//Items thus far have only static state associated with them.  No need to clone.
			newList.add(item);
		}
		return newList;
	}
	
	@Override
	public int binSize() {
		return binSize;
	}


	/**
	 * Creates a node with the given problem, model, and level, as well as a list of changes to apply to the instance.
	 * 
	 * @param problem The bin packing instance to branch from.
	 * @param model The model containing what is known so far about the current solution.
	 * @param lvl The level on the tree of this node.
	 * @param changes The set of changes that make this node different from the previous.
	 */
	Node(BinPackingInstance problem, BinPackingModel model, int lvl, List<Change> changes) {
		this.problem = problem;
		this.model = model;
		binList = (ArrayList<Bin>) problem.binList();
		binsListIterator = binList.listIterator();
		itemList = (ArrayList<Item>) problem.itemList();
		binSize = problem.binSize();

		for (Change change : changes) {
			change.applyChange(this);
		}
		
		isLeaf = itemList.size() == 0;
		if (!isLeaf) {
			item = itemList.get(0);
		} else {
			done = true;
			item = null;
			model.checkSolution(this);
		}
		level = lvl;
		nextBin();
	}
	
	/**
	 * Creates a node with the given problem, model, and level.
	 * 
	 * @param problem The bin packing instance to branch from.
	 * @param model The model containing what is known so far about the current solution.
	 * @param lvl The level on the tree of this node.
	 */
	Node(BinPackingInstance problem, BinPackingModel model, int lvl) {
		this(problem, model, lvl, new ArrayList<Change>(0));
	}
	
	public void destructor() {
		;
	}
	
	@Override
	public int upperBound() {
		if (upperBound != -1) {
			return upperBound;
		}
		//TODO Calculate upper bound.
		return 0;
	}

	@Override
	public int lowerBound() {
		if (lowerBound != -1) {
			return lowerBound;
		}
		//TODO Calculate lower bound.
		return model.getUpperBound();
	}
	
	public void invalidateBounds() {
		upperBound = -1;
		lowerBound = -1;
	}

	@Override
	public void removeItem(Item removal) {
		invalidateBounds();
		itemList.remove(removal);
	}

	@Override
	public void addToBin(Bin bin, Item addition) {
		invalidateBounds();
		binList.get(bin.getPosition()).addItem(addition);
	}

	@Override
	public void addToNewBin(Item addition) {
		invalidateBounds();
		binList.add(new Bin(binSize()));
	}
	
	@Override
	public Iterator<Node> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		return !done;
	}

	public Bin nextBin() {
		Bin currBin = nextBin;
		while (binsListIterator.hasNext()) {
			nextBin = binsListIterator.next();
			if (item.getWeight() <= nextBin.remainingSpace())
				break;
		}
		if (!binsListIterator.hasNext()) {
			nextBin = null;
		}
		return currBin;
	}
	
	@Override
	public Node next() {
		Bin bin = nextBin();
		ArrayList<Change> changes = new ArrayList<Change>(1);
		if (bin != null) {
			changes.add(new AddToBin(bin, item));
			return new Node(this.problem, this.model, level + 1, changes);
		} else if (!done) {
			done = true;
			changes.add(new NewBin(item));
			return new Node(this.problem, this.model, level + 1, changes);
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

}