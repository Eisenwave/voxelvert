package net.grian.vv.fmtvert;

import java.util.HashMap;
import java.util.Map;

public final class FormatverterFactory {
    
    private static Map<Format[], Formatverter> map = new HashMap<>();
    
    static {
        add(Format.BLOCKS, Format.VOXELS, new FormatverterBlocksVoxels());
        add(Format.QEF, Format.VOXELS, new FormatverterQEFVoxels());
    }
    
    private static void add(Format from, Format to, Formatverter fmtverter) {
        map.put(new Format[] {from, to}, fmtverter);
    }
    
    public static Formatverter fromFormats(Format from, Format to) {
        return map.get(new Format[] {from, to});
    }
    
}
