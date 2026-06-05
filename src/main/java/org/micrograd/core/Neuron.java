package org.micrograd.core;

import org.micrograd.functions.api.MathFunction;
import org.micrograd.util.Utils;

import java.util.ArrayList;
import java.util.List;

import static org.micrograd.util.Utils.createNodeName;

public class Neuron {

    public String name;
    public Node bias;
    public List<Node> weights;

    public Neuron(String name, int numOfInputs) {
        this.name = name;
        this.weights = getRandomWeights(numOfInputs);
        this.bias = new Node(createNodeName(this.name, "b"), Utils.randomFloatInRange(-1, 1));
    }

    public Node stimulateNeuron(List<Node> input, MathFunction mathFunctions) {
        return getSumOfSynapsesNodes(input).getActivationVal(createNodeName(name, "r"), mathFunctions);
    }

    public List<Node> getParameters() {
        var res = new ArrayList<Node>(weights);
        res.add(bias);
        return res;
    }

    private Node getSumOfSynapsesNodes(List<Node> input) {
        String sumNodeName = createNodeName(name, "s");
        var res = new Node(sumNodeName, 0);
        var c = 0;
        for (Node x : input) {
            Node weight = weights.get(c);
            Node sum = weight.multiply(x);
            res = res.add(sum);
            c++;
        }
        res = res.add(bias);
        return res;
    }

    private List<Node> getRandomWeights(int numOfInputs) {
        var result = new ArrayList<Node>();
        for (int i = 0; i < numOfInputs; i++) {
            String weightName = createNodeName(name, "w", i);
            result.add(new Node(weightName, Utils.randomFloatInRange(-1, 1)));
        }
        return result;
    }

    @Override
    public String toString() {
        return "\nNeuron{name='%s', bias=%s, weights=%s}".formatted(name, bias, weights);
    }
}
