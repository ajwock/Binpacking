/**
 * 
 */
package binpacking;

import java.util.ArrayList;
import java.util.List;

import binpacking.interfaces.BinPackingInstance;
import binpacking.interfaces.BinPackingSolution;
import binpacking.interfaces.Change;
import binpacking.interfaces.DynamicBinPackingInstance;
import binpacking.interfaces.MutableBinPackingInstance;

/**
 * @author dzdt
 *
 */
public class SpeedyBoiNode extends BinPackingNode implements DynamicBinPackingInstance {
	
	ArrayList<List<Change<MutableBinPackingInstance>>> changeList;
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
	
	public void newChangeFrame() {
		currentFrame = new ArrayList<Change<MutableBinPackingInstance>>();
		changeList.add(currentFrame);
	}
	
	public void pushChange(Change<MutableBinPackingInstance> change) {
		change.applyChange(this);
		currentFrame.add(change);
	}
	
	public void pushChangeFrame(List<Change<MutableBinPackingInstance>> changes) {
		currentFrame = changes;
		changeList.add(changes);
		for (Change<MutableBinPackingInstance> change : changes) {
			change.applyChange(this);
		}
	}
	
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
		//Upper bound can be an expensive operation- some basic memoization in case we want to see it multiple times.
		if (upperBound == -1) {
			//Apply an appromixation hueristic to the remaining instance.
			BinPackingSolution result = hueristic.apply(this);
			int ub = binList.size();
			/** Pass in the result as a possible solution. */
			model.checkSolution(result);
			model.trySetUpperBound(ub);
			//Many changes are applied to create the approximation.
			//Undo those.
			this.popChangeFrame();
			//Changes invalidate bounds, so set the variable last.
			upperBound = model.getUpperBound();
		}
		return upperBound;
	}

	@Override
	public BinPackingNode newNode(BinPackingInstance problem, BinPackingModel model, int lvl, List<Change<MutableBinPackingInstance>> changes, BinPackingHueristic hueristic, int remainingItemWeight, int remainingSpace) {
		return new SpeedyBoiNode(problem, model, lvl, changes, hueristic, remainingItemWeight, remainingSpace);
	}

	@Override
	public List<Bin> binList() {
		return binList;
	}
	
	/**
	 * Need to return a deep copy of the solution so that it isn't somehow clobbered.
	 */
	@Override
	public List<Bin> getSolution() {
		return super.binList();
	}

	/**
	 * @param problem
	 * @param model
	 * @param lvl
	 * @param changes
	 * @param hueristic
	 * @param remainingItemWeight
	 * @param remainingSpace
	 */
	public SpeedyBoiNode(BinPackingInstance problem, BinPackingModel model, int lvl, List<Change<MutableBinPackingInstance>> changes,
			BinPackingHueristic hueristic, int remainingItemWeight, int remainingSpace) {
		super(problem, model, lvl, changes, hueristic, remainingItemWeight, remainingSpace);
	}

	/**
	 * @param problem
	 * @param model
	 * @param lvl
	 * @param remainingWeight
	 * @param remainingSpace
	 */
	public SpeedyBoiNode(BinPackingInstance problem, BinPackingModel model, int lvl, int remainingWeight,
			int remainingSpace) {
		super(problem, model, lvl, remainingWeight, remainingSpace);
	}

	/**
	 * @param problem
	 * @param model
	 * @param lvl
	 * @param hueristic
	 * @param remainingWeight
	 * @param remainingSpace
	 */
	public SpeedyBoiNode(BinPackingInstance problem, BinPackingModel model, int lvl, BinPackingHueristic hueristic,
			int remainingWeight, int remainingSpace) {
		super(problem, model, lvl, hueristic, remainingWeight, remainingSpace);
	}

	/**
	 * @param problem
	 * @param model
	 * @param lvl
	 */
	public SpeedyBoiNode(BinPackingInstance problem, BinPackingModel model, int lvl) {
		super(problem, model, lvl);
	}

	/**
	 * @param problem
	 */
	public SpeedyBoiNode(BinPackingInstance problem) {
		super(problem);
	}

}
