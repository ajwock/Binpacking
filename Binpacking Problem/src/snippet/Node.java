package snippet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Handles the nodes at which the knapsack branches at
 * 
 * @author Gabe Reynolds
 *
 */
public class Node implements Iterable<Node>, Iterator<Node> {
	/** The level of the tree that this node is at. Each level represents an item */
	int level;
	/** The upper bound of the branch this node represents */
	double bound;
	
	BinPackingModel problem;
	
	Item item;
	
	ArrayList<Bin> binList;
	Iterator<Bin> binsListIterator;
	Bin nextBin;
	boolean done;
	boolean isLeaf;

	public ArrayList<Bin> binListClone() {
		ArrayList<Bin> newList = new ArrayList<Bin>(binList.size() + 1);
		for (Bin bin : binList) {
			newList.add(new Bin(bin));
		}
		return newList;
	}
	
	/**
	 * Creates a node with the given level, profit, and weight.
	 * 
	 * @param lvl    the given level
	 * @param money  the given profit
	 * @param weight the given weight
	 */
	Node(BinPackingModel problem, ArrayList<Bin> bins, int lvl) {
		this.problem = problem;
		binList = bins;
		level = lvl;
		bound = 0;
		binsListIterator = binList.iterator();
		item = problem.getNextItem();
		nextBin();
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int l) {
		level = l;
	}

	public double getLowerBound() {
		return bound;
	}

	public void setBound(double b) {
		bound = b;
	}

	@Override
	public Iterator<Node> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		return done;
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
		if (bin != null) {
			return new Node(problem, binListClone(), level + 1);
		} else if (!done) {
			//Create new bin.
			done = true;
			
		}
		throw new NoSuchElementException();
	}
	
	public boolean isLeaf() {
		return isLeaf;
	}

	public int getUpperBound() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}