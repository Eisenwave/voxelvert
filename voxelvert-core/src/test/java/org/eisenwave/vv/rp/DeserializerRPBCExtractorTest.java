package org.eisenwave.vv.rp;

import org.eisenwave.vv.io.DeserializerRPBCExtractor;
import org.eisenwave.vv.io.RPBCExtractor;
import org.eisenwave.vv.object.ColorMap;
import org.junit.Test;

import java.io.File;
import java.util.zip.ZipFile;

import static org.junit.Assert.*;

public class DeserializerRPBCExtractorTest {
    
    private final static String
        DEFAULT_EXTRACTOR = "color_extractors/default.json",
        DEFAULT_RP = "resourcepacks/default.zip";
    
    @Test
    public void testDefaultExtractorLoad() throws Exception {
        RPBCExtractor extractor = new DeserializerRPBCExtractor().fromResource(getClass(), DEFAULT_EXTRACTOR);
        assertNotNull(extractor);
    }
    
    @SuppressWarnings("ConstantConditions")
    @Test
    public void testDefaultExtractorExtract() throws Exception {
        RPBCExtractor extractor = new DeserializerRPBCExtractor().fromResource(getClass(), DEFAULT_EXTRACTOR);
        File file = new File(getClass().getClassLoader().getResource(DEFAULT_RP).getFile());
        ZipFile zip = new ZipFile(file);
        
        ColorMap colors = extractor.extract(zip);
        assertNotNull(colors);
    }
    
}
