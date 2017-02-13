package net.grian.vv.fmtvert;

import net.grian.spatium.voxel.VoxelArray;
import net.grian.torrens.qbcl.DeserializerQEF;
import net.grian.vv.arg.Argument;
import net.grian.vv.plugin.VVUser;

import java.io.File;
import java.io.IOException;

public class FormatverterQEFVoxels implements Formatverter {
    
    @Override
    public Format getFrom() {
        return Format.QEF;
    }
    
    @Override
    public Format getTo() {
        return Format.VOXELS;
    }
    
    @Override
    public void convert(VVUser user, String from, String to, Argument... args) throws Exception {
        File file = user.getFile(from);
        if (!file.exists() || !file.isFile()) throw new IOException("missing or not a file: "+from);
        
        VoxelArray voxels = new DeserializerQEF().fromFile(file);
        
        user.putData(to, voxels);
    }
    
}
