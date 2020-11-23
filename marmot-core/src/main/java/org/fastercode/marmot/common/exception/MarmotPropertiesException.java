package org.fastercode.marmot.common.exception;

public class MarmotPropertiesException extends RuntimeException {
    private static final long serialVersionUID = -5911461115434684353L;

    /**
     * Constructs an exception with formatted error message and arguments.
     *
     * @param errorMessage formatted error message
     * @param args         arguments of error message
     */
    public MarmotPropertiesException(final String errorMessage, final Object... args) {
        super(String.format(errorMessage, args));
    }

    /**
     * Constructs an exception with cause exception.
     *
     * @param cause cause exception
     */
    public MarmotPropertiesException(final Exception cause) {
        super(cause);
    }
}
