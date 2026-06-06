package org.kp.ai.micrograd.functions.registry;

import org.kp.ai.micrograd.functions.api.ErrorFunction;
import org.kp.ai.micrograd.functions.impl.MSEErrorFunction;

/**
 * Holder for error functions (backwards compatible with previous enum usage).
 */
public final class ErrorMathFunctions {
    public static final ErrorFunction MSE = new MSEErrorFunction();

    private ErrorMathFunctions() {
    }
}
