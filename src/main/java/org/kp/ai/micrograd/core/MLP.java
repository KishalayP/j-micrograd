package org.kp.ai.micrograd.core;

import org.kp.ai.micrograd.functions.api.ErrorFunction;
import org.kp.ai.micrograd.functions.api.MathFunction;
import org.kp.ai.micrograd.model.MLPTrainingResult;
import org.kp.ai.micrograd.util.Utils;

import java.util.ArrayList;
import java.util.List;

import static org.kp.ai.micrograd.util.Utils.createNodeName;

public class MLP {

    public String name;
    public List<Layer> layers;

    private MLP() {
        //To prevent empty initialization
    }

    public MLP(String name, int numOfInputs, List<Integer> numNeuronsInLayers) {
        this.name = name;
        createLayers(numOfInputs, numNeuronsInLayers);
    }

    public MLPTrainingResult train(int epoch, float stepSize, List<List<Float>> inputList, List<List<Float>> expectedOutput,
                                   MathFunction activationFunction, ErrorFunction lossFunction) {
        MLPTrainingResult result = null;
        for (int i = 0; i < epoch; i++) {
            //Forward Pass
            result = trainAndGetLoss(inputList, expectedOutput, activationFunction, lossFunction);
            //Backward Pass
            result.loss.fillGrad();
            //Re-training/Update
            tuneMLP(stepSize);
        }
        return result;
    }

    private void createLayers(int numOfInputs, List<Integer> numNeuronsInLayers) {
        this.layers = new ArrayList<>();
        int inputs = numOfInputs;
        for (int i = 0; i < numNeuronsInLayers.size(); i++) {
            layers.add(new Layer(createNodeName(name, "l", i), inputs, numNeuronsInLayers.get(i)));
            // next layer's inputs == this layer's number of neurons
            inputs = numNeuronsInLayers.get(i);
        }
    }

    private List<Node> getParameters() {
        var res = new ArrayList<Node>();
        for (Layer layer : layers) {
            res.addAll(layer.getParameters());
        }
        return res;
    }

    private MLPTrainingResult trainAndGetLoss(List<List<Float>> inputList, List<List<Float>> expectedOutputs,
                                              MathFunction activationFunction, ErrorFunction lossFunction) {
        if (inputList.size() != expectedOutputs.size()) {
            throw new IllegalArgumentException("Number of inputs and expected outputs must match");
        }

        Node loss = new Node("l", 0);
        MLPTrainingResult result = new MLPTrainingResult(createNodeName(name, "res"));

        for (int i = 0; i < inputList.size(); i++) {
            List<Float> input = inputList.get(i);
            if (layers.getFirst().neurons.getFirst().weights.size() != input.size()) {
                throw new IllegalArgumentException("Number of MLP Inputs and provided inputs must match");
            }

            List<Float> expectedVec = expectedOutputs.get(i);
            if (layers.getLast().neurons.size() != expectedVec.size()) {
                throw new IllegalArgumentException("Number of MLP Outputs and expected outputs must match");
            }

            List<Node> preds = getResult(input, activationFunction); // now a List<Node>
            if (preds.size() != expectedVec.size()) {
                throw new IllegalArgumentException("Output size mismatch for sample " + i);
            }

            for (int j = 0; j < preds.size(); j++) {
                Node target = new Node(Utils.createNodeName(name, "yout" + (i + 1) + "_", j), expectedVec.get(j));
                Node pred = preds.get(j);
                pred.name = Utils.createNodeName(name, "ygt" + (i + 1) + "_", j);
                loss = lossFunction.getError(loss, target, pred);
            }
            result.addPrediction(new ArrayList<>(preds));
        }

        result.loss = loss;
        return result;
    }

    private List<Node> getResult(List<Float> input, MathFunction activationFunction) {
        List<Node> xList = Utils.createListOfNodes("mlp", "x", input);
        List<List<Node>> activatedLayers = stimulateMLP(xList, activationFunction);

        if (activatedLayers.isEmpty()) {
            throw new IllegalStateException("No layers produced by forward pass");
        }
        List<Node> outputLayer = activatedLayers.getLast();
        if (outputLayer == null || outputLayer.isEmpty()) {
            throw new IllegalStateException("Last layer has no neurons");
        }
        return outputLayer;
    }

    private void tuneMLP(float stepSize) {
        for (Node node : getParameters()) {
            node.value -= stepSize * node.grad;
        }
    }

    private List<List<Node>> stimulateMLP(List<Node> input, MathFunction activationFunction) {
        var res = new ArrayList<List<Node>>();
        List<Node> currentInput = input;
        for (Layer layer : layers) {
            List<Node> outNodes = layer.stimulateLayer(currentInput, activationFunction);
            res.add(outNodes);
            // prepare inputs for next layer (use node values)
            currentInput = outNodes;
        }
        return res;
    }

    @Override
    public String toString() {
        return "MLP{name='%s', \nlayers=%s \n}".formatted(name, layers);
    }
}
