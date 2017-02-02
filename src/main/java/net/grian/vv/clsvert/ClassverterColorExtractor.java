package net.grian.vv.clsvert;

import net.grian.vv.cache.ColorMap;
import net.grian.vv.core.BlockColor;
import net.grian.vv.io.ExtractableColor;
import net.grian.vv.util.Arguments;

import java.io.IOException;
import java.util.*;
import java.util.zip.ZipFile;

public class ClassverterColorExtractor implements Classverter<ZipFile, ColorMap> {

    @Override
    public Class<ZipFile> getFrom() {
        return ZipFile.class;
    }

    @Override
    public Class<ColorMap> getTo() {
        return ColorMap.class;
    }

    @Override
    public ColorMap invoke(ZipFile zip, Object... args) {
        Arguments.requireMin(args, 1);
        Arguments.requireType(args[0], ExtractableColor[].class);

        ExtractableColor[] colors = (ExtractableColor[]) args[0];

        return invoke(zip, colors);
    }

    public ColorMap invoke(ZipFile zip, ExtractableColor[] colors) {
        //logger.info("converting rp to color map using "+colors.length+" colors");

        String name = zip.getName();
        name = name.substring(0, name.lastIndexOf('.'));
        ColorMap result = new ColorMap(name);
    
        Collection<ExtractableColor> collection = Arrays.asList(colors);
    
        collection.parallelStream().forEach(color -> {
            int rgb;
            try {
                rgb = color.getExtractor().extract(zip);
            } catch (IOException ex) {
                return;
            }
    
            result.put(color.getBlock(), new BlockColor(rgb, color.getVoxels()/4096F, color.hasTint()));
        });
        
        return result;
    }
    
    private static <T> Collection<Spliterator<T>> spliterators(T[] array) {
        Collection<Spliterator<T>> spliterators = new LinkedList<>();
        spliterators.add(Arrays.spliterator(array));
        
        int threads = Runtime.getRuntime().availableProcessors();
        while ((threads /= 2) > 0) {
            Collection<Spliterator<T>> extras = new LinkedList<>();
            for (Spliterator<T> spliterator : spliterators)
                extras.add(spliterator.trySplit());
            
            spliterators.addAll(extras);
        }
        
        return spliterators;
    }


}
