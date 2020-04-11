package snippet;


import java.util.ArrayList;
import java.util.Arrays;

import edu.ncsu.csc316.dsa.queue.ArrayBasedQueue;
import edu.ncsu.csc316.dsa.queue.Queue;

public class Branching {
	
	ArrayList<Item> items = new ArrayList<Item>();
	public int plain() {
	
	
		Queue<Node> M = new ArrayBasedQueue<Node>();
		Node u;
		Node v;
	
		
	
		//dummy node
		u = new Node(-1, 0);
		M.enqueue(u);
		v = new Node(0, 0)
	
		int lowerBound = 0;
		while(!M.isEmpty()) {
			u = M.dequeue();
			if(u.getLevel() == numItems - 1) {
				continue;
			}
	
			
	
			v.setLevel(u.getLevel() + 1);
	
				
	
				v.setWeight(u.getWeight() + items[v.getLevel()].getWeight());
	
				v.setProfit(u.getProfit() + items[v.getLevel()].getValue());
	
				
	
				if(v.getWeight() <= capacity && v.getProfit() > lowerBound) {
	
					lowerBound = v.getProfit();
	
				}
	
				v.setBound(bound(v));
	
				
	
				if(v.getBound() > lowerBound) {
	
					Node temp = new Node(v.getLevel(), v.getProfit(), v.getWeight());
	
					M.enqueue(temp);
	
				}
	
				
	
				v.setWeight(u.getWeight());
	
				v.setProfit(u.getProfit());
	
				v.setBound(bound(v));
				
				if(v.getBound() > lowerBound) {
	
					Node temp = new Node(v.getLevel(), v.getProfit(), v.getWeight());
	
					M.enqueue(temp);
	
				}
	
		}
	
		return lowerBound;
	
	}
}

