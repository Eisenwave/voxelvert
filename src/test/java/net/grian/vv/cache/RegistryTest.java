package net.grian.vv.cache;

import net.grian.vv.VVTest;
import org.junit.Test;

import java.util.logging.Level;

import static org.junit.Assert.assertNotNull;

public class RegistryTest {

    @Test
    public void loadColorExtractors() throws Exception {
        Registry registry = new Registry();
        registry.loadColorExtractors();
        assertNotNull(registry.getColors("default"));
    }
    
    public void someTest() throws Exception {
        VVTest.LOGGER.setLevel(Level.ALL);
        VVTest.LOGGER.severe("severe message");
        VVTest.LOGGER.warning("warning message");
        VVTest.LOGGER.info("info message");
        VVTest.LOGGER.config("config message");
        VVTest.LOGGER.fine("fine message");
        VVTest.LOGGER.finer("finer message");
        VVTest.LOGGER.finest("finest message");
    }

}