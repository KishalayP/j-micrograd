package org.micrograd.core;

import org.micrograd.functions.MathFunctions;

import java.util.ArrayList;
import java.util.List;

import static org.micrograd.functions.Utils.createNodeName;

public class MLP {

    public String name;
    public List<Layer> layers;

    public MLP(String name, int numOfInputs, List<Integer> numNeuronsInLayers) {
        this.name = name;
        createLayers(numOfInputs, numNeuronsInLayers);
    }

    public List<List<Node>> activateMLP(List<Node> input, MathFunctions activationFunction) {
        var res = new ArrayList<List<Node>>();
        List<Node> currentInput = input;
        for (Layer layer : layers) {
            List<Node> outNodes = layer.activateLayer(currentInput, activationFunction);
            res.add(outNodes);
            // prepare inputs for next layer (use node values)
            currentInput = outNodes;
        }
        return res;
    }

    private void createLayers(int numOfInputs, List<Integer> numNeuronsInLayers) {
        this.layers = new ArrayList<>();
        int inputs = numOfInputs;
        for (int i = 0; i < numNeuronsInLayers.size(); i++) {
            layers.add(new Layer(createNodeName("", "l", i), inputs, numNeuronsInLayers.get(i)));
            // next layer's inputs == this layer's number of neurons
            inputs = numNeuronsInLayers.get(i);
        }
    }

    @Override
    public String toString() {
        return "MLP{name='%s', \nlayers=%s \n}".formatted(name, layers);
    }
}
