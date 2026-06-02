micrograd (Java)
=================

A small Java implementation of MicroGrad-style automatic differentiation.
This repository contains core autodiff building blocks (a `Node` class and
math functions), a few utility classes, and unit tests.

Key files

- src/main/java/org/micrograd/core/Node.java — core computational node
- src/main/java/org/micrograd/functions/MathFunctions.java — math ops
- src/main/java/org/micrograd/util/DrawGraph.java — (utility) draw computation graph
- src/main/java/org/micrograd/util/Position.java — helper type for graph drawing
- src/test/java/org/micrograd/NodeTest.java — unit tests

Requirements

- Java JDK 11 or later
- Apache Maven 3.6+ (used for building and running tests)

Build, test and run
From the repository root (PowerShell examples):

```powershell
# show Maven and Java versions
mvn -v
java -version

# run the unit tests
mvn test

# build the project (produces target/)
mvn package
```

If you want to run compiled classes directly, build with `mvn package` and then run
any produced jars or class files as appropriate. This project is primarily
intended for library/educational use and unit tests demonstrate usage.

Project layout

- src/main/java — main source code
- src/test/java — unit tests
- target — build output (ignored in version control)

Contributing

- Feel free to open issues or PRs. Keep changes small and focused.
- Follow project coding style and include unit tests for new behavior.

License
This repository includes a top-level `LICENSE` file. Please refer to that file
for license terms.

Contact
If you need help or want to discuss changes, open an issue in the repository.

