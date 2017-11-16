package org.eisenwave.vv.ui.user;

import org.eisenwave.vv.ui.fmtvert.Format;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

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
    
    @NotNull
    default String[] list(boolean sort) {
        File[] files = getDirectory().listFiles();
        assert files != null;
        
        String[] names = new String[files.length];
        for (int i = 0; i < files.length; i++)
            names[i] = files[i].getName() + (files[i].isDirectory()? "/" : "");
        
        if (sort) {
            Arrays.sort(names, Comparator.comparing(String::toLowerCase));
            Arrays.sort(names, (x,y) -> {
                if (x.endsWith("/")) return y.endsWith("/")? 0 : -1;
                else return y.endsWith("/")? 1 : 0;
            });
    
            
        }
    
        return names;
    }
    
    @NotNull
    default String[] list() {
        return list(true);
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
    abstract boolean contains(@Nullable Format format, @NotNull String name);
    
    // FILE OPERATIONS
    
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
     * @param from the input name
     * @param to the output name
     * @return whether the object could be copied
     * @throws IOException if an I/O error occurs
     */
    abstract boolean copy(@NotNull String from, @NotNull String to) throws IOException;
    
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
