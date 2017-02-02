package net.grian.vv.fmtvert;

import net.grian.spatium.voxel.BlockArray;
import net.grian.spatium.voxel.VoxelArray;
import net.grian.torrens.util.Resources;
import net.grian.vv.cache.ColorMap;
import net.grian.vv.core.BlockSet;
import net.grian.vv.io.ExtractableColor;
import net.grian.vv.plugin.VVPlugin;
import net.grian.vv.plugin.VVUser;
import net.grian.vv.util.ConvertUtil;

import java.util.zip.ZipFile;

public class FormatverterBlocksVoxels implements Formatverter {
    
    @Override
    public Format getFrom() {
        return Format.BLOCKS;
    }
    
    @Override
    public Format getTo() {
        return Format.VOXELS;
    }
    
    @Override
    public void convert(VVUser user, String from, String to, Object... args) throws Exception {
        BlockSet selection = user.getSelection();
        BlockArray blocks = ConvertUtil.convert(user.getWorld(), BlockArray.class, selection);
        
        ExtractableColor[] extractableColors = VVPlugin.getRegistry().getColors("default");
        ZipFile pack = Resources.getZipFile(getClass(), "resourcepacks/default.zip");
        ColorMap colors = ConvertUtil.convert(pack, ColorMap.class, new Object[] {extractableColors});
        
        VoxelArray voxels = ConvertUtil.convert(blocks, VoxelArray.class, colors, 0);
        user.putData(to, voxels);
    }
    
}
