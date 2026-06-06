package org.micrograd.functions.api;

import org.micrograd.core.Node;

public interface ErrorFunction {
    Node getError(Node currentLoss, Node target, Node prediction);
}

