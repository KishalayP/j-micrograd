package org.micrograd.core;

import junit.framework.TestCase;
import org.micrograd.functions.registry.MathFunctions;
import org.micrograd.util.DrawGraph;
import org.micrograd.util.Utils;

import java.util.List;

public class NeuronTest extends TestCase {

    public void testGetSumOfSynapses() {
        List<Node> xList = Utils.createListOfNodes("mlp", "x", List.of(2.0F, 3.0F));
        var neuron = new Neuron("n1", 2);
        Node sumOfSynapses = neuron.stimulateNeuron(xList, MathFunctions.TANH);
        System.out.println(neuron);
        System.out.println(sumOfSynapses);
        System.out.println(DrawGraph.getGraphDisplayString(sumOfSynapses));

        // assertions
        assertNotNull(sumOfSynapses);
        assertTrue(Float.isFinite(sumOfSynapses.value));
        // tanh activation should keep output in (-1,1)
        assertTrue(Math.abs(sumOfSynapses.value) <= 1.0f + 1e-6f);
    }
}