package net.grian.vv.io;

import java.io.IOException;

/**
 * Thrown when parsing a particular file version or style is not supported by parser.
 */
public class FileVersionException extends IOException {

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
