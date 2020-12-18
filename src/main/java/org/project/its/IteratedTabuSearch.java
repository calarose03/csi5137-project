package org.project.its;

import org.avmframework.EmptyVectorException;
import org.avmframework.Monitor;
import org.avmframework.TerminationException;
import org.avmframework.TerminationPolicy;
import org.avmframework.Vector;
import org.avmframework.initialization.Initializer;
import org.avmframework.objective.ObjectiveFunction;
import org.avmframework.objective.ObjectiveValue;
import org.avmframework.variable.AtomicVariable;

import java.util.Random;

public class IteratedTabuSearch {
	
	protected TabuSearch search;
	
	protected TerminationPolicy terminationPolicy;
	
	protected Initializer initializer;
	
	protected Initializer restarter;

	protected Monitor monitor;

	protected ObjectiveFunction objFun;

	protected Vector vector;
	
	public IteratedTabuSearch(TerminationPolicy tp, Initializer initializer) {
		this.search = new TabuSearch();
	    this.terminationPolicy = tp;
	    this.initializer = initializer;
	    this.restarter = initializer;
	}
	
	public Monitor search(Vector vector, ObjectiveFunction objFun) {
		// set up the monitor
	    this.monitor = new Monitor(terminationPolicy);

	    // set up the objective function
	    this.objFun = objFun;
	    objFun.setMonitor(monitor);

	    // initialize the vector
	    this.vector = vector;
	    initializer.initialize(vector);

	    // is there anything to optimize?
	    if (vector.size() == 0) {
	      throw new EmptyVectorException();
	    }

	    try {
		  System.out.println("Starting");
		  iteratedTabuSearch(vector);

	    } catch (TerminationException exception) {
	      // the search has ended
	      monitor.observeTermination();
	    }

	    return monitor;
	}
	
	protected void iteratedTabuSearch(Vector vector) throws TerminationException {

		Vector currentSolution = search.search(vector, objFun);

		Vector bestSolution = currentSolution.deepCopy();
		ObjectiveValue bestValue = objFun.evaluate(bestSolution);

		for(int i = 1; i < vector.size(); i++) {
			vector = perturbate(currentSolution);

			currentSolution = search.search(vector, objFun);
			ObjectiveValue currentValue = objFun.evaluate(currentSolution);

			if (currentValue.betterThan(bestValue)) {
				bestValue = currentValue;
			}
		}

		// Our search has ended, we force a termination.
		throw new TerminationException();
	}

	private Vector perturbate(Vector vector){
		int level = new Random().nextInt(vector.size()-1) + 1;

		Vector perturbation = vector.deepCopy();

		for(int i = level; i <  vector.size(); i++){
			AtomicVariable var = (AtomicVariable) perturbation.getVariable(i);
			int newValue = new Random().nextInt(var.getMax());
			var.setValue(newValue);
		}
		return perturbation;
	}

}
