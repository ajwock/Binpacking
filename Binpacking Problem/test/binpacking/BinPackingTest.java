package binpacking;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import binpacking.branching.BinPackingNode;
import binpacking.controller.BinpackSolver;
import binpacking.interfaces.BinPackingHueristic;
import binpacking.interfaces.BinPackingInstance;
import binpacking.interfaces.MutableBinPackingInstance;
import binpacking.model.BinManager;
import binpacking.model.BinPackingModel;
import general.interfaces.Change;

class BinPackingTest {

	/**
	 * This node is meant to force the upper bound to be higher, causing longer
	 * branching periods during testing.
	 * 
	 * @author Andrew Wock
	 *
	 */
	private class UBNode extends BinPackingNode {
		
		int boundOffset;

		UBNode(BinPackingInstance problem, BinPackingModel model, int lvl,
				List<Change<MutableBinPackingInstance>> changes, BinPackingHueristic hueristic, int remainingItemWeight,
				int remainingSpace, int boundOffset) {
			super(problem, model, lvl, changes, hueristic, remainingItemWeight, remainingSpace);
		}

		public UBNode(BinManager original, int boundOffset) {
			super(original);
			this.boundOffset = boundOffset;
		}

		public BinPackingNode newNode(BinPackingInstance problem, BinPackingModel model, int lvl,
				List<Change<MutableBinPackingInstance>> changes, BinPackingHueristic hueristic, int remainingItemWeight,
				int remainingSpace) {
			return new UBNode(problem, model, lvl, changes, hueristic, remainingItemWeight, remainingSpace, boundOffset);
		}

		public int upperBound() {
			return super.upperBound() + boundOffset;
		}
	}

	@Test
	void test() {
		ProcessBuilder pb = new ProcessBuilder();
		final String basename = "testfiles/temp/test16_no";
		try {
			for (int i = 0; i < 16; i++) {
				String testFileName = basename + i + ".bp";
				pb.command("python", "testfiles/generateInstance.py", testFileName, "16");
				Process process = pb.start();
				process.waitFor();
				String[] args = { testFileName };
				BinpackSolver.resolveArgs(args);
				BinPackingNode node1 = new BinPackingNode(BinpackSolver.original);
				BinPackingModel model1 = BinpackSolver.original.runOnNode(node1);
				BinpackSolver.outputInfo(testFileName);

				BinpackSolver.resolveArgs(args);

				/**
				 * Branches a whole lot more. We want to see that with more branches the
				 * solution remains the same.
				 */
				BinPackingNode node2 = new UBNode(BinpackSolver.original, 1);
				BinPackingModel model2 = BinpackSolver.original.runOnNode(node2);
				BinpackSolver.outputInfo(testFileName);
				assertEquals(model2, model1);
			}
		} catch (IOException | InterruptedException e) {
			System.out.println(e.getStackTrace());
		}
	}

}
