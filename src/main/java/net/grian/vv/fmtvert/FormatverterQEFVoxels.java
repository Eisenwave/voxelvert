package net.grian.vv.fmtvert;

import net.grian.spatium.voxel.VoxelArray;
import net.grian.torrens.qbcl.DeserializerQEF;
import net.grian.vv.plugin.VVUser;

import java.io.File;

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
    public void convert(VVUser user, String from, String to, Object... args) throws Exception {
        File dir = user.getFileDirectory();
        File qef = new File(dir, from);
        VoxelArray voxels = new DeserializerQEF().fromFile(qef);
        
        user.putData(to, voxels);
    }
    
}
