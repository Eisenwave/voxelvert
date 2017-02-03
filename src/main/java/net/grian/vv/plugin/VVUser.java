package net.grian.vv.plugin;

import net.grian.vv.core.BlockSet;
import org.bukkit.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;

/**
 * A VoxelVert user.
 */
public interface VVUser {
    
    /**
     * Returns the personal directory the user has on the server.
     *
     * @return the personal directory
     */
    abstract File getFileDirectory() throws IOException;
    
    /**
     * Returns the file in the user's directory.
     *
     * @param relPath the path relative to the user's directory
     * @return the file
     * @throws IOException if an I/O Error occurs
     */
    abstract File getFile(String relPath) throws IOException;
    
    /**
     * Returns the name of the user.
     *
     * @return the user name
     */
    abstract String getName();
    
    @SuppressWarnings("unchecked")
    abstract void putData(String name, Object object);
    
    abstract boolean removeData(String name);
    
    abstract <T> T getData(String name);
    
    abstract boolean hasData(String name);
    
    abstract BlockSet getSelection();
    
    abstract World getWorld();
    
}
