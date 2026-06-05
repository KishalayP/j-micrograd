package org.micrograd.functions.impl;

import org.micrograd.core.Node;
import org.micrograd.functions.api.AbstractMathFunction;

import java.util.List;

public class ExpFunction extends AbstractMathFunction {
    @Override
    public String getRepresentation(String x) {
        return "(exp^%s)".formatted(x);
    }

    @Override
    public float applyFunction(float x) {
        return (float) Math.exp(x);
    }

    @Override
    public void backFillGradVal(Node resultNode, List<Node> operandNodes) {
        if (operandNodes != null && !operandNodes.isEmpty()) {
            Node operand = operandNodes.getFirst();
            // derivative of exp(x) is exp(x) which equals resultNode.value
            operand.grad += resultNode.grad * resultNode.value;
        }
    }

    @Override
    public String toString() {
        return "exp";
    }
}

