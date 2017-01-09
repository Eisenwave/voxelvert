package net.grian.vv.cache;

import net.grian.vv.io.DeserializerExtractableArray;
import net.grian.vv.io.ExtractableColor;
import net.grian.vv.util.Resources;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class Registry {

    private final Logger logger;

    private final Map<String, ExtractableColor[]> colorArrays = new HashMap<>();

    public Registry(Logger logger) {
        this.logger = logger;
    }

    public Registry() {
        this(Logger.getGlobal());
    }

    public void loadResources() {
        loadColorExtractors();
    }

    public void loadColorExtractors() {
        File dir = Resources.getFile(getClass(), "color_extractors");
        File[] files = dir.listFiles();
        if (files == null) {
            logger.warning(dir+" is empty");
            return;
        }

        for (File file : files) {
            String name = file.getName();
            name = name.substring(0, name.lastIndexOf('.'));
            try {
                colorArrays.put(name, new DeserializerExtractableArray().deserialize(file));
            } catch (IOException ex) {
                logger.warning("failed to load extractor: "+name);
                ex.printStackTrace();
            }
        }
    }

    public ExtractableColor[] getColors(String name) {
        return colorArrays.get(name);
    }

    public void putColors(String name, ExtractableColor[] colors) {
        colorArrays.put(name, colors);
    }



}
