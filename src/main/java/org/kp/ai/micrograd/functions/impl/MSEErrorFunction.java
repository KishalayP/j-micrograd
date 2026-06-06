package org.kp.ai.micrograd.functions.impl;

import org.kp.ai.micrograd.core.Node;
import org.kp.ai.micrograd.functions.api.AbstractErrorFunction;

public class MSEErrorFunction extends AbstractErrorFunction {
    @Override
    public Node getError(Node currentLoss, Node target, Node prediction) {
        Node diff = target.subtract(prediction);
        return currentLoss.add(diff.multiply(diff));
    }
}

