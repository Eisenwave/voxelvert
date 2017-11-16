package org.eisenwave.vv.ui.cmd;

/**
 * A known exception which happens when executing a voxelvert command.
 */
public class VVInitializerException extends Exception {
    
    public VVInitializerException() {
        super();
    }
    
    public VVInitializerException(String message) {
        super(message);
    }
    
    public VVInitializerException(String message, Object... args) {
        super(String.format(message, args));
    }
    
    public VVInitializerException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public VVInitializerException(String message, Throwable cause, Object... args) {
        super(String.format(message, args), cause);
    }
    
    public VVInitializerException(Throwable cause) {
        super(cause);
    }
    
}
