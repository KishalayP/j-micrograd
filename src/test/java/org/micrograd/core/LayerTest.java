package org.micrograd.core;

import junit.framework.TestCase;
import org.micrograd.functions.MathFunctions;
import org.micrograd.functions.Utils;

import java.util.List;

public class LayerTest extends TestCase {

    public void testLayerTest() {
        List<Node> xList = Utils.createListOfNodes("l1", "x", List.of(2.0F, 3.0F));
        var l1 = new Layer("l1", 2, 3);
        List<Node> activatedLayer = l1.activateLayer(xList, MathFunctions.TANH);
        System.out.println(activatedLayer);
    }

}