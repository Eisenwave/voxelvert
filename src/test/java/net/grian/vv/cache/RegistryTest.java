package net.grian.vv.cache;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class RegistryTest {

    @Test
    public void loadColorExtractors() throws Exception {
        Registry registry = new Registry();
        registry.loadColorExtractors();
        assertNotNull(registry.getColors("default"));
    }

}