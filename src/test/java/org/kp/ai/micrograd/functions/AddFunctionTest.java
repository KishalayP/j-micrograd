package org.kp.ai.micrograd.functions;

import org.junit.Test;
import org.kp.ai.micrograd.core.Node;

import static org.junit.Assert.assertEquals;

public class AddFunctionTest {

    @Test
    public void testAddForwardBackprop() {
        var a = new Node("a", 2.0f);
        var b = new Node("b", 3.0f);
        Node c = a.add(b);

        // forward
        assertEquals("add forward", 5.0f, c.value, 1e-6f);

        // backward
        c.fillGrad();
        assertEquals("a.grad should be 1 after add backprop", 1.0f, a.grad, 1e-6f);
        assertEquals("b.grad should be 1 after add backprop", 1.0f, b.grad, 1e-6f);
    }
}

