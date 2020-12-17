package org.project.its;

import org.avmframework.AbstractVector;
import org.avmframework.EmptyVectorException;
import org.avmframework.Monitor;
import org.avmframework.TerminationException;
import org.avmframework.TerminationPolicy;
import org.avmframework.Vector;
import org.avmframework.initialization.Initializer;
import org.avmframework.objective.ObjectiveFunction;

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
	
	protected void iteratedTabuSearch(AbstractVector abstractVector) throws TerminationException {

		search.search(vector, objFun);
		
	}

}
