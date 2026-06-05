package org.micrograd.functions;

import org.junit.Test;
import org.micrograd.core.Node;

import static org.junit.Assert.assertEquals;

public class SubFunctionTest {

    @Test
    public void testSubForwardBackprop() {
        var a = new Node("a", 5.0f);
        var b = new Node("b", 2.0f);
        Node c = a.subtract(b);

        // forward
        assertEquals("sub forward", 3.0f, c.value, 1e-6f);

        // backward
        c.fillGrad();
        assertEquals("a.grad should be 1 after sub backprop", 1.0f, a.grad, 1e-6f);
        assertEquals("b.grad should be -1 after sub backprop", -1.0f, b.grad, 1e-6f);
    }
}

