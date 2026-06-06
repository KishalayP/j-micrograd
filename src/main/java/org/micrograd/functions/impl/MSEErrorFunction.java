package org.micrograd.functions.impl;

import org.micrograd.core.Node;
import org.micrograd.functions.api.AbstractErrorFunction;

public class MSEErrorFunction extends AbstractErrorFunction {
    @Override
    public Node getError(Node currentLoss, Node target, Node prediction) {
        Node diff = target.subtract(prediction);
        return currentLoss.add(diff.multiply(diff));
    }
}

