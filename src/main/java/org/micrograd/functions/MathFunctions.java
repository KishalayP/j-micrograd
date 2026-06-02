package org.micrograd.functions;

import org.micrograd.core.Node;

import java.util.List;

public enum MathFunctions {
    ADD {
        @Override
        public String getRepresentation(String x) {
            return null;
        }

        @Override
        public String getRepresentation(String x, String y) {
            return "(%s + %s)".formatted(x, y);
        }

        @Override
        public float applyFunction(float x) {
            return 0;
        }

        @Override
        public float applyFunction(float x, float y) {
            return x + y;
        }

        @Override
        public void backFillGradVal(Node resultNode, List<Node> operandNodes) {
            if (operandNodes != null && !operandNodes.isEmpty()) {
                Node firstOperNode = operandNodes.getFirst();
                var secondOperNode = operandNodes.get(1);
                firstOperNode.grad += resultNode.grad;
                secondOperNode.grad += resultNode.grad;
            }
        }

        @Override
        public String toString() {
            return "+";
        }
    },

    MUL {
        @Override
        public String getRepresentation(String x) {
            return null;
        }

        @Override
        public String getRepresentation(String x, String y) {
            return "(%s * %s)".formatted(x, y);
        }

        @Override
        public float applyFunction(float x) {
            return 0;
        }

        @Override
        public float applyFunction(float x, float y) {
            return x * y;
        }

        @Override
        public void backFillGradVal(Node resultNode, List<Node> operandNodes) {
            if (operandNodes != null && !operandNodes.isEmpty()) {
                Node firstOperNode = operandNodes.getFirst();
                var secondOperNode = operandNodes.get(1);
                firstOperNode.grad += secondOperNode.value * resultNode.grad;
                secondOperNode.grad += firstOperNode.value * resultNode.grad;
            }
        }

        @Override
        public String toString() {
            return "*";
        }
    },

    TANH {
        @Override
        public String getRepresentation(String x) {
            return "tanh(%s)".formatted(x);
        }

        @Override
        public String getRepresentation(String x, String y) {
            return null;
        }

        @Override
        public float applyFunction(float x) {
            double exp = Math.exp(2 * x);
            return (float) ((exp - 1) / (exp + 1));
        }

        @Override
        public float applyFunction(float x, float y) {
            return 0;
        }

        @Override
        public void backFillGradVal(Node resultNode, List<Node> operandNodes) {
            if (operandNodes != null && !operandNodes.isEmpty()) {
                operandNodes.getFirst().grad += 1 - (resultNode.value * resultNode.value);
            }
        }

        @Override
        public String toString() {
            return "tanh";
        }
    };

    public abstract String getRepresentation(String x);

    public abstract String getRepresentation(String x, String y);

    public abstract float applyFunction(float x);

    public abstract float applyFunction(float x, float y);

    public abstract void backFillGradVal(Node resultNode, List<Node> operandNodes);
}
