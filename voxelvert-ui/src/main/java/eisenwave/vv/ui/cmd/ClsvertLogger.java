package eisenwave.vv.ui.cmd;

import eisenwave.vv.ui.user.VVUser;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class ClsvertLogger extends Handler {
    
    private final static String
    FORMAT = "    %s: %s";
    
    private final VVUser user;
    
    public ClsvertLogger(VVUser user) {
        this.user = user;
    }
    
    @Override
    public void publish(LogRecord record) {
        if (record.getLevel().intValue() >= Level.FINE.intValue()) {
            String cls = record.getSourceClassName();
            cls = cls.substring(cls.lastIndexOf('.')+1);
            
            user.print(FORMAT, cls, record.getMessage());
        }
    }
    
    @Override
    public void flush() {
    
    }
    
    @Override
    public void close() throws SecurityException {
    
    }
    
}
