package binpacking.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import binpacking.branching.BinPackingNode;
import binpacking.branching.SpeedyBoiNode;
import binpacking.interfaces.BinPackingHueristic;
import binpacking.interfaces.BinPackingInstance;
import binpacking.interfaces.BinPackingSolution;
import bnb.Branching;
import bnb.OptimalSolutionException;

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
	boolean traceTree;

	/**
	 * Creates a new BinManager object, with the given capacity and the number of
	 * items that can be put into each bin that's created.
	 */
	public BinManager(int cap, int items) {
		capacity = cap;
		numItems = items;
		this.items = new ArrayList<Item>(numItems);
		assignItemPositions(this.items);
		traceTree = false;
	}

	public void assignItemPositions(List<Item> items) {
		Collections.sort(items);
		for (int i = 0; i < items.size(); i++) {
			items.get(i).setPosition(i);
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

	public void setTraceTree(boolean tt) {
		traceTree = tt;
	}

	public void addBin() {
		Bin k = new Bin(capacity, boxOfBins.size());
		boxOfBins.add(k);
	}

	public BinPackingModel unrefined() {
		BinPackingNode root = new BinPackingNode(this);
		return runOnNode(root);
	}

	public BinPackingModel fast() {
		BinPackingNode root = new SpeedyBoiNode(this);
		return runOnNode(root);
	}

	public BinPackingModel runOnNode(BinPackingNode root) {
		assignItemPositions(root.itemList());
		// This algorithm can have upper bounds calculated by the parents- nearly a
		// twofold speedup.
		Branching bnb = new Branching(traceTree, true, false);
		BinPackingModel model = root.getModel();
		try {
			bnb.branch(root);
		} catch (OptimalSolutionException e) {
			model.checkSolution(e.getSolution());
		}
		branches = bnb.getBranches();
		boxOfBins = new ArrayList<Bin>(model.getSolution());
		numBins = boxOfBins.size();
		return model;
	}

	public BinPackingSolution ffapprox() {
		BinPackingNode root = new BinPackingNode(this);
		BinPackingHueristic hueristic = new FirstFitHueristic();
		return runApproximationOnNode(root, hueristic);
	}

	public BinPackingModel runApproximationOnNode(BinPackingNode node, BinPackingHueristic hueristic) {
		assignItemPositions(node.itemList);
		BinPackingModel model = node.getModel();
		model.checkSolution(hueristic.apply(node));
		boxOfBins = new ArrayList<Bin>(model.getSolution());
		numBins = boxOfBins.size();
		return model;
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