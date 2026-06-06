package org.kp.ai.micrograd.functions.api;

import org.kp.ai.micrograd.core.Node;

public interface ErrorFunction {
    Node getError(Node currentLoss, Node target, Node prediction);
}

