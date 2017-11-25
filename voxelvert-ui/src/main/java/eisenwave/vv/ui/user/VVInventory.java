package eisenwave.vv.ui.user;

import eisenwave.vv.ui.fmtvert.Format;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A VoxelVert inventory which stores objects of given format with a unique name.
 *
 */
public interface VVInventory {
    
    // BASIC GETTERS & SETTERS
    
    /**
     * Returns the user who owns this inventory.
     *
     * @return the inventory owner
     */
    @NotNull
    abstract VVUser getOwner();
    
    @NotNull
    abstract File getDirectory();
    
    /**
     * Returns a file relative to the inventory's directory.
     *
     * @param name the file name
     * @return the file object
     */
    default File getFile(String name) {
        return new File(getDirectory(), name);
    }
    
    /**
     * Returns a set containing the names of all {@link VVInventoryVariable} objects in this inventory.
     *
     * @return the names of all inventory variables
     */
    @NotNull
    abstract Set<String> getVariableNames();
    
    @NotNull
    default List<String> list() {
        List<String> result = new ArrayList<>();
        
        result.addAll(getVariableNames());
        
        File[] files = getDirectory().listFiles();
        if (files == null)
            throw new IllegalStateException("couldn't list files in user directory: " + getDirectory());
    
        for (File file : files)
            result.add(file.getName() + (file.isDirectory()? "/" : ""));
        
        return result;
    }
    
    /**
     * Returns whether the inventory contains a {@link VVInventoryVariable} with given identifier.
     *
     * @param id the identifier
     * @return whether the veriable exists
     */
    abstract boolean hasVariable(String id);
    
    /**
     * Returns whether the inventory contains a {@link VVInventoryVariable} with given identifier.
     *
     * @param id the identifier
     * @return whether the veriable exists
     */
    @Nullable
    abstract VVInventoryVariable<?> getVariable(String id);
    
    /**
     * Returns whether the inventory contains an object with given name.
     *
     * @param name the name
     * @return whether the inventory contains the object
     */
    abstract boolean contains(@NotNull String name);
    
    // FILE OPERATIONS
    
    @Nullable
    abstract BasicFileAttributes getBasicAttributes(String path);
    
    /**
     * Loads an object from the inventory depending on the provided format.
     *
     * @param format the format
     * @param name the object name
     * @return the object or <code>null</code> of no object of given name is stored in the inventory
     * @throws IOException if an I/O error occurs
     */
    @Nullable
    abstract Object load(@NotNull Format format, @Nullable String name) throws IOException;
    
    /**
     * Copies a file from one location to another.
     *
     * @param source the source name
     * @param target the target name
     * @return whether the object could be copied
     * @throws IOException if an I/O error occurs
     */
    abstract boolean copy(@NotNull String source, @NotNull String target, boolean replace) throws IOException;
    
    /**
     * Moves a file from one location to another.
     *
     * @param source the source name
     * @param target the target path
     * @return whether the object could be copied
     * @throws IOException if an I/O error occurs
     */
    abstract boolean move(@NotNull String source, @NotNull String target, boolean replace) throws IOException;
    
    /**
     * Copies a file from one location to another.
     *
     * @param source the source name
     * @param target the target name
     * @return whether the object could be copied
     * @throws IOException if an I/O error occurs
     */
    default boolean copy(@NotNull String source, @NotNull String target) throws IOException {
        return copy(source, target, true);
    }
    
    /**
     * Moves a file from one location to another.
     *
     * @param source the source name
     * @param target the target name
     * @return whether the object could be copied
     * @throws IOException if an I/O error occurs
     */
    default boolean move(@NotNull String source, @NotNull String target) throws IOException {
        return move(source, target, true);
    }
    
    /**
     * Saves an object with given format and name.
     *
     * @param format the format
     * @param object the object
     * @param name the object name
     * @return true if the object could be saved with given format
     * @throws IOException if an I/O error occurs or the object can not be saved with given format
     */
    abstract boolean save(@NotNull Format format, @NotNull Object object, @NotNull String name) throws IOException;
    
    /**
     * Deletes an object with given name.
     *
     * @param name the name
     * @return true if an object was deleted, else false
     */
    abstract boolean delete(@NotNull String name);
    
    /**
     * Clears the inventory.
     */
    abstract void clear();
    
}
