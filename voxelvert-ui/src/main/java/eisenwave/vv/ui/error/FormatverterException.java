package eisenwave.vv.ui.error;

public class FormatverterException extends RuntimeException {
    
    public FormatverterException() {
        super();
    }
    
    public FormatverterException(String message) {
        super(message);
    }
    
    public FormatverterException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public FormatverterException(Throwable cause) {
        super(cause);
    }
    
}
