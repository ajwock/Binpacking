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
 * @author Drew Wock and Gabe Reynolds
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
	/** A list of BinPackingNodes for this bin packing problem */
	ArrayList<BinPackingNode> boxOfNodes = new ArrayList<BinPackingNode>();
	/** The number of bins in the current best solution of this problem*/
	private int numBins;
	/** True if the contents of each bin should be printed out at the end, false otherwise */
	boolean traceTree;

	/**
	 * Creates a new BinManager object, with the given capacity and the number of
	 * items that can be put into each bin that's created.
	 * 
	 * @param cap the capacity of each bin
	 * @param items the number of items to be packed
	 */
	public BinManager(int cap, int items) {
		capacity = cap;
		numItems = items;
		this.items = new ArrayList<Item>(numItems);
		assignItemPositions(this.items);
		traceTree = false;
	}
	
	/**
	 * Sorts the given item list by descending weight,
	 * and then assigns them a position based on their index in the sorted list
	 * @param items the given list of items
	 */
	public void assignItemPositions(List<Item> items) {
		Collections.sort(items);
		for (int i = 0; i < items.size(); i++) {
			items.get(i).setPosition(i);
		}
	}
	
	/**
	 * Gets the capacity of a bin
	 * @return the capacity of a bin
	 */
	public int getCapacity() {
		return this.capacity;
	}
	
	/**
	 * Gets the number of bins in the optimal solution of this problem
	 * @return the number of bins in the optimal solution of this problem
	 */
	public int getNumBins() {
		return numBins;
	}
	
	/**
	 * Gets the number of items to be packed
	 * @return the number of items to be packed
	 */
	public int getNumItems() {
		return this.numItems;
	}
	
	/**
	 * Adds the given item to the item list
	 * @param g the given item
	 */
	public void addItem(Item g) {
		items.add(g);
	}
	
	/**
	 * Gets the number of branches in this bin packing problem
	 * @return the number of branches in this bin packing problem
	 */
	public int getBranches() {
		return branches;
	}
	
	/**
	 * Sets the traceTree to the given boolean value
	 * @param tt the given boolean value
	 */
	public void setTraceTree(boolean tt) {
		traceTree = tt;
	}
	
	/**
	 * Creates a new bin for this instance
	 */
	public void addBin() {
		Bin k = new Bin(capacity, boxOfBins.size());
		boxOfBins.add(k);
	}
	
	/**
	 * Solves the problem instance using the basic, unoptimized node
	 * @return the optimal solution found for this bin packing problem
	 */
	public BinPackingModel unrefined() {
		BinPackingNode root = new BinPackingNode(this);
		return runOnNode(root);
	}
	
	/**
	 * Solves the problem instance using the optimized node
	 * @return the optimal solution found for this bin packing problem
	 */
	public BinPackingModel fast() {
		BinPackingNode root = new SpeedyBoiNode(this);
		return runOnNode(root);
	}
	
	/**
	 * Finds the optimal solution for this bin packing problem with the given node as the root node
	 * @param root the given root node
	 * @return the optimal solution found for this bin packing problem
	 */
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
	
	/**
	 * Uses first fit approximation to solve the bin packing problem
	 * @return the optimal solution found for this bin packing problem by first fit approximation
	 */
	public BinPackingSolution ffapprox() {
		BinPackingNode root = new BinPackingNode(this);
		BinPackingHueristic hueristic = new FirstFitHueristic();
		return runApproximationOnNode(root, hueristic);
	}
	
	/**
	 * Runs the given heuristic on the given node to approximate the optimal solution
	 * @param node the given node
	 * @param hueristic the given heuristic
	 * @return the optimal solution found by the given heuristic on the given node
	 */
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