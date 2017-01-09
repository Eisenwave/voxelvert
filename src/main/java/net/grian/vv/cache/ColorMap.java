package net.grian.vv.cache;

import net.grian.vv.convert.ConverterColorExtractor;
import net.grian.vv.core.BlockColor;
import net.grian.vv.core.BlockKey;
import net.grian.vv.util.Resources;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipFile;

public class ColorMap extends HashMap<BlockKey, BlockColor> {

    public final static int
            /** Accept no blocks with a smaller space occupation */
            INSIST_OCCUPATION = 1,
            UNTINTED = 1 << 1,
            /** Completely ignore alpha channel */
            IGNORE_ALPHA = 1 << 2,
            /** Accept no blocks with lower alpha */
            INSIST_ALPHA = 1 << 3;


    private final String name;

    public ColorMap(String name) {
        this.name = name;
    }

    public static ColorMap loadDefault() throws IOException {
        ZipFile zip = Resources.getZipFile(ColorMap.class, "resourcepacks/default.zip");
        return new ConverterColorExtractor().invoke(zip);
    }

    public BlockColor get(BlockKey key) {
        BlockColor result = super.get(key);
        return (result == null && key.getData() != 0)?
                super.get(new BlockKey(key.getId(), 0)) : result;
    }

    public String getName() {
        return name;
    }

    /**
     * Attempts to find the {@link BlockKey} which is most accurately described by a given {@link BlockColor}.
     *
     * @param color the color of the block
     * @param flags query settings
     * @return the block most accurately described by the color
     */
    public BlockKey get(BlockColor color, int flags) {
        boolean
                insistOcc = (flags & INSIST_OCCUPATION) != 0,
                untinted = (flags & UNTINTED) != 0,
                ignoreA = (flags & IGNORE_ALPHA) != 0;

        Map.Entry<BlockKey, BlockColor> bestFit = null;
        for (Map.Entry<BlockKey, BlockColor> entry : entrySet()) {

        }

        return bestFit==null? null : bestFit.getKey();
    }

    @Override
    public String toString() {
        return ColorMap.class.getSimpleName()+"{size="+size()+"}";
    }

}
