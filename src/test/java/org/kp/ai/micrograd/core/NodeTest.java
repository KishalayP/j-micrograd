package org.kp.ai.micrograd.core;

import org.junit.Test;
import org.kp.ai.micrograd.functions.registry.MathFunctions;
import org.kp.ai.micrograd.util.DrawGraph;

import static org.junit.Assert.assertEquals;

public class NodeTest {

    @Test
    // TestNN1: simple arithmetic graph
    // Graph: a=2, b=2, c=a+b, d=2, e=c*d, f=3, g=e*f
    // Verifies forward pass values and gradients after backprop through g
    public void testNN1() {
        var a = new Node("a", 2);
        var b = new Node("b", 2);
        Node c = a.add("c", b);
        var d = new Node("d", 2);
        Node e = c.multiply("e", d);
        var f = new Node("f", 3);
        Node g = e.multiply("g", f);
        // Forward pass checks: ensure node values are computed correctly
        assertEquals("a.value should be 2.0", 2.0f, a.value, 1e-6f); // a initialized to 2
        assertEquals("b.value should be 2.0", 2.0f, b.value, 1e-6f); // b initialized to 2
        assertEquals("c.value should be a + b = 4.0", 4.0f, c.value, 1e-6f); // c = a + b = 4
        assertEquals("d.value should be 2.0", 2.0f, d.value, 1e-6f); // d initialized to 2
        assertEquals("e.value should be c * d = 8.0", 8.0f, e.value, 1e-6f); // e = c * d = 4 * 2 = 8
        assertEquals("f.value should be 3.0", 3.0f, f.value, 1e-6f); // f initialized to 3
        assertEquals("g.value should be e * f = 24.0", 24.0f, g.value, 1e-6f); // g = e * f = 8 * 3 = 24

        // compute gradients with automatic backprop
        g.fillGrad();

        // Backpropagation checks: expected gradients (hand-derived)
        assertEquals("g.grad should be seed 1.0 after fillGrad", 1.0f, g.grad, 1e-6f); // seed gradient at output
        assertEquals("e.grad should equal f (3.0)", 3.0f, e.grad, 1e-6f); // de/dg = f => 3
        assertEquals("f.grad should equal e (8.0)", 8.0f, f.grad, 1e-6f); // df/dg = e => 8
        assertEquals("c.grad should equal de/dg * dd/de = 3*2 = 6", 6.0f, c.grad, 1e-6f); // dc/de = d => 2; de/dg * dc/de = 3 * 2 = 6
        assertEquals("a.grad should equal 6.0 (propagated through c)", 6.0f, a.grad, 1e-6f); // da/dc = 1; chain => 6
        assertEquals("b.grad should equal 6.0 (propagated through c)", 6.0f, b.grad, 1e-6f); // db/dc = 1; chain => 6
        assertEquals("d.grad should equal de/dg * dd/de = 3*4 = 12", 12.0f, d.grad, 1e-6f); // dd/de = c => 4; de/dg * dd/de = 3 * 4 = 12
    }

    @Test
    // TestNN2: tanh activation with manual gradient propagation
    // Graph: o = tanh(n), where n = x1*w1 + x2*w2 + b
    // This test checks forward values and manually computed gradients
    public void testNN2() {
        var x1 = new Node("x1", 2);
        var x2 = new Node("x2", 0);
        var w1 = new Node("w1", -3);
        var w2 = new Node("w2", 1);
        var b = new Node("b", 6.8814f);
        var x1w1 = x1.multiply(w1);
        var x2w2 = x2.multiply(w2);
        var x1w1xx2w2 = x1w1.add(x2w2);
        var n = x1w1xx2w2.add("n", b);
        Node o = n.getActivationVal("o", MathFunctions.TANH);

        // Forward pass: validate n and activated output o
        float expectedN = x1w1xx2w2.value + b.value; // n = x1*w1 + x2*w2 + b
        assertEquals("n.value should equal x1*w1 + x2*w2 + b", expectedN, n.value, 1e-6f);
        float expectedO = (float) Math.tanh(expectedN); // o = tanh(n)
        assertEquals("o.value should equal tanh(n)", expectedO, o.value, 1e-6f);

        // Manual backward propagation (chain rule): seed output gradient and propagate
        o.grad = 1.0f; // seed
        // derivative of tanh is 1 - tanh(n)^2
        n.grad = 1 - (o.value * o.value);
        // propagate to the add node and bias
        x1w1xx2w2.grad = n.grad;
        b.grad = n.grad;
        // propagate into multiplication nodes
        x1w1.grad = x1w1xx2w2.grad;
        x2w2.grad = x1w1xx2w2.grad;
        // gradients for inputs via multiplication chain rule
        x1.grad = w1.value * x1w1.grad;
        w1.grad = x1.value * x1w1.grad;
        x2.grad = w2.value * x2w2.grad;
        w2.grad = x2.value * x2w2.grad;

        // Assertions: numeric values for gradients based on manual propagation
        float expectedNGrad = 1 - (o.value * o.value);
        assertEquals("n.grad should be tanh'(n)", expectedNGrad, n.grad, 1e-6f); // tanh' at n
        assertEquals("b.grad should equal n.grad (bias)", expectedNGrad, b.grad, 1e-6f); // bias receives same gradient
        assertEquals("sum node grad should equal n.grad", expectedNGrad, x1w1xx2w2.grad, 1e-6f); // add node
        assertEquals("x1*w1 node grad should equal n.grad", expectedNGrad, x1w1.grad, 1e-6f); // multiplication node gradients
        assertEquals("x2*w2 node grad should equal n.grad", expectedNGrad, x2w2.grad, 1e-6f);
        assertEquals("x1.grad should equal w1 * n.grad", w1.value * expectedNGrad, x1.grad, 1e-6f); // dx1 = w1 * grad
        assertEquals("w1.grad should equal x1 * n.grad", x1.value * expectedNGrad, w1.grad, 1e-6f); // dw1 = x1 * grad
        assertEquals("x2.grad should equal w2 * n.grad", w2.value * expectedNGrad, x2.grad, 1e-6f);
        assertEquals("w2.grad should equal x2 * n.grad", x2.value * expectedNGrad, w2.grad, 1e-6f);
    }

    @Test
    // TestNN3: validate automatic backprop (fillGrad) produces same gradients as manual approach
    public void testNN3() {
        var x1 = new Node("x1", 2);
        var x2 = new Node("x2", 0);
        var w1 = new Node("w1", -3);
        var w2 = new Node("w2", 1);
        var b = new Node("b", 6.8814f);
        var x1w1 = x1.multiply(w1);
        var x2w2 = x2.multiply(w2);
        var x1w1xx2w2 = x1w1.add(x2w2);
        var n = x1w1xx2w2.add("n", b);
        Node o = n.getActivationVal("o", MathFunctions.TANH);
        // use automatic backprop and assert gradients
        o.fillGrad();

        float expectedNGrad = 1 - (o.value * o.value);
        assertEquals(expectedNGrad, n.grad, 1e-6f);
        assertEquals(expectedNGrad, b.grad, 1e-6f);
        assertEquals(expectedNGrad, x1w1xx2w2.grad, 1e-6f);
        assertEquals(expectedNGrad, x1w1.grad, 1e-6f);
        assertEquals(expectedNGrad, x2w2.grad, 1e-6f);
        assertEquals(w1.value * expectedNGrad, x1.grad, 1e-6f);
        assertEquals(x1.value * expectedNGrad, w1.grad, 1e-6f);
        assertEquals(w2.value * expectedNGrad, x2.grad, 1e-6f);
        assertEquals(x2.value * expectedNGrad, w2.grad, 1e-6f);
    }

    @Test
    // TestNN4: automatic backprop on activation node (same network as NN2) - verifies fillGrad correctness
    public void testNN4() {
        var x1 = new Node("x1", 2);
        var x2 = new Node("x2", 0);
        var w1 = new Node("w1", -3);
        var w2 = new Node("w2", 1);
        var b = new Node("b", 6.8814f);
        var x1w1 = x1.multiply(w1);
        var x2w2 = x2.multiply(w2);
        var x1w1xx2w2 = x1w1.add(x2w2);
        var n = x1w1xx2w2.add("n", b);
        Node o = n.getActivationVal("o", MathFunctions.TANH);
        // Forward pass: check n and activation o
        float expectedN = x1w1xx2w2.value + b.value; // n = x1*w1 + x2*w2 + b
        assertEquals("n.value should equal x1*w1 + x2*w2 + b", expectedN, n.value, 1e-6f);
        float expectedO = (float) Math.tanh(expectedN); // o = tanh(n)
        assertEquals("o.value should equal tanh(n)", expectedO, o.value, 1e-6f);

        // Automatic backprop: use fillGrad to propagate gradients from o
        o.fillGrad();

        // Validate gradients produced by automatic backprop
        float expectedNGrad2 = 1 - (o.value * o.value); // tanh' at n
        assertEquals("n.grad should be tanh'(n)", expectedNGrad2, n.grad, 1e-6f);
        assertEquals("b.grad should equal n.grad (bias)", expectedNGrad2, b.grad, 1e-6f); // bias receives same gradient
        assertEquals("sum node grad should equal n.grad", expectedNGrad2, x1w1xx2w2.grad, 1e-6f); // add node
        assertEquals("x1*w1 node grad should equal n.grad", expectedNGrad2, x1w1.grad, 1e-6f); // multiplication nodes
        assertEquals("x2*w2 node grad should equal n.grad", expectedNGrad2, x2w2.grad, 1e-6f);
        assertEquals("x1.grad should equal w1 * n.grad", w1.value * expectedNGrad2, x1.grad, 1e-6f);
        assertEquals("w1.grad should equal x1 * n.grad", x1.value * expectedNGrad2, w1.grad, 1e-6f);
        assertEquals("x2.grad should equal w2 * n.grad", w2.value * expectedNGrad2, x2.grad, 1e-6f);
        assertEquals("w2.grad should equal x2 * n.grad", x2.value * expectedNGrad2, w2.grad, 1e-6f);

        System.out.println(DrawGraph.getGraphDisplayString(o));
    }

    @Test
    // TestNN5: composite expression f = (a*b)*(a+b)
    // Verifies forward values and combined gradient contributions to a and b
    public void testNN5() {
        var a = new Node("a", -2);
        var b = new Node("b", 3);
        var d = a.multiply(b);
        var e = a.add(b);
        var f = d.multiply(e);
        // forward checks
        assertEquals("a.value should be -2.0", -2.0f, a.value, 1e-6f);
        assertEquals("b.value should be 3.0", 3.0f, b.value, 1e-6f);
        assertEquals("d.value should be a*b = -6.0", -6.0f, d.value, 1e-6f);
        assertEquals("e.value should be a+b = 1.0", 1.0f, e.value, 1e-6f);
        assertEquals("f.value should be d*e = -6.0", -6.0f, f.value, 1e-6f);

        // backprop and gradient assertions
        f.fillGrad();
        assertEquals("f.grad should be seed 1.0 after fillGrad", 1.0f, f.grad, 1e-6f);
        assertEquals("d.grad should equal df/dd = e = 1.0", 1.0f, d.grad, 1e-6f);
        assertEquals("e.grad should equal df/de = d = -6.0", -6.0f, e.grad, 1e-6f);
        // a.grad = from d (b * d.grad) + from e (1 * e.grad) => b*1 + 1*(-6) = 3 - 6 = -3
        assertEquals("a.grad should equal combined contributions = -3.0", -3.0f, a.grad, 1e-6f);
        // b.grad = from d (a * d.grad) + from e (1 * e.grad) => a*1 + 1*(-6) = -2 - 6 = -8
        assertEquals("b.grad should equal combined contributions = -8.0", -8.0f, b.grad, 1e-6f);
    }

    @Test
    // TestNN6: node used twice as operand (b = a + a) ensures gradient accumulates correctly
    public void testNN6(){
        var a = new Node("a", 3);
        var b = a.add(a);
        b.fillGrad();
        assertEquals("a.value should be 3.0", 3, a.value, 1e-6f);
        assertEquals("b.value should be 6.0", 6, b.value, 1e-6f);

        assertEquals("a.grad should be 2.0 since b = a+a", 2.0f, a.grad, 1e-6f);
        assertEquals("b.grad should be 1.0 (seed)", 1.0f, b.grad, 1e-6f);
    }

    @Test
    public void testNN7() {
        var x1 = new Node("x1", 2);
        var x2 = new Node("x2", 0);
        var w1 = new Node("w1", -3);
        var w2 = new Node("w2", 1);
        var b = new Node("b", 6.8814f);
        var one = new Node("one", 1);
        var x1w1 = x1.multiply(w1);
        var x2w2 = x2.multiply(w2);
        var x1w1xx2w2 = x1w1.add(x2w2);
        var n = x1w1xx2w2.add("n", b);
        var d = n.applyFunction("d", 2, MathFunctions.MUL);
        var e = d.getActivationVal("e", MathFunctions.EXP);
        Node o = (e.applyFunction("e1", 1, MathFunctions.SUB)).divide(e.applyFunction("e2", one, MathFunctions.ADD));

        // Forward pass: check n and activation o
        float expectedN = x1w1xx2w2.value + b.value; // n = x1*w1 + x2*w2 + b
        assertEquals("n.value should equal x1*w1 + x2*w2 + b", expectedN, n.value, 1e-6f);
        float expectedO = (float) Math.tanh(expectedN); // o = tanh(n)
        assertEquals("o.value should equal tanh(n)", expectedO, o.value, 1e-6f);

        // Automatic backprop: use fillGrad to propagate gradients from o
        o.fillGrad();
        System.out.println(DrawGraph.getGraphDisplayString(o));

        // Validate gradients produced by automatic backprop
        float expectedNGrad2 = 1 - (o.value * o.value); // tanh' at n
        assertEquals("n.grad should be tanh'(n)", expectedNGrad2, n.grad, 1e-6f);
        assertEquals("b.grad should equal n.grad (bias)", expectedNGrad2, b.grad, 1e-6f); // bias receives same gradient
        assertEquals("sum node grad should equal n.grad", expectedNGrad2, x1w1xx2w2.grad, 1e-6f); // add node
        assertEquals("x1*w1 node grad should equal n.grad", expectedNGrad2, x1w1.grad, 1e-6f); // multiplication nodes
        assertEquals("x2*w2 node grad should equal n.grad", expectedNGrad2, x2w2.grad, 1e-6f);
        assertEquals("x1.grad should equal w1 * n.grad", w1.value * expectedNGrad2, x1.grad, 1e-6f);
        assertEquals("w1.grad should equal x1 * n.grad", x1.value * expectedNGrad2, w1.grad, 1e-6f);
        assertEquals("x2.grad should equal w2 * n.grad", w2.value * expectedNGrad2, x2.grad, 1e-6f);
        assertEquals("w2.grad should equal x2 * n.grad", x2.value * expectedNGrad2, w2.grad, 1e-6f);
    }
}