package org.kp.ai.micrograd.functions;

import org.junit.Test;
import org.kp.ai.micrograd.core.Node;
import org.kp.ai.micrograd.functions.registry.MathFunctions;

import static org.junit.Assert.assertEquals;

public class TanhFunctionTest {

    @Test
    public void testTanhForwardBackprop() {
        var x = new Node("x", 0.7f);
        Node t = new Node("t", x, MathFunctions.TANH);

        // forward
        float expected = (float) Math.tanh(x.value);
        assertEquals("tanh forward", expected, t.value, 1e-6f);

        // backward
        t.fillGrad();
        float expectedGrad = 1 - (t.value * t.value);
        assertEquals("tanh backprop operand grad", expectedGrad, x.grad, 1e-6f);
    }
}

