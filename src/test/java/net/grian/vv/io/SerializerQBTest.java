package net.grian.vv.io;

import net.grian.torrens.qbcl.DeserializerQB;
import net.grian.torrens.qbcl.SerializerQB;
import net.grian.torrens.qbcl.QBModel;
import net.grian.vv.VVTest;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class SerializerQBTest {

    @Test
    public void preserveVoxelCount() throws Exception {
        Logger logger = VVTest.LOGGER;
        logger.setLevel(Level.INFO);
        
        QBModel model = new DeserializerQB(logger).fromResource(getClass(), "sniper.qb");
        logger.fine("serializing model: "+model);
        int before = model.voxelCount();

        File out = new File(VVTest.DIR_FILES, "SerializerQBTest.qb");
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed to create "+out);

        new SerializerQB(logger).toFile(model, out);

        QBModel model2 = new DeserializerQB(logger).fromFile(out);
        int after = model2.voxelCount();
        
        assertEquals(before, after);
    }

}