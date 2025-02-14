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

    public Monitor search(Vector vector, ObjectiveFunction objFun, boolean isITS) {
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
            if (isITS) {
                iteratedTabuSearch(vector);
            } else {
                search.search(vector, objFun);
            }
            throw new TerminationException();
        } catch (TerminationException exception) {
            // the search has ended
            monitor.observeTermination();
        }

        return monitor;
    }

    /** Iterated Tabu Search implementation (goes with Algorithm 1 in the report)**/
    protected void iteratedTabuSearch(Vector vector) throws TerminationException {

        Vector currentSolution = search.search(vector, objFun);

        Vector bestSolution = currentSolution.deepCopy();
        ObjectiveValue bestValue = objFun.evaluate(bestSolution);

        for (int i = 0; i < vector.size(); i++) {
            // Perturb the current solution
            vector = perturb(currentSolution);

            // Do the Tabu Search
            currentSolution = search.search(vector, objFun);
            ObjectiveValue currentValue = objFun.evaluate(currentSolution);

            // Update best solution if needed
            if (currentValue.betterThan(bestValue)) {
                bestValue = currentValue;
            }
        }

        // Our search has ended, we force a termination.
        throw new TerminationException();
    }

    /** Method to perturb the solution (goes with Algorithm 4 in the report) **/
    private Vector perturb(Vector vector) {
        int level = new Random().nextInt(vector.size() - 1) + 1;

        Vector perturbation = vector.deepCopy();

        // For each variable in the subset, randomly assign a new value
        for (int i = level; i < vector.size(); i++) {
            AtomicVariable var = (AtomicVariable) perturbation.getVariable(i);
            int newValue = new Random().nextInt(var.getMax());
            var.setValue(newValue);
        }
        return perturbation;
    }

}
