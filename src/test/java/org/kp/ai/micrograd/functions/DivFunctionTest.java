package org.kp.ai.micrograd.functions;

import org.junit.Test;
import org.kp.ai.micrograd.core.Node;

import static org.junit.Assert.assertEquals;

public class DivFunctionTest {

    @Test
    public void testDivForwardBackprop() {
        var a = new Node("a", 6.0f);
        var b = new Node("b", 2.0f);
        Node c = a.divide(b);

        // forward
        assertEquals("div forward", 3.0f, c.value, 1e-6f);

        // backward
        c.fillGrad();
        assertEquals("a.grad should equal 1/b after div backprop", 1.0f / b.value, a.grad, 1e-6f);
        assertEquals("b.grad should equal -a/(b^2) after div backprop", -(a.value / (b.value * b.value)), b.grad, 1e-6f);
    }
}

