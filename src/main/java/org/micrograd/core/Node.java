package org.micrograd.core;

import org.micrograd.functions.api.MathFunction;
import java.util.*;

import static org.micrograd.functions.registry.MathFunctions.*;

public class Node {

    public String name;
    public float value;
    public float grad;
    public List<Node> prev;
    public MathFunction operatorFunction;

    public Node(String name, float value) {
        this.name = name;
        this.value = value;
        this.grad = 0.0f;
    }

    public Node(String name, Node operNode, MathFunction operatorFunction) {
        this.name = name.isEmpty() ? operatorFunction.getRepresentation(operNode.name) : name;
        this.value = operatorFunction.applyFunction(operNode.value);
        this.prev = List.of(operNode);
        this.operatorFunction = operatorFunction;
    }

    public Node(String name, Node firstOperNode, Node secondOperNode, MathFunction operatorFunction) {
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

    public Node applyFunction(String newName, float toOperate, MathFunction activationFunction) {
        return new Node(newName, this, new Node("__" + newName, toOperate), activationFunction);
    }

    public Node applyFunction(String newName, Node toOperate, MathFunction activationFunction) {
        return new Node(newName, this, toOperate, activationFunction);
    }

    public Node applyFunction(String newName, MathFunction activationFunction) {
        return new Node(newName, this, activationFunction);
    }

    public Node applyFunction(Node toOperate, MathFunction activationFunction) {
        return new Node("", this, toOperate, activationFunction);
    }

    public Node applyFunction(MathFunction activationFunction) {
        return new Node("", this, activationFunction);
    }

    public Node getActivationVal(String name, MathFunction activationFunction) {
        return new Node(name, this, activationFunction);
    }

    public Node getActivationVal(MathFunction activationFunction) {
        return new Node("", this, activationFunction);
    }

    public void fillGrad() {
        List<Node> sortedTopological = sortTopological();
        for (Node node : sortedTopological) {
            node.grad = 0;
        }
        this.grad = 1;
        for (Node node : sortedTopological) {
            node.fillBackwardGrad();
        }
    }

    private void fillBackwardGrad() {
        if (operatorFunction != null) {
            this.operatorFunction.backFillGradVal(this, prev);
        }
    }

    private List<Node> sortTopological() {
        var result = new LinkedList<Node>();
        var visited = new HashSet<Node>();
        sortTopological(this, result, visited);
        Collections.reverse(result);
        return result;
    }

    private void sortTopological(Node currentNode, List<Node> result, Set<Node> visited) {
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
