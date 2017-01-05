package net.grian.vv.io;

/**
 * Thrown when parsing a particular file version or style is not supported by parser.
 */
public class FileVersionException extends ParseException {

    public FileVersionException() {
        super();
    }

    public FileVersionException(String message) {
        super(message);
    }

    public FileVersionException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileVersionException(Throwable cause) {
        super(cause);
    }

}
