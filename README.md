micrograd (Java)
=================

A compact Java implementation of MicroGrad-style automatic differentiation and
small multilayer-perceptron examples. The project implements a minimal
computational graph (`Node`) with forward evaluation and backward automatic
differentiation, a set of mathematical operator implementations and a tiny
MLP trainer used in tests.

Highlights
----------

- Core autodiff building blocks: `Node`, operator functions and backprop logic.
- Modular function API: `MathFunction` / `ErrorFunction` interfaces plus
  concrete implementations (ADD, SUB, MUL, DIV, POW, EXP, TANH and MSE error).
- Visualizer: ASCII graph drawing utility for debugging/computation graphs.
- Unit tests cover forward/backward correctness and a small MLP training example.

Key files and packages
----------------------

- src/main/java/org/micrograd/core
    - Node.java — core computation node, graph construction and backprop
    - Neuron/Layer/MLP — small neural-network building blocks and trainer
- src/main/java/org/micrograd/functions
    - api — public contracts (MathFunction, ErrorFunction, abstract base classes)
    - impl — concrete function implementations (AddFunction, TanhFunction, ...)
    - registry — convenience holders (MathFunctions, ErrorMathFunctions) exposing
      singleton instances for backward compatibility
- src/main/java/org/micrograd/util
    - DrawGraph.java — ASCII graph renderer for debugging
- src/test/java — comprehensive unit tests for nodes, functions and MLP

Requirements
------------
- Java JDK 11 or later
- Apache Maven 3.6+ (used for building and running tests)

Build, test and run
-------------------
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

The unit tests include small forward/backward checks for each operator,
graph-visualization output and an MLP training test that verifies gradients
and parameter updates.

Usage examples (code snippets)
------------------------------
Create nodes and compose a simple expression:

```java
Node x = new Node("x", 2.0f);
Node w = new Node("w", -3.0f);
Node n = x.multiply(w).add(new Node("b", 1.0f));
Node o = n.getActivationVal(MathFunctions.TANH);
// backprop
o.fillGrad();
// inspect gradients
System.out.println(w.grad);
```

Train the sample MLP (tests demonstrate API):

```java
var mlp = new MLP("mlp", 3, List.of(4, 4, 1));
mlp.train(100,0.01f,inputList, expectedOutputs, MathFunctions.TANH, ErrorMathFunctions.MSE);
```

Design notes
------------

- Function API: functions live under `org.micrograd.functions.api` (interfaces)
  and `org.micrograd.functions.impl` (implementations). A small `registry`
  package exposes singleton instances so older call sites continue to work
  (`MathFunctions.TANH`, `ErrorMathFunctions.MSE`).
- Topological sort and reverse traversal are used when computing gradients to
  ensure correct backward ordering. The tests validate gradients for many
  operations and small networks.

Contributing
------------

- Add unit tests for new functions or changes. Tests live under
  `src/test/java` and are executed with `mvn test`.
- Keep changes small and focused. If you add new functions, place them under
  `org.micrograd.functions.impl` and expose them in the registry if appropriate.

License
-------
See the top-level `LICENSE` file for license terms.

Contact
-------
If you need help or want to discuss changes, open an issue in the repository.
