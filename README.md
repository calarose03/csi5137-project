# Iterated Tabu Search (CSI 5137 Project)

The Iterated Tabu Search is based off of the Iterated Local Search, using the Tabu Search. 
In this implementation, it attempts to find test data for a problem and target branch given.

Since this implementation depends on the AVM framework, to install and compile it, follow the guidelines in `Avmf-README.md`.


## Running the Provided Examples

### Test Data Generation

Similarly to `GenerateInputData` class in the `org.avmframework.examples` package, 
the `GenerateInputDataITS` class in the `org.project.its` package shows how the ITS 
may be applied to generating input data.

There are three test objects, `Calendar`, `Line`, and `Triangle`.

To generate input data for a test object, use the following command

```
java -cp target/avmf-1.0-jar-with-dependencies.jar org.project.its.GenerateInputDataITS testobject branchID its
```

`Calendar` has branch IDs ranging from 1T/F to 23T/F. `Line` has branch IDs
ranging from 1T/F to 7T/F, while `Triangle` has branch IDs ranging from 1T/F
to 8T/F.

The `its` property is a boolean that specifies if the generation will go through the Iterated Tabu Search or
standard Tabu Search (`true` for ITS, `false` for TS).

As an example to obtain input data for test object Triangle to cover branch 3T only via standard TS, use 
the following command:

```
java -cp target/avmf-1.0-jar-with-dependencies.jar org.project.its.GenerateInputDataITS Triangle 3T false
```


For more information about the framework, please read `Avmf-README.md`.