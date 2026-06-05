package org.micrograd.functions;

import org.junit.Test;
import org.micrograd.core.Node;
import org.micrograd.functions.registry.ErrorMathFunctions;

import static org.junit.Assert.assertEquals;

public class MSEErrorFunctionTest {

    @Test
    public void testMSEErrorForwardBackprop() {
        Node loss = new Node("l", 0f);
        Node target = new Node("t", 2.0f);
        Node pred = new Node("p", 1.0f);

        // use holder instance for backward compatibility
        Node newLoss = ErrorMathFunctions.MSE.getError(loss, target, pred);

        // forward: (2 - 1)^2 = 1
        assertEquals("mse forward", 1.0f, newLoss.value, 1e-6f);

        // backward
        newLoss.fillGrad();
        // derivative wrt pred is -2*(target - pred) = -2*1 = -2
        assertEquals("pred.grad", -2.0f, pred.grad, 1e-6f);
        // derivative wrt target is +2*(target - pred) = 2
        assertEquals("target.grad", 2.0f, target.grad, 1e-6f);
    }
}

