package org.micrograd.core;

import junit.framework.TestCase;
import org.micrograd.functions.MathFunctions;
import org.micrograd.functions.Utils;
import org.micrograd.util.DrawGraph;

import java.util.List;

public class NeuronTest extends TestCase {

    public void testGetSumOfSynapses() {
        List<Node> xList = Utils.createListOfNodes("mlp", "x", List.of(2.0F, 3.0F));
        var neuron = new Neuron("n1", 2);
        Node sumOfSynapses = neuron.activateNeuron(xList, MathFunctions.TANH);
        System.out.println(neuron);
        System.out.println(sumOfSynapses);
        System.out.println(DrawGraph.getGraphDisplayString(sumOfSynapses));
    }
}