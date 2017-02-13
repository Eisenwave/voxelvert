package net.grian.vv.fmtvert;

import net.grian.spatium.voxel.BlockArray;
import net.grian.spatium.voxel.VoxelArray;
import net.grian.torrens.util.Resources;
import net.grian.vv.arg.Argument;
import net.grian.vv.cache.ColorMap;
import net.grian.vv.core.BlockSet;
import net.grian.vv.io.ExtractableColor;
import net.grian.vv.plugin.VVPlugin;
import net.grian.vv.plugin.VVUser;
import net.grian.vv.util.Arguments;
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
    public void convert(VVUser user, String from, String to, Argument... args) throws Exception {
        Arguments.requireMin(args, 2);
        BlockSet selection = user.getSelection();
        int flags = argFlags(args);
        
        BlockArray blocks = ConvertUtil.convert(user.getWorld(), BlockArray.class, selection, flags);
        
        ExtractableColor[] extractableColors = VVPlugin.getRegistry().getColors("default");
        ZipFile pack = Resources.getZipFile(getClass(), "resourcepacks/default.zip");
        ColorMap colors = ConvertUtil.convert(pack, ColorMap.class, new Object[] {extractableColors});
        
        VoxelArray voxels = ConvertUtil.convert(blocks, VoxelArray.class, colors, 0);
        user.putData(to, voxels);
    }
    
    private BlockSet argSelection(Argument[] args) {
        Argument selectionArg = Argument.find("-s", args);
        return (BlockSet) selectionArg.getValue();
    }
    
    private int argFlags(Argument[] args) {
        Argument selectionArg = Argument.find("-f", args);
        return ((Number) selectionArg.getValue()).intValue();
    }
    
}
