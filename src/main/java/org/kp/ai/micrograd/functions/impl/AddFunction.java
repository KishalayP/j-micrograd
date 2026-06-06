package org.kp.ai.micrograd.functions.impl;

import org.kp.ai.micrograd.core.Node;
import org.kp.ai.micrograd.functions.api.AbstractMathFunction;

import java.util.List;

public class AddFunction extends AbstractMathFunction {
    @Override
    public String getRepresentation(String x, String y) {
        return "(%s + %s)".formatted(x, y);
    }

    @Override
    public float applyFunction(float x, float y) {
        return x + y;
    }

    @Override
    public void backFillGradVal(Node resultNode, List<Node> operandNodes) {
        if (operandNodes != null && operandNodes.size() >= 2) {
            Node firstOperNode = operandNodes.get(0);
            Node secondOperNode = operandNodes.get(1);
            firstOperNode.grad += resultNode.grad;
            secondOperNode.grad += resultNode.grad;
        }
    }

    @Override
    public String toString() {
        return "+";
    }
}

