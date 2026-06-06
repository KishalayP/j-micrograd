package org.kp.ai.micrograd.functions.api;

import org.kp.ai.micrograd.core.Node;

public abstract class AbstractErrorFunction implements ErrorFunction {
    @Override
    public Node getError(Node currentLoss, Node target, Node prediction) {
        // default: no change to loss
        return currentLoss;
    }
}

