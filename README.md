![build status](https://travis-ci.org/qdrien/jpacman-framework.svg?branch=master)

JPacman-Framework
=================
This is a fork from https://github.com/SERG-Delft/jpacman-framework (see the original README.md [here](old_README.md)) made for the Software Evolution course at UMONS.
This version includes the "series of mazes" extension as well as a "board generator" (included in [BoardFactory](src/main/java/nl/tudelft/jpacman/board/BoardFactory.java)) that translates images to ready-to-use text files in the JPacMan format (more convenient to create levels than the default text version).

Getting Started
---------------

1. Git clone the project
2. To see JPacman in action (assuming you have [Maven](https://maven.apache.org/) installed): 
    1. Open a terminal/console/command prompt on the project's root folder/directory (where the `pom.xml` file is located)
    2. compile: `mvn compile`
    3. run: `mvn exec:java -Dexec.mainClass="nl.tudelft.jpacman.Launcher"`
    4. Note that this version is by default in "quick win" mode (disable this by setting the `QUICK_WIN` field to `false` in [Level](src/main/java/nl/tudelft/jpacman/level/Level.java))
        When enabled, this mode means that picking up 13 pellets is enough to complete a level (more convenient to test multiple levels).
4. To run the test suite in maven: `mvn test` (you can alternatively check build states and logs on [Travis](https://travis-ci.org/qdrien/jpacman-framework))