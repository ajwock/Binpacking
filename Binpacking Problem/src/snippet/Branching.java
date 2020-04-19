package snippet;

import java.util.ArrayList;
import java.util.Arrays;

import edu.ncsu.csc316.dsa.queue.ArrayBasedQueue;
import edu.ncsu.csc316.dsa.queue.Queue;

public class Branching {
	
	int getUpperBound() {
		return 0;
	}

	void branch(Node node) throws OptimalSolutionException {
		for (Node newBranch : node) {
			if (node.isLeaf()) {
				break;
			}

			double lb = node.getLowerBound();
			double ub = node.getUpperBound();

			if (lb < ub) {
				branch(node);
			}
		}
	}

}
