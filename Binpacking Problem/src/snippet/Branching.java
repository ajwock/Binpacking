package snippet;

import java.util.ArrayList;
import java.util.Arrays;

import edu.ncsu.csc316.dsa.queue.ArrayBasedQueue;
import edu.ncsu.csc316.dsa.queue.Queue;

public class Branching {
	
	int getUpperBound() {
		return 0;
	}

	void branch(Node node) {
		for (Node newBranch : node) {
			if (node.getLowerBound() < getUpperBound()) {
				branch(node);
			}
		}
	}

}
