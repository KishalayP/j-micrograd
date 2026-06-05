package org.micrograd.core;

import junit.framework.TestCase;
import org.micrograd.functions.ErrorMathFunctions;
import org.micrograd.functions.MathFunctions;

import java.util.ArrayList;
import java.util.List;

public class MLPTest extends TestCase {

    public void testTrainMLP1() {
        var inputList = List.of(List.of(2.0F, 3.0F, -1F),
                List.of(3.0F, -1.0F, -0.5F),
                List.of(0.5F, 1.0F, 1.0F),
                List.of(1.0F, 1.0F, -1.0F));
        var expectedOutput = List.of(List.of(1F), List.of(-1F), List.of(-1F), List.of(1F));
        var mlp = new MLP("mlp", 3, List.of(4, 4, 1));
        MLPTrainingResult mlpTrainingResult = mlp.train(100, 0.1F, inputList, expectedOutput,
                MathFunctions.TANH, ErrorMathFunctions.MSE);
        Node loss = mlpTrainingResult.loss;
        System.out.println("Neural Network Equation: " + loss.name);
        loss.name = "l";
        System.out.println(loss);
        System.out.println(mlpTrainingResult.predictions);

        // assertions
        assertNotNull(mlpTrainingResult);
        assertNotNull(mlpTrainingResult.loss);
        // loss should be a finite number
        assertTrue(Float.isFinite(mlpTrainingResult.loss.value));
        // predictions: one vector per input sample
        assertEquals(inputList.size(), mlpTrainingResult.predictions.size());
        for (int i = 0; i < mlpTrainingResult.predictions.size(); i++) {
            var predVec = mlpTrainingResult.predictions.get(i);
            var expectedVec = expectedOutput.get(i);
            assertEquals(expectedVec.size(), predVec.size());
            for (Node out : predVec) {
                assertNotNull(out);
                assertTrue(Float.isFinite(out.value));
                // tanh activation keeps outputs in (-1,1)
                assertTrue(Math.abs(out.value) <= 1.0f + 1e-6f);
            }
        }
    }

    public void testGradientAndParameterUpdate() {
        // Simple dataset: 4 samples, 3 inputs each, scalar targets
        var inputList = List.of(
                List.of(2.0F, 3.0F, -1F),
                List.of(3.0F, -1.0F, -0.5F),
                List.of(0.5F, 1.0F, 1.0F),
                List.of(1.0F, 1.0F, -1.0F)
        );
        var expectedOutput = List.of(List.of(1F), List.of(-1F), List.of(-1F), List.of(1F));

        var mlp = new MLP("mlp", 3, List.of(4, 4, 1));

        // collect parameter values before training
        List<Node> paramsBeforeNodes = new ArrayList<>();
        for (Layer layer : mlp.layers) {
            paramsBeforeNodes.addAll(layer.getParameters());
        }
        float[] beforeVals = new float[paramsBeforeNodes.size()];
        for (int i = 0; i < paramsBeforeNodes.size(); i++) {
            beforeVals[i] = paramsBeforeNodes.get(i).value;
        }

        // perform a single training epoch (one weight update)
        MLPTrainingResult result = mlp.train(1, 0.01F, inputList, expectedOutput, MathFunctions.TANH, ErrorMathFunctions.MSE);

        // collect parameter values after training
        List<Node> paramsAfterNodes = new ArrayList<>();
        for (Layer layer : mlp.layers) {
            paramsAfterNodes.addAll(layer.getParameters());
        }
        float[] afterVals = new float[paramsAfterNodes.size()];
        for (int i = 0; i < paramsAfterNodes.size(); i++) {
            afterVals[i] = paramsAfterNodes.get(i).value;
        }

        // assertion: sizes must match
        assertEquals("Parameter count should remain the same after training", beforeVals.length, afterVals.length);

        // check that at least one parameter changed value (indicates update occurred)
        boolean anyChanged = false;
        for (int i = 0; i < beforeVals.length; i++) {
            if (Math.abs(beforeVals[i] - afterVals[i]) > 1e-9f) {
                anyChanged = true;
                break;
            }
        }
        assertTrue("Expected at least one parameter to change after one training step", anyChanged);

        // check that gradients were computed (some parameter grad should be non-zero and finite)
        boolean anyNonZeroGrad = false;
        for (Node p : paramsAfterNodes) {
            if (Float.isFinite(p.grad) && Math.abs(p.grad) > 1e-9f) {
                anyNonZeroGrad = true;
                break;
            }
        }
        assertTrue("Expected at least one parameter to have a non-zero gradient after backprop", anyNonZeroGrad);

        // Ensure returned training result contains a loss node and predictions in expected shape
        assertNotNull("Training result should not be null", result);
        assertNotNull("Training result must contain a loss node", result.loss);
        assertEquals("Predictions size should equal number of input samples", inputList.size(), result.predictions.size());
    }
}