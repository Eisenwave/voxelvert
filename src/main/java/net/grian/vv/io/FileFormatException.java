package net.grian.vv.io;

/**
 * Thrown when a file of wrong format is being read or written.
 */
public class FileFormatException extends ParseException {

    public FileFormatException() {
        super();
    }

    public FileFormatException(String message) {
        super(message);
    }

    public FileFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileFormatException(Throwable cause) {
        super(cause);
    }

}
