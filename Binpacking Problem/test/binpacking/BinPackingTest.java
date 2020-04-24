package binpacking;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class BinPackingTest {

	@Test
	void test() {
		ProcessBuilder pb = new ProcessBuilder();
		final String basename = "testfiles/temp/test16_no";
		try {
			for (int i = 0; i < 16; i++) {
				String testFileName = basename + i + ".bp";
				pb.command("python", "testfiles/generateInstance.py", testFileName, "16");
				Process process = pb.start();
				int exitVal = process.waitFor();
				String[] args = { testFileName };
				BinpackSolver.resolveArgs(args);
				BinPackingNode node1 = new BinPackingNode(BinpackSolver.original);
				BinPackingModel model1= BinpackSolver.original.runOnNode(node1);
				BinpackSolver.outputInfo(testFileName);
				
				BinpackSolver.resolveArgs(args);
				/**
				 * Branches a whole lot more.  We want to see that with more branches the
				 * solution remains the same.
				 */
				BinPackingNode node2 = new BinPackingNode(BinpackSolver.original) {
					public int upperBound() {
						return super.upperBound() + 10;
					}
				};
				BinPackingModel model2= BinpackSolver.original.runOnNode(node2);
				BinpackSolver.outputInfo(testFileName);
				assertEquals(model2, model1);
			}
		} catch (IOException | InterruptedException e) {
			System.out.println(e.getStackTrace());
		}
	}

}
