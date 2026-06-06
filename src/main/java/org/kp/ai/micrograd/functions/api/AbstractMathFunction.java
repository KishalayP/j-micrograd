package org.kp.ai.micrograd.functions.api;

import org.kp.ai.micrograd.core.Node;

import java.util.List;

public abstract class AbstractMathFunction implements MathFunction {
    @Override
    public String getRepresentation(String x) {
        return "";
    }

    @Override
    public String getRepresentation(String x, String y) {
        return "";
    }

    @Override
    public float applyFunction(float x) {
        return 0f;
    }

    @Override
    public float applyFunction(float x, float y) {
        return 0f;
    }

    @Override
    public void backFillGradVal(Node resultNode, List<Node> operandNodes) { /* default no-op */ }
}

