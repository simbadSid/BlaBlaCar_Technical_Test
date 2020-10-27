# BlaBlaCar technical test
This repository contains our solution to the BlaBlaCar technical test (see src/main/resources/BlaBlaCar_TechnicalTest.pdf).

## Principle of the solution
Our solution for the simulation is a multithreaded approach.
It is based on the "Ahead of Time Evaluation".
Each mower is simulated with an independent thread.
This thread computes the new positions reached by processing the input instructions of the mower.
The __*mower*__ thread computes its successive positions independently of the others.

Meanwhile, a set of __*checker*__ threads are responsible to check, at each iteration, all the mowers for a potential collision.
If a collision between two mowers is detected, one of them is chosen; and the instruction that led to the position is silently-canceled.

One of the main interest of our approach is the good separation of memory between threads.
Most of the time, each thread access its own memory only.
This data-locality allows a proper hardware memory-caching (hence fast memory accesses).
Consequently, our approach scales properly with the number of threads (mowers).
It also allows to split properly the workload between CPUs.

Our method is designed to embed a future efficient improvement (in the next version of the code) based on the clustering of the mowers.
Indeed, two mowers that are relatively far from each others do not need to be checked at each iteration.
Consequently, we have considered clustering the mowers according to their relative distance.
Each cluster of mowers being checked by a single and independant __*checker*__ thread.
Consequently, at each iteration, a mower's position is only compared to its closer mowers. 

## Build
Our project is built and deployed using the Apache Ant toolset.
The build script is entirely implemented in "./build.xml".
In order to build an executable, a user needs to run the command:
```
ant distribute
```
This generates an executable jar file with our main simulation program.
Similarly, the unit test executables may be generated using
```
ant distributeTest
```

## Usage
Once the executable has been generated, our program may be tested using
```
java -jar dist/lib/BlaBlaCar_Technical-1.0.jar
```
By default, the simulation is executed using the input file in "src/test/resources/input/NoCollision_NoOutField_Mower2_X5_Y5_1".
However, a different input file may be specified using
```
java -jar dist/lib/BlaBlaCar_Technical-1.0.jar --inputFile <file path>
```
All the other options may be discovered using the --help input.

## Test
The different scenario that we have implemented to test our code are in "./src/test/resources/input".
Some of these scenario have been manually solved and saved in "./src/test/resources/output".
They allow to check the functional correctness of the code.
Other scenarios are used for the high number of instruction and/or mowers.
All these tests are based on JUnit and may be executed using
```
java -jar dist/lib/BlaBlaCar_TechnicalTest-1.0.jar
```


## Generate Java doc
The javadoc of all the classes of this project may be generated using
```
ant doc
```
This results in creating the html file corresponding to each class.
 This file is stored within "./doc".

## Note
Some libraries (such as the Debug or Log libraries) have been reimplemented in this project.
Our re-implementation is a lightweight code that allows this project to only use standard libraries.
This is done on purpose in order to ease the deployment and the review.

## Futur implementations
1. Javadoc: correct @warning keyword
1. Add the support for thread-affinity by scanning the number of CPU and distributing the threads
1. Finish the implementation of the concurrent checker:
   1. Implement the function to decide and process the clustering between groups of mowers.
   1. Implement a decision function to decide which checker to use (simple of clustered) depending on the number of mower and concurrent threads.
1. Generate different scenarios for unit tests