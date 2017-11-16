package org.eisenwave.vv.ui.cmd;

import org.eisenwave.vv.ui.fmtvert.Format;
import org.eisenwave.vv.ui.fmtvert.Formatverter;
import org.eisenwave.vv.ui.fmtvert.Progress;
import org.eisenwave.vv.ui.fmtvert.ProgressListener;
import org.eisenwave.vv.ui.user.VVUser;

import java.util.Collection;
import java.util.HashSet;

public abstract class VoxelVertTask extends Progress implements Runnable {
    
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
    
}
