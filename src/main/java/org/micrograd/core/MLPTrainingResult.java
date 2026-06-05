package org.micrograd.core;

import java.util.ArrayList;
import java.util.List;

public class MLPTrainingResult {

    String name;
    Node loss;
    // now each element is the output vector (List<Node>) for one sample
    List<List<Node>> predictions;

    public MLPTrainingResult(String name) {
        this.name = name;
        this.predictions = new ArrayList<>();
    }

    // add a full prediction vector for one sample
    public void addPrediction(List<Node> toAdd) {
        predictions.add(toAdd);
    }
}