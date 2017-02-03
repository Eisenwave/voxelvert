package net.grian.vv.fmtvert;

import net.grian.spatium.voxel.VoxelArray;
import net.grian.torrens.qbcl.DeserializerQB;
import net.grian.torrens.qbcl.QBModel;
import net.grian.vv.core.VoxelMesh;
import net.grian.vv.plugin.VVUser;
import net.grian.vv.util.ConvertUtil;

import java.io.File;
import java.io.IOException;

public class FormatverterQBVoxels implements Formatverter {
    
    @Override
    public Format getFrom() {
        return Format.QB;
    }
    
    @Override
    public Format getTo() {
        return Format.VOXELS;
    }
    
    @Override
    public void convert(VVUser user, String from, String to, Object... args) throws Exception {
        File file = user.getFile(from);
        if (!file.exists() || !file.isFile()) throw new IOException("missing or not a file: "+from);
        
        QBModel qb = new DeserializerQB().fromFile(file);
        VoxelMesh mesh = ConvertUtil.convert(qb, VoxelMesh.class);
        VoxelArray array = ConvertUtil.convert(mesh, VoxelArray.class);
        
        user.putData(to, array);
    }
    
}
