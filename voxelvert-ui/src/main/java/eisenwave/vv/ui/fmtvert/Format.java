package eisenwave.vv.ui.fmtvert;

import eisenwave.spatium.util.PrimArrays;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class Format implements Comparable<Format> {
    
    private final static List<WeakReference<Format>> instances = new LinkedList<>();
    
    /** Color database for assigning blocks to rgb values, derived from texture pack */
    public final static Format
        BLOCK_COLOR_TABLE = new Format("colors", false, "bct"),
    
    /** An image */
    IMAGE = new Format("image", false, "bmp", "gif", "jpg", "jpeg", "png"),
    
    /** Minecraft model */
    MODEL = new Format("model", false),
    
    /** Qubicle Exchange Format */
    QEF = new Format("qef", false, "qef"),
    
    /** Qubicle Binary */
    QB = new Format("qb", false, "qb"),
    
    /** Resource Pack */
    RESOURCE_PACK = new Format("resource_pack", false, "zip"),
    
    /** Minecraft Schematic */
    SCHEMATIC = new Format("schematic", false, "schematic"),
    
    /** Standard Tessellation Language */
    STL = new Format("stl", false, "stl"),
    
    /** Wavefront OBJ */
    WAVEFRONT = new Format("wavefront", false, "obj"),
    
    /** Array of Voxels */
    VOXEL_ARRAY = new Format("voxel_array", true),
    
    /** Mesh of Voxels */
    VOXEL_MESH = new Format("voxel_mesh", true),
    
    /** Array of Blocks */
    BLOCK_ARRAY = new Format("block_array", true);
    
    /*
    private final static Map<String, Format> byExt = new HashMap<>();
    
    static {
        for (Format f : values())
            for (String ext : f.ext)
                byExt.put(ext.toLowerCase(), f);
    }
    */
    
    /**
     * Searches all existing instances of this class for a format which has the specified extension.
     *
     * @param ext the file extension
     * @return the format or <code>null</code> if no format could be found
     */
    @Nullable
    public static Format getByExtension(@NotNull String ext) {
        return searchInstances(f -> PrimArrays.contains(f.getExtensions(), ext));
    }
    
    /**
     * Searches all existing instances of this class for a format which has the specified name.
     *
     * @param id the identifier
     * @return the format or <code>null</code> if no format could be found
     */
    @Nullable
    public static Format getById(@NotNull String id) {
        return searchInstances(f -> id.equals(f.id));
    }
    
    @Nullable
    private static Format searchInstances(Predicate<Format> condition) {
        Iterator<WeakReference<Format>> iter = instances.iterator();
        while (iter.hasNext()) {
            Format f = iter.next().get();
            if (f == null) {
                iter.remove();
                continue;
            }
            
            if (condition.test(f))
                return f;
        }
        
        return null;
    }
    
    private final boolean internal;
    private final String id;
    private final String[] ext;
    
    public Format(@NotNull String id, boolean internal, @NotNull String... extensions) {
        this.id = id;
        this.internal = internal;
        this.ext = extensions;
        
        WeakReference<Format> ref = new WeakReference<>(this);
        instances.add(ref);
    }
    
    /**
     * Returns the identifier of this format.
     *
     * @return the identifier of this format
     */
    public String getId() {
        return id;
    }
    
    @NotNull
    public String[] getExtensions() {
        return Arrays.copyOf(ext, ext.length);
    }
    
    /**
     * Returns whether a format is internal. Such formats will not be available conversion options to the user in
     * interfaces and commands.
     *
     * @return whether the format is internal
     */
    @Contract(pure = true)
    public boolean isInternal() {
        return internal;
    }
    
    /**
     * Returns whether a format is a file format.
     *
     * @return whether a format is a file format
     */
    @Contract(pure = true)
    public boolean isFile() {
        return !internal;
    }
    
    // MISC
    
    
    @Override
    public int compareTo(@NotNull Format o) {
        return getId().compareTo(o.getId());
    }
    
    @Override
    public String toString() {
        return getId();
    }
    
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
    
    public boolean equals(Format format) {
        return getId().equals(format.getId());
    }
    
    @Override
    public int hashCode() {
        return getId().hashCode();
    }
    
}
