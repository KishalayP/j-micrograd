package org.kp.ai.micrograd.functions;

import org.junit.Test;
import org.kp.ai.micrograd.core.Node;
import org.kp.ai.micrograd.functions.registry.MathFunctions;

import static org.junit.Assert.assertEquals;

public class ExpFunctionTest {

    @Test
    public void testExpForwardBackprop() {
        var x = new Node("x", 1.5f);
        Node e = new Node("e", x, MathFunctions.EXP);

        // forward
        assertEquals("exp forward", (float) Math.exp(x.value), e.value, 1e-6f);

        // backward
        e.fillGrad();
        assertEquals("exp backprop operand grad equals result.value", e.value, x.grad, 1e-6f);
    }
}

