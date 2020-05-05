/**
 * 
 */
package binpacking.branching;

import java.util.ArrayList;
import java.util.List;

import binpacking.interfaces.BinPackingHueristic;
import binpacking.interfaces.BinPackingInstance;
import binpacking.interfaces.BinPackingSolution;
import binpacking.interfaces.DynamicBinPackingInstance;
import binpacking.interfaces.MutableBinPackingInstance;
import binpacking.model.Bin;
import binpacking.model.BinPackingModel;
import general.interfaces.Change;

/**
 * A type of BinPackingNode, except faster.
 * 
 * Uses the Change interface to apply and reverse changes so that the same memory can be used for many
 * different configurations of Items in Bins.
 * 
 * @author Drew Wock
 *
 */
public class SpeedyBoiNode extends BinPackingNode implements DynamicBinPackingInstance {

	/**
	 * List of change "frames".
	 * 
	 * A change frame is a list of changes that can be reversed as a unit.
	 */
	ArrayList<List<Change<MutableBinPackingInstance>>> changeList;
	
	/**
	 * The top frame of the change stack.
	 */
	List<Change<MutableBinPackingInstance>> currentFrame;

	@Override
	public void applyChanges(List<Change<MutableBinPackingInstance>> changes) {
		if (changeList == null) {
			changeList = new ArrayList<List<Change<MutableBinPackingInstance>>>();
		}
		pushChangeFrame(changes);
	}

	@Override
	public void destructor() {
		if (changeList != null) {
			while (!changeList.isEmpty()) {
				popChangeFrame();
			}
		}
	}

	/**
	 * Creates a new, empty change frame.
	 */
	public void newChangeFrame() {
		currentFrame = new ArrayList<Change<MutableBinPackingInstance>>();
		changeList.add(currentFrame);
	}

	/**
	 * Pushes a single change to the top change frame.
	 */
	public void pushChange(Change<MutableBinPackingInstance> change) {
		change.applyChange(this);
		currentFrame.add(change);
	}

	/**
	 * Pushes a whole list of changes as a new change frame.
	 */
	public void pushChangeFrame(List<Change<MutableBinPackingInstance>> changes) {
		currentFrame = changes;
		changeList.add(changes);
		for (Change<MutableBinPackingInstance> change : changes) {
			change.applyChange(this);
		}
	}

	/**
	 * Reverse all changes in a frame.
	 */
	public void popChangeFrame() {
		List<Change<MutableBinPackingInstance>> poppedChanges = changeList.remove(changeList.size() - 1);
		while (!poppedChanges.isEmpty()) {
			poppedChanges.remove(poppedChanges.size() - 1).reverseChange(this);
		}
		if (changeList.isEmpty()) {
			currentFrame = null;
		} else {
			currentFrame = changeList.get(changeList.size() - 1);
		}
	}

	@Override
	public int upperBound() {
		// Upper bound can be an expensive operation- some basic memoization in case we
		// want to see it multiple times.
		if (upperBound == -1) {
			// Apply an appromixation hueristic to the remaining instance.
			BinPackingSolution result = hueristic.apply(this);
			// Pass in the result as a possible solution.
			model.checkSolution(result);
			// Many changes are applied to create the approximation.
			// Undo those.
			this.popChangeFrame();
		}
		// Changes invalidate bounds, so set the variable last.
		upperBound = model.bestSolutionValue();
		return upperBound;
	}

	@Override
	public BinPackingNode newNode(BinPackingInstance problem, BinPackingModel model, int lvl,
			List<Change<MutableBinPackingInstance>> changes, BinPackingHueristic hueristic, int remainingItemWeight,
			int remainingSpace) {
		return new SpeedyBoiNode(problem, model, lvl, changes, hueristic, remainingItemWeight, remainingSpace);
	}

	@Override
	public List<Bin> binList() {
		return binList;
	}

	/**
	 * Need to return a deep copy of the solution so that it isn't clobbered by the reversal of changes.
	 */
	@Override
	public List<Bin> getSolution() {
		return super.binList();
	}

	/**
	 * Construct a new SpeedyBoiNode.
	 * 
	 * @param problem The parent instance of the problem.
	 * @param model The model containing a reference solution.
	 * @param lvl The level at which this node occurs.
	 * @param changes The changes between this node and the parent instance.
	 * @param hueristic The hueristic to use to calculate upper bounds.
	 * @param remainingItemWeight The weight of all un-placed items.
	 * @param remainingSpace The amount of unfilled space in existing bins.
	 */
	public SpeedyBoiNode(BinPackingInstance problem, BinPackingModel model, int lvl,
			List<Change<MutableBinPackingInstance>> changes, BinPackingHueristic hueristic, int remainingItemWeight,
			int remainingSpace) {
		super(problem, model, lvl, changes, hueristic, remainingItemWeight, remainingSpace);
	}

	/**
	 * Constructor.
	 * 
	 * @param problem The parent instance of the problem.
	 * @param model The model containing a reference solution.
	 * @param lvl The level at which this node occurs.
	 * @param remainingItemWeight The weight of all un-placed items.
	 * @param remainingSpace The amount of unfilled space in existing bins.
	 */
	public SpeedyBoiNode(BinPackingInstance problem, BinPackingModel model, int lvl, int remainingWeight,
			int remainingSpace) {
		super(problem, model, lvl, remainingWeight, remainingSpace);
	}

	/**
	 * Constructor.
	 * 
	 * @param problem The parent instance of the problem.
	 * @param model The model containing a reference solution.
	 * @param lvl The level at which this node occurs.
	 * @param hueristic The hueristic to use to calculate upper bounds.
	 * @param remainingItemWeight The weight of all un-placed items.
	 * @param remainingSpace The amount of unfilled space in existing bins.
	 */
	public SpeedyBoiNode(BinPackingInstance problem, BinPackingModel model, int lvl, BinPackingHueristic hueristic,
			int remainingWeight, int remainingSpace) {
		super(problem, model, lvl, hueristic, remainingWeight, remainingSpace);
	}

	/**
	 * Constructor.
	 * 
	 * @param problem The parent instance of the problem.
	 * @param model The model containing a reference solution.
	 * @param lvl The level at which this node occurs.
	 */
	public SpeedyBoiNode(BinPackingInstance problem, BinPackingModel model, int lvl) {
		super(problem, model, lvl);
	}

	/**
	 * Root constructor.
	 * 
	 * @param problem The parent instance of the problem.
	 */
	public SpeedyBoiNode(BinPackingInstance problem) {
		super(problem);
		// Need to initialize parental upper bound first to avoid item dupe
		// bugs.
		this.upperBound();
	}

}
