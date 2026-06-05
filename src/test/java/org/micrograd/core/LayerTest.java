package org.micrograd.core;

import junit.framework.TestCase;
import org.micrograd.functions.MathFunctions;
import org.micrograd.functions.Utils;

import java.util.List;

public class LayerTest extends TestCase {

    public void testLayerTest() {
        List<Node> xList = Utils.createListOfNodes("l1", "x", List.of(2.0F, 3.0F));
        var l1 = new Layer("l1", 2, 3);
        List<Node> activatedLayer = l1.stimulateLayer(xList, MathFunctions.TANH);
        // basic assertions: correct number of outputs and values in tanh range
        assertEquals(3, activatedLayer.size());
        for (Node node : activatedLayer) {
            assertNotNull(node);
            assertTrue(Float.isFinite(node.value));
            // tanh outputs are in (-1, 1)
            assertTrue(Math.abs(node.value) <= 1.0f + 1e-6f);
        }
    }

}