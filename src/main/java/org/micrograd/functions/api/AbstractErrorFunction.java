package org.micrograd.functions.api;

import org.micrograd.core.Node;

public abstract class AbstractErrorFunction implements ErrorFunction {
    @Override
    public Node getError(Node currentLoss, Node target, Node prediction) {
        // default: no change to loss
        return currentLoss;
    }
}

