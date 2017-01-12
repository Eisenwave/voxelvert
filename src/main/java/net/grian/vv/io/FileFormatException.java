package net.grian.vv.io;

import java.io.IOException;

/**
 * Thrown when a file of wrong format is being read or written.
 */
public class FileFormatException extends IOException {

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
