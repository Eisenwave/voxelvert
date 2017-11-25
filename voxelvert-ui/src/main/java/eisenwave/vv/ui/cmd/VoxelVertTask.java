package eisenwave.vv.ui.cmd;

import eisenwave.vv.ui.fmtvert.*;
import eisenwave.vv.ui.user.VVUser;


public abstract class VoxelVertTask extends Progress {
    
    protected final VVUser user;
    protected final Format sourceFormat, targetFormat;
    protected final String source, target;
    
    public VoxelVertTask(VVUser user, Format sourceFormat, String source, Format targetFormat, String target) {
        this.user = user;
        this.sourceFormat = sourceFormat;
        this.source = source;
        this.targetFormat = targetFormat;
        this.target = target;
    }
    
    public abstract void run() throws Exception;
    
    public VVUser getUser() {
        return user;
    }
    
    public Format getInputFormat() {
        return sourceFormat;
    }
    
    public Format getOutputFormat() {
        return targetFormat;
    }
    
    public String getInput() {
        return source;
    }
    
    public String getOutput() {
        return target;
    }
    
    @Override
    public String toString() {
        return "{" + user.getName() + ": " +
            String.format("%s<%s> -> %s<%s>", sourceFormat, source, targetFormat, target) + "}";
    }
    
}
