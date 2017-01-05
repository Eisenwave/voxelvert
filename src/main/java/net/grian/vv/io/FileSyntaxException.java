package net.grian.vv.io;

/**
 * Thrown when a file can not be parsed due to invalid syntax.
 */
public class FileSyntaxException extends ParseException {

    public FileSyntaxException() {
        super();
    }

    public FileSyntaxException(String message) {
        super(message);
    }

    public FileSyntaxException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileSyntaxException(Throwable cause) {
        super(cause);
    }

}
