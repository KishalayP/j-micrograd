package org.micrograd.functions;

import org.junit.Test;
import org.micrograd.core.Node;

import static org.junit.Assert.assertEquals;

public class MulFunctionTest {

    @Test
    public void testMulForwardBackprop() {
        var a = new Node("a", 4.0f);
        var b = new Node("b", 2.5f);
        Node c = a.multiply(b);

        // forward
        assertEquals("mul forward", 10.0f, c.value, 1e-6f);

        // backward
        c.fillGrad();
        assertEquals("a.grad should equal b after mul backprop", b.value, a.grad, 1e-6f);
        assertEquals("b.grad should equal a after mul backprop", a.value, b.grad, 1e-6f);
    }
}

