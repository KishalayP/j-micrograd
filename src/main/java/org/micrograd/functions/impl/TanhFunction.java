package org.micrograd.functions.impl;

import org.micrograd.core.Node;
import org.micrograd.functions.api.AbstractMathFunction;

import java.util.List;

public class TanhFunction extends AbstractMathFunction {
    @Override
    public String getRepresentation(String x) {
        return "tanh(%s)".formatted(x);
    }

    @Override
    public float applyFunction(float x) {
        double exp = Math.exp(2 * x);
        return (float) ((exp - 1) / (exp + 1));
    }

    @Override
    public void backFillGradVal(Node resultNode, List<Node> operandNodes) {
        if (operandNodes != null && operandNodes.size() >= 1) {
            var gradOfResultFunction = 1 - (resultNode.value * resultNode.value);
            operandNodes.get(0).grad += resultNode.grad * gradOfResultFunction;
        }
    }

    @Override
    public String toString() {
        return "tanh";
    }
}

