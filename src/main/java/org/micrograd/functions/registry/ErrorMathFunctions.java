package org.micrograd.functions.registry;

import org.micrograd.functions.api.ErrorFunction;
import org.micrograd.functions.impl.MSEErrorFunction;

/**
 * Holder for error functions (backwards compatible with previous enum usage).
 */
public final class ErrorMathFunctions {
    public static final ErrorFunction MSE = new MSEErrorFunction();

    private ErrorMathFunctions() {
    }
}
