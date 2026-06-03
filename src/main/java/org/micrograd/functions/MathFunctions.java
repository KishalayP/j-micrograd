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

    SUB {
        @Override
        public String getRepresentation(String x) {
            return "";
        }

        @Override
        public String getRepresentation(String x, String y) {
            return "(%s - %s)".formatted(x, y);
        }

        @Override
        public float applyFunction(float x) {
            return 0;
        }

        @Override
        public float applyFunction(float x, float y) {
            return x - y;
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
            return "-";
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
                firstOperNode.grad += resultNode.grad * secondOperNode.value;
                secondOperNode.grad += resultNode.grad * firstOperNode.value;
            }
        }

        @Override
        public String toString() {
            return "*";
        }
    },

    DIV {
        @Override
        public String getRepresentation(String x) {
            return null;
        }

        @Override
        public String getRepresentation(String x, String y) {
            return "(%s / %s)".formatted(x, y);
        }

        @Override
        public float applyFunction(float x) {
            return 0;
        }

        @Override
        public float applyFunction(float x, float y) {
            return x / y;
        }

        @Override
        public void backFillGradVal(Node resultNode, List<Node> operandNodes) {
            if (operandNodes != null && !operandNodes.isEmpty()) {
                Node firstOperNode = operandNodes.getFirst();
                var secondOperNode = operandNodes.get(1);
                // grad = derivative result which is backfilled from operations done using result * local derivative of operation used to get result
                firstOperNode.grad += resultNode.grad * (1 / secondOperNode.value);
                secondOperNode.grad += resultNode.grad * (1 / firstOperNode.value);
            }
        }

        @Override
        public String toString() {
            return "/";
        }
    },

    POW {
        @Override
        public String getRepresentation(String x) {
            return "";
        }

        @Override
        public String getRepresentation(String x, String y) {
            return "(%s^%s)".formatted(x, y);
        }

        @Override
        public float applyFunction(float x) {
            return 0;
        }

        @Override
        public float applyFunction(float x, float y) {
            return (float) Math.pow(x, y);
        }

        @Override
        public void backFillGradVal(Node resultNode, List<Node> operandNodes) {
            if (operandNodes != null && !operandNodes.isEmpty()) {
                var base = operandNodes.getFirst().value;
                var power = operandNodes.get(1).value;
                // f(x) = x^y, then diff is f'(x) = y * (x*(y-1))
                var gradOfResultFunction = power * Math.pow(base, power - 1);
                operandNodes.getFirst().grad += (float) (resultNode.grad * gradOfResultFunction);
            }
        }

        @Override
        public String toString() {
            return "pow";
        }
    },

    EXP {
        @Override
        public String getRepresentation(String x) {
            return "(exp^%s)".formatted(x);
        }

        @Override
        public String getRepresentation(String x, String y) {
            return "";
        }

        @Override
        public float applyFunction(float x) {
            return (float) Math.exp(x);
        }

        @Override
        public float applyFunction(float x, float y) {
            return 0;
        }

        @Override
        public void backFillGradVal(Node resultNode, List<Node> operandNodes) {
            if (operandNodes != null && !operandNodes.isEmpty()) {
                operandNodes.getFirst().grad += resultNode.grad * resultNode.value;
            }
        }

        @Override
        public String toString() {
            return "exp";
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
                var gradOfResultFunction = 1 - (resultNode.value * resultNode.value);
                operandNodes.getFirst().grad += resultNode.grad * gradOfResultFunction;
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
