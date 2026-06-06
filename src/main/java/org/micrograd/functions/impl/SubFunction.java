package org.micrograd.functions.impl;

import org.micrograd.core.Node;
import org.micrograd.functions.api.AbstractMathFunction;

import java.util.List;

public class SubFunction extends AbstractMathFunction {
    @Override
    public String getRepresentation(String x, String y) {
        return "(%s - %s)".formatted(x, y);
    }

    @Override
    public float applyFunction(float x, float y) {
        return x - y;
    }

    @Override
    public void backFillGradVal(Node resultNode, List<Node> operandNodes) {
        if (operandNodes != null && operandNodes.size() >= 2) {
            Node firstOperNode = operandNodes.get(0);
            Node secondOperNode = operandNodes.get(1);
            firstOperNode.grad += resultNode.grad;
            secondOperNode.grad -= resultNode.grad; // derivative wrt second operand is -1
        }
    }

    @Override
    public String toString() {
        return "-";
    }
}

