package eisenwave.vv;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class VVTest {

    private static VVTest instance = null;

    public synchronized static VVTest getInstance() {
        return instance==null? instance = new VVTest() : instance;
    }
    
    public final static Logger LOGGER = Logger.getLogger("voxelvert.debug");
    static {
        LOGGER.setUseParentHandlers(false);
        LOGGER.addHandler(new VVLoggingHandler());
    }
    
    private final static File testDirectory = new File("/tmp/vv");
    
    public static File directory() throws IOException {
        if (testDirectory.isFile() || !testDirectory.exists() && !testDirectory.mkdir())
            throw new IOException("could not initialize temporary directory");
        return testDirectory;
    }
    
}
