package org.micrograd.functions.impl;

import org.micrograd.core.Node;
import org.micrograd.functions.api.AbstractMathFunction;

import java.util.List;

public class PowFunction extends AbstractMathFunction {
    @Override
    public String getRepresentation(String x, String y) {
        return "(%s^%s)".formatted(x, y);
    }

    @Override
    public float applyFunction(float x, float y) {
        return (float) Math.pow(x, y);
    }

    @Override
    public void backFillGradVal(Node resultNode, List<Node> operandNodes) {
        if (operandNodes != null && operandNodes.size() >= 2) {
            var baseNode = operandNodes.get(0);
            var powerNode = operandNodes.get(1);
            var base = baseNode.value;
            var power = powerNode.value;
            var value = resultNode.value; // x^y
            // gradient wrt base: y * x^(y-1)
            var gradBase = power * Math.pow(base, power - 1);
            baseNode.grad += (float) (resultNode.grad * gradBase);
            // gradient wrt exponent: x^y * ln(x)  (only valid for x>0)
            if (base > 0) {
                powerNode.grad += resultNode.grad * (float) (value * Math.log(base));
            }
        }
    }

    @Override
    public String toString() {
        return "pow";
    }
}

