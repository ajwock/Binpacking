package binpacking.branching;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import binpacking.controller.BinpackSolver;
import binpacking.interfaces.BinPackingSolution;
import binpacking.model.Bin;
import binpacking.model.BinPackingModel;
import binpacking.model.Item;
import bnb.interfaces.BoundCalculator;

class BinPackingTest {

	@Test
	void test() {

		try {
			BinPackingModel esm = new BinPackingModel();
			List<Bin> esolution = new ArrayList<Bin>();
			Bin nb = new Bin(10, 0);
			Item i3 = new Item(0, 3);
			Item i2 = new Item(0, 2);
			nb.addItem(i3);
			nb.addItem(i3);
			nb.addItem(i2);
			nb.addItem(i2);
			esolution.add(new Bin(nb));
			nb.setPosition(1);
			esolution.add(new Bin(nb));
			nb.setPosition(2);
			esolution.add(new Bin(nb));
			nb.setPosition(3);
			esm.checkSolution(new BinPackingSolution() {

				@Override
				public List<Bin> getSolution() {
					return esolution;
				}

			});

			BinPackingModel checkModel = new BinPackingModel();
			String[] exceptargs = { "testfiles/bptestexcept.bp", "-basicbnb", "-q" };
			BinPackingSolution exceptSolution = BinpackSolver.startBPProgram(exceptargs);
			List<Bin> es = exceptSolution.getSolution();
			assertEquals("Bad solution: " + exceptSolution, 3, es.size());
			checkModel.checkSolution(new BinPackingSolution() {

				@Override
				public List<Bin> getSolution() {
					return es;
				}

			});
			assertEquals("Expected: " + esm.getSolution() + "\nBut got " + checkModel.getSolution(), esm, checkModel);

			String[] exceptargs2 = { "testfiles/bptestexcept.bp", "-fastbnb", "-q" };
			BinPackingSolution exceptSolution2 = BinpackSolver.startBPProgram(exceptargs2);
			List<Bin> es2 = exceptSolution2.getSolution();
			assertEquals("Bad solution: " + exceptSolution2, 3, es2.size());
			checkModel.checkSolution(new BinPackingSolution() {

				@Override
				public List<Bin> getSolution() {
					return es2;
				}

			});
			assertEquals("Expected: " + esm.getSolution() + "\nBut got " + checkModel.getSolution(), esm, checkModel);

			/**
			 * Exhaustion test. Makes sure that with many random instances the program still
			 * works.
			 */
			ProcessBuilder pb = new ProcessBuilder();
			final String basename = "testfiles/temp/test8_no";
			for (int i = 0; i < 128; i++) {
				String testFileName = basename + i + ".bp";
				/**
				 * Delegate instance generation to an existing python script to save programmer
				 * time.
				 */
				pb.command("python", "testfiles/generateInstance.py", testFileName, "8");
				Process process = pb.start();
				process.waitFor();
				String[] args = { testFileName, "-q" };
				BinpackSolver.resolveArgs(args);
				BinPackingNode node1 = new BinPackingNode(BinpackSolver.original);
				BinPackingModel model1 = BinpackSolver.original.runOnNode(node1);
				int branches1 = BinpackSolver.original.getBranches();
				BinpackSolver.outputInfo(testFileName);

				BinpackSolver.resolveArgs(args);
				BinPackingNode node2 = new BinPackingNode(BinpackSolver.original);
				// Forces all branches to be taken.
				node2.setLowerBoundCalculator(new BoundCalculator<BinPackingNode, Integer>() {

					@Override
					public Integer bound(BinPackingNode instance) {
						return 0;
					}

				});
				BinPackingModel model2 = BinpackSolver.original.runOnNode(node2);
				int branches2 = BinpackSolver.original.getBranches();
				BinpackSolver.outputInfo(testFileName);
				assertEquals(model2, model1);

				BinpackSolver.resolveArgs(args);
				BinPackingNode node3 = new SpeedyBoiNode(BinpackSolver.original);
				BinPackingModel model3 = BinpackSolver.original.runOnNode(node3);
				int branches3 = BinpackSolver.original.getBranches();
				BinpackSolver.outputInfo(testFileName);
				assertEquals(model2, model3);

				BinpackSolver.resolveArgs(args);
				BinPackingNode node4 = new BinPackingNode(BinpackSolver.original);
				// Forces all branches to be taken.
				node4.setLowerBoundCalculator(new BoundCalculator<BinPackingNode, Integer>() {

					@Override
					public Integer bound(BinPackingNode instance) {
						return 0;
					}

				});
				BinPackingModel model4 = BinpackSolver.original.runOnNode(node4);
				int branches4 = BinpackSolver.original.getBranches();
				BinpackSolver.outputInfo(testFileName);
				assertEquals(model3, model4);

				if (branches1 == branches2) {
					checkExceptionalInstance(node1);
				}
				if (branches3 == branches4) {
					checkExceptionalInstance(node1);
				}
				// Each type of node should produce the same branching behavior.
				assertEquals(branches1, branches3);
				assertEquals(branches2, branches4);
			}
		} catch (IOException | InterruptedException e) {
			System.out.println(e.getStackTrace());
		}
	}

	/**
	 * The number of branches between a node with proper branch and bound behavior
	 * and no bounding is nearly guaranteed to be different- though there is one
	 * case where this is not true- when all or all but one of the items in the
	 * problem are of size > binSize / 2.
	 * 
	 * If the number of branches on the full branching instances and bounded
	 * branching instances are different, then fail.
	 */
	public void checkExceptionalInstance(BinPackingNode node) {
		System.out.println("Interesting Solution: " + node.getModel().getSolution());
		int halfcap = node.binSize() / 2;
		boolean alreadySeenSmallItem = false;
		List<Item> items = node.itemList();
		for (Item item : items) {
			if (item.getWeight() <= halfcap) {
				if (alreadySeenSmallItem) {
					fail();
				} else {
					alreadySeenSmallItem = true;
				}
			}
		}
	}

}
