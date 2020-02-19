package de.rngcntr.janusbench.exceptions;

public class UnavailableBenchmarkException extends RuntimeException {

    private static final long serialVersionUID = 1822446746749214948L;

    public UnavailableBenchmarkException() { super(); }

    public UnavailableBenchmarkException(String message) { super(message); }

    public UnavailableBenchmarkException(String message, Throwable cause) { super(message, cause); }

    public UnavailableBenchmarkException(String message, Throwable cause, boolean enableSuppression,
                                         boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public UnavailableBenchmarkException(Throwable cause) { super(cause); }
}