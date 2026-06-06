package org.kp.ai.micrograd.functions.api;

import org.kp.ai.micrograd.core.Node;

import java.util.List;

public interface MathFunction {
    String getRepresentation(String x);

    String getRepresentation(String x, String y);

    float applyFunction(float x);

    float applyFunction(float x, float y);

    void backFillGradVal(Node resultNode, List<Node> operandNodes);
}

