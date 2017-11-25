package eisenwave.vv.ui.user;

import eisenwave.vv.ui.VoxelVert;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

/**
 * A VoxelVert user.
 */
public interface VVUser {
    
    /**
     * Returns the voxelvert instance of this user.
     *
     * @return voxelvert
     */
    @NotNull
    abstract VoxelVert getVoxelVert();
    
    /**
     * Returns the name of the user.
     *
     * @return the user name
     */
    abstract String getName();
    
    /**
     * Returns the inventory of the user.
     *
     * @return the user inventory
     */
    abstract VVInventory getInventory();
    
    @Nullable
    abstract Logger getLogger();
    
    /**
     * Returns whether this type of user can print updates via the {@link #update(String)} method.
     *
     * @return whether this user can print updates
     * @see #update(String)
     */
    public boolean acceptsUpdates();
    
    /**
     * Sends a message to the user.
     *
     * @param msg the message
     */
    abstract void print(String msg);
    
    /**
     * Sends a formatted message to the user.
     *
     * @param format the format string
     * @param args the arguments
     */
    default void print(String format, Object... args) {
        print(String.format(format, args));
    }
    
    /**
     * Sends an error message to the user.
     *
     * @param err the error message
     */
    abstract void error(String err);
    
    /**
     * Sends a formatted error message to the user.
     *
     * @param format the format string
     * @param args the arguments
     */
    default void error(String format, Object... args) {
        error(String.format(format, args));
    }
    
    /**
     * Goes back to the beginning of the current line (by carriage return in the shell) and replaces it with the new
     * message.
     *
     * @param msg the message to print
     */
    default void update(String msg) {
        throw new UnsupportedOperationException("user does not support updates");
    }
    
    /**
     * Goes back to the beginning of the current line (by carriage return in the shell) and replaces it with the new
     * formatted message.
     *
     * @param msg the message to print
     */
    default void update(String msg, Object... args) {
        update(String.format(msg, args));
    }
    
}
