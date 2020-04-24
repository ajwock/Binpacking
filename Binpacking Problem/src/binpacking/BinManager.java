package binpacking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import binpacking.interfaces.BinPackingInstance;

/**
 * Handles the nodes at which the knapsack branches at
 * 
 * @author Gabe Reynolds
 *
 */
public class BinManager implements BinPackingInstance {

	/** The capacity of a bin */
	private int capacity;
	/** The number of times a branch occurs */
	private int branches = 0;
	/** An array of all items that can be put in to the knapsack */
	ArrayList<Item> items;
	/** The total number of items to be put in to bins */
	private int numItems;
	/** Handles the bins */
	ArrayList<Bin> boxOfBins = new ArrayList<Bin>();
	ArrayList<BinPackingNode> boxOfNodes = new ArrayList<BinPackingNode>();
	private int numBins;

	/**
	 * Creates a new BinManager object, with the given capacity and the number of
	 * items that can be put into each bin that's created.
	 */
	BinManager(int cap, int items) {
		capacity = cap;
		numItems = items;
		this.items = new ArrayList<Item>(numItems);
		assignPositions();
	}
	
	public void assignPositions() {
		Collections.sort(this.items);
		for (int i = 0; i < this.items.size(); i++) {
			this.items.get(i).setPosition(i);
		}
	}
	
	public int getCapacity() {
		return this.capacity;
	}

	public int getNumBins() {
		return numBins;
	}

	public int getNumItems() {
		return this.numItems;
	}

	public void addItem(Item g) {
		items.add(g);
	}

	public int getBranches() {
		return branches;
	}

	public void addBin() {
		Bin k = new Bin(capacity, boxOfBins.size());
		boxOfBins.add(k);
	}

	public long unrefined() {
		assignPositions();
		Branching bnb = new Branching();
		BinPackingNode root = new SpeedyBoiNode(this);
		BinPackingModel model = root.getModel();
		try {
			bnb.branch(root);
		} catch (OptimalSolutionException e) {
			model.checkSolution(e.getSolution());
		}
		branches = bnb.branches;
		boxOfBins = new ArrayList<Bin>(model.getSolution());
		numBins = boxOfBins.size();
		return numBins;
	}
	
	public long ffapprox() {
		assignPositions();
		BinPackingNode root = new SpeedyBoiNode(this);
		BinPackingModel model = root.getModel();
		model.checkSolution(new FirstFitHueristic().apply(root));
		boxOfBins = new ArrayList<Bin>(model.getSolution());
		numBins = boxOfBins.size();
		return numBins;
	}

	@Override
	public List<Bin> binList() {
		return boxOfBins;
	}

	@Override
	public List<Item> itemList() {
		return items;
	}

	@Override
	public int binSize() {
		return capacity;
	}

}