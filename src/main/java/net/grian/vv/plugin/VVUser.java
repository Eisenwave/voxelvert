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
     * Returns the name of the user.
     *
     * @return the user name
     */
    abstract String getName();
    
    abstract <T> void putData(String name, @Nullable T object, Class<T> type);
    
    @SuppressWarnings("unchecked")
    default <T> void putData(String name, @Nonnull T object) {
        putData(name, object, (Class<T>) object.getClass());
    }
    
    abstract boolean removeData(String name);
    
    abstract <T> T getData(String name);
    
    abstract BlockSet getSelection();
    
    abstract World getWorld();
    
}
