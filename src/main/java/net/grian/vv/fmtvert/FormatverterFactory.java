package net.grian.vv.fmtvert;

public final class FormatverterFactory {
    
    private static FormatversionManager manager = new FormatversionManager();
    
    static {
        manager.add(new FormatverterBlocksVoxels());
        manager.add(new FormatverterQEFVoxels());
        manager.add(new FormatverterQBVoxels());
    }
    
    public static Formatverter fromFormats(Format from, Format to) {
        return manager.get(from, to);
    }
    
}
