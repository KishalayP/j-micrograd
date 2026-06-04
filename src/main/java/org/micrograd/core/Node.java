package org.micrograd.core;

import org.micrograd.functions.MathFunctions;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.micrograd.functions.MathFunctions.*;

public class Node {

    public String name;
    public float value;
    public float grad;
    public List<Node> prev;
    public MathFunctions operatorFunction;

    public Node(String name, float value) {
        this.name = name;
        this.value = value;
        this.grad = 0.0f;
    }

    public Node(String name, Node operNode, MathFunctions operatorFunction) {
        this.name = name.isEmpty() ? operatorFunction.getRepresentation(operNode.name) : name;
        this.value = operatorFunction.applyFunction(operNode.value);
        this.prev = List.of(operNode);
        this.operatorFunction = operatorFunction;
    }

    public Node(String name, Node firstOperNode, Node secondOperNode, MathFunctions operatorFunction) {
        this.name = name.isEmpty() ? operatorFunction.getRepresentation(firstOperNode.name, secondOperNode.name) : name;
        this.value = operatorFunction.applyFunction(firstOperNode.value, secondOperNode.value);
        this.prev = List.of(firstOperNode, secondOperNode);
        this.operatorFunction = operatorFunction;
    }

    public Node add(String newName, Node toOperate) {
        return new Node(newName, this, toOperate, ADD);
    }

    public Node add(Node toOperate) {
        return new Node("", this, toOperate, ADD);
    }

    public Node subtract(String newName, Node toOperate) {
        return new Node(newName, this, toOperate, SUB);
    }

    public Node subtract(Node toOperate) {
        return new Node("", this, toOperate, SUB);
    }

    public Node multiply(String newName, float toOperate) {
        return new Node(newName, this, new Node("__" + newName, toOperate), MUL);
    }

    public Node multiply(String newName, Node toOperate) {
        return new Node(newName, this, toOperate, MUL);
    }

    public Node multiply(Node toOperate) {
        return new Node("", this, toOperate, MUL);
    }

    public Node divide(String newName, Node toOperate) {
        return new Node(newName, this, toOperate, DIV);
    }

    public Node divide(Node toOperate) {
        return new Node("", this, toOperate, DIV);
    }

    public Node applyFunction(String newName, float toOperate, MathFunctions activationFunction) {
        return new Node(newName, this, new Node("__" + newName, toOperate), activationFunction);
    }

    public Node applyFunction(String newName, Node toOperate, MathFunctions activationFunction) {
        return new Node(newName, this, toOperate, activationFunction);
    }

    public Node applyFunction(String newName, MathFunctions activationFunction) {
        return new Node(newName, this, activationFunction);
    }

    public Node applyFunction(Node toOperate, MathFunctions activationFunction) {
        return new Node("", this, toOperate, activationFunction);
    }

    public Node applyFunction(MathFunctions activationFunction) {
        return new Node("", this, activationFunction);
    }

    public Node getActivationVal(String name, MathFunctions activationFunction) {
        return new Node(name, this, activationFunction);
    }

    public Node getActivationVal(MathFunctions activationFunction) {
        return new Node("", this, activationFunction);
    }

    public void fillGrad() {
        this.grad = 1;
        for (Node node : sortTopological()) {
            node.fillBackwardGrad();
        }
    }

    private void fillBackwardGrad() {
        if (operatorFunction != null) {
            this.operatorFunction.backFillGradVal(this, prev);
        }
    }

    private Set<Node> sortTopological() {
        var result = new LinkedHashSet<Node>();
        var visited = new HashSet<Node>();
        sortTopological(this, result, visited);
        return result.reversed();
    }

    private void sortTopological(Node currentNode, Set<Node> result, Set<Node> visited) {
        if (currentNode.prev == null) {
            result.add(currentNode);
            return;
        }
        if (!visited.contains(currentNode)) {
            visited.add(currentNode);
            for (Node node : currentNode.prev) {
                sortTopological(node, result, visited);
            }
            result.add(currentNode);
        }
    }

    @Override
    public String toString() {
        return "Name: %s | Value: %.4f | Grad: %.4f".formatted(this.name, this.value, this.grad);
    }
}
