package org.avmframework.localsearch;

import java.util.HashMap;
import java.util.Map;

import javax.naming.directory.DirContext;

import org.avmframework.TerminationException;
import org.avmframework.objective.ObjectiveValue;

public class HillClimbingSearch extends LocalSearch{
 
	protected ObjectiveValue initial;
	protected ObjectiveValue current;
	protected int num;
		
	protected void initialize() throws TerminationException {
		initial = objFun.evaluate(vector);
		current = initial;
		num = var.getValue();
	}
	
	@Override
	protected void performSearch() throws TerminationException {
		initialize();
		
		boolean foundOptimum = false;
		while (!foundOptimum) {
			
			// Get the neighbours of the current solution
			Map<ObjectiveValue, Integer> neighbours = getNeighbours();
			
			ObjectiveValue next = null;
			double nextDir = 0;
			
			// Iterate through the neighbours to find the best solution
			for(Map.Entry<ObjectiveValue, Integer> neighbour : neighbours.entrySet()) {
				if (next == null) {
					next = neighbour.getKey();
					nextDir = neighbour.getValue();
				} else if(neighbour.getKey().betterThan(next)) {
					next = neighbour.getKey();
					nextDir = neighbour.getValue();
				}
			}
			
			
			if (next.betterThan(current)) {
				// We found a better solution, we'll move to that one.
				current = next;
				num += nextDir;
			} else {
				// We are stuck in a local optimum so we end here.
				foundOptimum = true;
			}
			var.setValue(num);
		}
	}
	
	
	private Map<ObjectiveValue, Integer> getNeighbours() throws TerminationException{
		var.setValue(num - 1);
	    ObjectiveValue left = objFun.evaluate(vector);
	    
	    var.setValue(num + 1);
	    ObjectiveValue right = objFun.evaluate(vector);
	    
	    HashMap<ObjectiveValue, Integer> neighbours= new HashMap<>();
	    neighbours.put(left, -1);
	    neighbours.put(right, 1);
	    
	    return neighbours;
	}
	
}
