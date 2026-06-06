package org.micrograd.functions;

import org.junit.Test;
import org.micrograd.core.Node;
import org.micrograd.functions.registry.MathFunctions;

import static org.junit.Assert.assertEquals;

public class PowFunctionTest {

    @Test
    public void testPowForwardBackprop() {
        var x = new Node("x", 2.0f);
        var y = new Node("y", 3.0f);
        // create pow node: x^y
        Node p = new Node("p", x, y, MathFunctions.POW);

        // forward
        assertEquals("pow forward", (float) Math.pow(x.value, y.value), p.value, 1e-6f);

        // backward
        p.fillGrad();
        float expectedBaseGrad = (float) (y.value * Math.pow(x.value, y.value - 1));
        assertEquals("base grad", expectedBaseGrad, x.grad, 1e-6f);
        // exponent gradient exists only for base>0 in our implementation
        if (x.value > 0) {
            float expectedExpGrad = p.value * (float) Math.log(x.value);
            assertEquals("exp grad", expectedExpGrad, y.grad, 1e-6f);
        }
    }
}

