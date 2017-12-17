package eisenwave.vv.bukkit.browser;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface FileBrowser {
    
    // WORKING DIRECTORY
    
    /**
     * Returns the current working directory.
     *
     * @return the working directory
     */
    abstract Path getDirectory();
    
    /**
     * Changes the current working directory.
     *
     * @param directory the directory
     * @return the new complete directory
     */
    abstract Path changeDirectory(Path directory);
    
    // LIST
    
    /**
     * Lists the directories in the current directory.
     *
     * @return all directories
     */
    abstract List<Path> listDirectoryNames() throws IOException;
    
    /**
     * Lists the files in the current directory.
     *
     * @return all directories
     */
    abstract List<Path> listFileNames() throws IOException;
    
    // FILE OPERATIONS
    
    /**
     * Creates a new directory.
     *
     * @param path the directory path
     * @return true if a new directory was created, else false
     * @throws IOException if an I/O error occurs
     */
    abstract boolean makeDirectory(@NotNull Path path) throws IOException;
    
    /**
     * Creates a new directory and all the parent directories necessary for that.
     *
     * @param path the directory path
     * @return true if a new directory was created, else false
     * @throws IOException if an I/O error occurs
     */
    abstract boolean makeDirectories(@NotNull Path path) throws IOException;
    
    abstract void createFile(@NotNull Path path) throws IOException;
    
    /**
     * Copies a file from one location to another.
     *
     * @param source the source name
     * @param target the target name
     * @return whether the object could be copied
     * @throws IOException if an I/O error occurs
     */
    abstract boolean copy(@NotNull Path source, @NotNull Path target, boolean replace) throws IOException;
    
    /**
     * Moves a file from one location to another.
     *
     * @param source the source name
     * @param target the target path
     * @return whether the object could be copied
     * @throws IOException if an I/O error occurs
     */
    abstract boolean move(@NotNull Path source, @NotNull Path target, boolean replace) throws IOException;
    
    /**
     * Deletes a file with given name.
     *
     * @param name the name
     * @return true if an object was deleted, else false
     */
    abstract boolean delete(@NotNull Path name) throws IOException;
    
    // DEFAULT
    
    /**
     * Copies a file from one location to another.
     *
     * @param source the source name
     * @param target the target name
     * @return whether the object could be copied
     * @throws IOException if an I/O error occurs
     */
    default boolean copy(@NotNull Path source, @NotNull Path target) throws IOException {
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
    default boolean move(@NotNull Path source, @NotNull Path target) throws IOException {
        return move(source, target, true);
    }
    
}
