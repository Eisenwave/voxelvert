package eisenwave.vv.rp;

import eisenwave.vv.io.DeserializerBCE;
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
        BlockColorExtractor extractor = new DeserializerBCE().fromResource(getClass(), DEFAULT_EXTRACTOR);
        assertNotNull(extractor);
    }
    
    @SuppressWarnings("ConstantConditions")
    @Test
    public void testDefaultExtractorExtract() throws Exception {
        BlockColorExtractor extractor = new DeserializerBCE().fromResource(getClass(), DEFAULT_EXTRACTOR);
        File file = new File(getClass().getClassLoader().getResource(DEFAULT_RP).getFile());
        ZipFile zip = new ZipFile(file);
        
        BlockColorTable colors = extractor.extract(zip);
        assertNotNull(colors);
    }
    
}
