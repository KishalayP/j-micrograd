package org.kp.ai.micrograd.functions.impl;

import org.kp.ai.micrograd.core.Node;
import org.kp.ai.micrograd.functions.api.AbstractMathFunction;

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
        if (operandNodes != null && !operandNodes.isEmpty()) {
            var gradOfResultFunction = 1 - (resultNode.value * resultNode.value);
            operandNodes.getFirst().grad += resultNode.grad * gradOfResultFunction;
        }
    }

    @Override
    public String toString() {
        return "tanh";
    }
}

