package org.project.its;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.avmframework.TerminationException;
import org.avmframework.Vector;
import org.avmframework.objective.ObjectiveFunction;
import org.avmframework.objective.ObjectiveValue;
import org.avmframework.variable.AtomicVariable;

public class TabuSearch {

    /** The Tabu Search Parameters. They can be changed to experiment. Defaults are
     * ITERATIONS = 50
     * TABU_LIST_SIZE = 10
     * **/
    private static final int ITERATIONS = 50;
    private static final int TABU_LIST_SIZE = 10;

    protected ObjectiveValue currentValue;
    protected ObjectiveValue bestValue;

    protected Vector current;
    protected Vector best;

    protected List<Vector> tabuList;
    protected ObjectiveFunction objFun;

    public Vector search(Vector vector, ObjectiveFunction objectiveFunction) throws TerminationException {
        this.current = vector.deepCopy();
        this.best = vector.deepCopy();
        this.objFun = objectiveFunction;
        this.currentValue = objFun.evaluate(current);
        this.bestValue = objFun.evaluate(best);

        this.tabuList = new ArrayList<>();
        return performSearch();
    }

    /** Tabu Search implementation (goes with Algorithm 3: Tabu Search in the report) **/
    protected Vector performSearch() throws TerminationException {
        int counter = 1;
        boolean improved = false;

        while (counter <= ITERATIONS || improved) {
            List<Vector> neighboursList = getNeighbours(current.size(), current);

            Vector bestNeighbour = neighboursList.get(0).deepCopy();
            ObjectiveValue bestNeighbourValue = objFun.evaluate(bestNeighbour);
            for (Vector neighbour : neighboursList) {
                boolean isTabu = tabuList.contains(neighbour);

                ObjectiveValue neighbourValue = objFun.evaluate(neighbour);

                if (neighbourValue.betterThan(bestNeighbourValue) && !isTabu) {
                    bestNeighbour = neighbour.deepCopy();
                    bestNeighbourValue = neighbourValue;
                }
            }

            // If the best neighbour is a better solution than the current one,
            // we add this flag to continue iterating (if we pass over the maximum number of iterations)
            improved = bestNeighbourValue.betterThan(currentValue) ? true : false;

            // Update the current solution and add it to the tabu list
            current = bestNeighbour.deepCopy();
            currentValue = bestNeighbourValue;
            tabuList.add(bestNeighbour);

            // Keep the list within its allowed size
            if (tabuList.size() > TABU_LIST_SIZE) {
                tabuList.remove(0);
            }

            // Update the best solution if needed
            if (currentValue.betterThan(bestValue)) {
                best = current.deepCopy();
                bestValue = currentValue;
                System.out.println("Updated best: " + best);
            }
            counter += 1;
        }
        return best;
    }

    /** Method to obtain the neighbours (goes with Algorithm 2: Finding Neighbours in the report) **/
    private List<Vector> getNeighbours(int neighbourhoodSize, Vector vector) {

        List<Vector> neighboursList = new ArrayList<>();

        for (int i = 0; i < neighbourhoodSize; i++) {

            AtomicVariable currentVar = (AtomicVariable) vector.getVariable(i);

            int range = Math.min(currentVar.getMax() - currentVar.getValue(), currentVar.getValue() - currentVar.getMin());
            int bigMove = new Random().nextInt(range + 1) + 1;

            // Create 4 new neighbours
            Vector neighbourVector1 = generateNeighbour(vector, i, 1);
            Vector neighbourVector2 = generateNeighbour(vector, i, -1);
            Vector neighbourVector3 = generateNeighbour(vector, i, bigMove);
            Vector neighbourVector4 = generateNeighbour(vector, i, -bigMove);

            neighboursList.add(neighbourVector1);
            neighboursList.add(neighbourVector2);
            neighboursList.add(neighbourVector3);
            neighboursList.add(neighbourVector4);
        }
        return neighboursList;
    }

    private Vector generateNeighbour(Vector vector, int index, int move) {
        Vector neighbourVector = vector.deepCopy();
        AtomicVariable var = (AtomicVariable) neighbourVector.getVariable(index);
        int num = var.getValue();
        var.setValue(num + move);

        return neighbourVector;
    }
}
