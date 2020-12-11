# Random Walk Domination

## Introduction
This repo holds the source code and scripts for random walk domination.

We also upload some datasets that you can play with in the folder "data".

## Usage
We take doing some tests over PR when k varies as an example.
### Step 1. Do some configurations
1. Go to src/main/java/au/edu/rmit/randomwalk/experiment/VaryingK.java.
2. Change function *readDataset* to load the target dataset.
```
    private AbstractGraphNode[] readDataset() {
        return fileManager.readHepPhNetwork();
    }
```
3. Set parameter L, T, startNK, endNK, deltaK.
```
    L: the cost (length) of the weighted random walk.
    T: the repeated times.
    startNK: the start number of K.
    endNK: the end number of K.
    deltaK: the step length of K.
```
4. Set parameter EXP_PREFIX to specify the output folder.
### Step 2. Run
1. Go to /src/main/java/au/edu/rmit/randomwalk/AppEngine.java.
2. Call different functions to test different algorithms in AppEngine.
```
    degreeGreedy(): TopK.
    dpGreedy(): DpSel.
    matrixSel(): MatrixSel.
    boundSel(): BoundSel.
    approximateGreedy(): SamSel.
    pagerank(): PageRankSel.
```
If you want to do some tests over CR, you can call function *BuildCounterPart* in /src/main/java/au/edu/rmit/randomwalk/io/FileManager.java to build the counterpart graph.
Then, you can follow Step 1 and Step 2 to do the tests.
