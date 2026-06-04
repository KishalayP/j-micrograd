package org.micrograd.core;

import junit.framework.TestCase;
import org.micrograd.functions.MathFunctions;
import org.micrograd.functions.Utils;
import org.micrograd.util.DrawGraph;
import org.micrograd.util.GraphVizDraw;

import java.util.List;

public class MLPTest extends TestCase {

    public void testActivateMLP() {
        List<Node> xList = Utils.createListOfNodes("mlp", "x", List.of(2.0F, 3.0F, -1F));
        var mlp = new MLP("mlp", 3, List.of(4, 4, 1));
        List<List<Node>> activatedLayer = mlp.activateMLP(xList, MathFunctions.TANH);
//        System.out.println(mlp);
        for (List<Node> nodes : activatedLayer) {
            System.out.println(nodes);
        }

        Node resultNode = activatedLayer.getLast().getFirst();
        System.out.println(DrawGraph.getGraphDisplayString(resultNode));
        System.out.println(GraphVizDraw.getGraphDisplayString(resultNode));
    }
}