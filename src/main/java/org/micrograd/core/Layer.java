package org.micrograd.core;

import org.micrograd.functions.MathFunctions;
import org.micrograd.functions.Utils;

import java.util.ArrayList;
import java.util.List;

public class Layer {

    public String name;
    public List<Neuron> neurons;

    public Layer(String name, int neuronNumOfInputs, int numOfNeurons) {
        this.name = name;
        neurons = new ArrayList<>();
        createNeurons(neuronNumOfInputs, numOfNeurons);
    }

    public List<Node> activateLayer(List<Node> input, MathFunctions activationFunction) {
        var res = new ArrayList<Node>();
        for (Neuron neuron : neurons) {
            res.add(neuron.activateNeuron(input, activationFunction));
        }
        return res;
    }

    private void createNeurons(int neuronNumOfInputs, int numOfNeurons) {
        for (int i = 0; i < numOfNeurons; i++) {
            String neuronName = Utils.createNodeName(name, "n", i);
            neurons.add(new Neuron(neuronName, neuronNumOfInputs));
        }
    }

    @Override
    public String toString() {
        return "Layer{name='%s', \nneurons=%s \n}".formatted(name, neurons);
    }
}
