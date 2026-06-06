package org.kp.ai.micrograd.functions.registry;

import org.kp.ai.micrograd.functions.api.MathFunction;
import org.kp.ai.micrograd.functions.impl.*;

/**
 * Holder for concrete MathFunction instances to preserve the old usage style
 * (e.g. MathFunctions.TANH) while the implementations are provided by
 * separate classes implementing {@link MathFunction}.
 */
public final class MathFunctions {
    public static final MathFunction ADD = new AddFunction();
    public static final MathFunction SUB = new SubFunction();
    public static final MathFunction MUL = new MulFunction();
    public static final MathFunction DIV = new DivFunction();
    public static final MathFunction POW = new PowFunction();
    public static final MathFunction EXP = new ExpFunction();
    public static final MathFunction TANH = new TanhFunction();

    private MathFunctions() { /* no instances */ }
}
