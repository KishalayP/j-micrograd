package org.micrograd.functions;

import org.micrograd.core.Node;

public enum ErrorMathFunctions {

    MSE {
        @Override
        public Node getError(Node currentLoss, Node target, Node prediction) {
            Node diff = target.subtract(prediction);
            return currentLoss.add(diff.multiply(diff));
        }
    };

    public abstract Node getError(Node currentLoss, Node target, Node prediction);
}
