package org.project.its;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import org.avmframework.Monitor;
import org.avmframework.TerminationPolicy;
import org.avmframework.Vector;
import org.avmframework.examples.inputdatageneration.Branch;
import org.avmframework.examples.inputdatageneration.TestObject;
import org.avmframework.examples.util.ArgsParser;
import org.avmframework.initialization.Initializer;
import org.avmframework.initialization.RandomInitializer;
import org.avmframework.objective.ObjectiveFunction;

public class GenerateInputDataITS {
	
	static final String SEARCH_NAME = "HillClimbingSearch"; // can also be set at the command line
	 static final int MAX_EVALUATIONS = 100000;
	
	public static void main(String[] args) {
	    // Nested class to help parse command line arguments
		GenerateInputDataITSArgsParser argsParser = new GenerateInputDataITSArgsParser(args);

	    // instantiate the test object using command line parameters
	    TestObject testObject = argsParser.parseTestObjectParam();

	    // instantiate the branch using command line parameters
	    Branch target = argsParser.parseBranchParam(testObject);

	   // set up the objective function
	    ObjectiveFunction objFun = testObject.getObjectiveFunction(target);

	    // set up the vector
	    Vector vector = testObject.getVector();

	    // set up the termination policy
	    TerminationPolicy terminationPolicy =
	        TerminationPolicy.createMaxEvaluationsTerminationPolicy(MAX_EVALUATIONS);

	    // set up random initialization of vectors
	    RandomGenerator randomGenerator = new MersenneTwister();
	    Initializer initializer = new RandomInitializer(randomGenerator);

	    // set up the AlternatingVariableMethod
	    IteratedTabuSearch its = new IteratedTabuSearch(terminationPolicy, initializer);

	    // perform the search
	    Monitor monitor = its.search(vector, objFun);

	    // output the results
	    System.out.println("Best solution: " + monitor.getBestVector());
	    System.out.println("Best objective value: " + monitor.getBestObjVal());
	    System.out.println(
	        "Number of objective function evaluations: "
	            + monitor.getNumEvaluations()
	            + " (unique: "
	            + monitor.getNumUniqueEvaluations()
	            + ")");
	    System.out.println("Running time: " + monitor.getRunningTime() + "ms");
	  }

	  static class GenerateInputDataITSArgsParser extends ArgsParser {
	    static final int TEST_OBJECT_PARAM_INDEX = 0;
	    static final int BRANCH_PARAM_INDEX = 1;
	    static final int SEARCH_PARAM_INDEX = 2;
	    static final int MIN_NUM_ARGS = 2;

	    GenerateInputDataITSArgsParser(String[] args) {
	      super(GenerateInputDataITS.class, args);
	    }

	    protected void addParams() {
	      addParam(
	          "testobject",
	          "a test object to generate data for (e.g., \"Calendar\", \"Line\" or \"Triangle\")");
	      addParam(
	          "branch",
	          "a branch ID of the form X(T|F) where X is a branching node number (e.g., \"5T\")");
	      super.addParams();
	    }

	    TestObject parseTestObjectParam() {
	      TestObject testObject = null;
	      if (args.length > TEST_OBJECT_PARAM_INDEX) {
	        String testObjectName = args[TEST_OBJECT_PARAM_INDEX];

	        try {
	          testObject = TestObject.instantiate(testObjectName);
	        } catch (Exception exception) {
	          error(exception.getMessage());
	        }

	      } else {
	        wrongNumberOfArgumentsError();
	      }

	      return testObject;
	    }

	    Branch parseBranchParam(TestObject testObject) {
	      Branch branch = null;
	      if (args.length > BRANCH_PARAM_INDEX) {
	        String suppliedParam = args[BRANCH_PARAM_INDEX];

	        try {
	          branch = Branch.instantiate(suppliedParam, testObject);
	        } catch (Exception exception) {
	          error(exception.getMessage());
	        }
	      } else {
	        wrongNumberOfArgumentsError();
	      }

	      return branch;
	    }

	    void wrongNumberOfArgumentsError() {
	      error(
	          "Wrong number of arguments -- expected at least " + MIN_NUM_ARGS + " got " + args.length);
	    }
	  }
}
