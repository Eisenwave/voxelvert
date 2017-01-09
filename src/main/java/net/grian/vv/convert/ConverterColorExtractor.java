package net.grian.vv.convert;

import net.grian.vv.cache.ColorMap;
import net.grian.vv.core.BlockColor;
import net.grian.vv.core.BlockKey;
import net.grian.vv.io.ExtractableColor;
import net.grian.vv.util.Arguments;
import net.grian.vv.util.Colors;
import org.bukkit.Material;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ConverterColorExtractor implements Converter<ZipFile, ColorMap> {

    @Override
    public Class<ZipFile> getFrom() {
        return ZipFile.class;
    }

    @Override
    public Class<ColorMap> getTo() {
        return ColorMap.class;
    }

    private final Logger logger = Logger.getGlobal();

    @Override
    public ColorMap invoke(ZipFile zip, Object... args) {
        Arguments.requireMin(args, 1);
        Arguments.requireType(args[0], ExtractableColor[].class);

        ExtractableColor[] colors = (ExtractableColor[]) args[0];

        return invoke(zip, colors);
    }

    public ColorMap invoke(ZipFile zip, ExtractableColor[] colors) {
        logger.info("converting rp to color map using "+colors.length+" colors");

        String name = zip.getName();
        name = name.substring(0, name.lastIndexOf('.'));
        ColorMap result = new ColorMap(name);

        for (ExtractableColor color : colors) {
            int rgb;
            try {
                rgb = color.getExtractor().extract(zip);
            } catch (IOException ex) {
                logger.info("failed extraction "+color+": "+ex.getMessage());
                continue;
            }

            result.put(color.getBlock(), new BlockColor(rgb, color.getVoxels()/4096F, color.hasTint()));
        }

        logger.info("finished converting to "+result);
        return result;
    }


}
