package net.grian.vv.io;

import java.io.IOException;

/**
 * Thrown when a file can not be parsed due to invalid syntax.
 */
public class FileSyntaxException extends IOException {

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
