package net.grian.vv.convert;

/**
 * Thrown when a conversion fails due to an invalid path specification.
 */
public class ConversionPathException extends RuntimeException {

    public ConversionPathException() {
        super();
    }

    public ConversionPathException(String message) {
        super(message);
    }

}
