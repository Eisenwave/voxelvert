package eisenwave.vv.ui;

import eisenwave.vv.object.Language;

import java.io.File;

public interface VoxelVert {
    
    /**
     * Returns the root directory in which voxelvert will save files.
     *
     * @return the print directory
     */
    abstract File getDirectory();
    
    /**
     * Returns the language.
     *
     * @return the language
     */
    abstract Language getLanguage();
    
}
