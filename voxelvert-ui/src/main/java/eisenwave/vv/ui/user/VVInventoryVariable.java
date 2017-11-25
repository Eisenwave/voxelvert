package eisenwave.vv.ui.user;

import eisenwave.vv.ui.fmtvert.Format;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public interface VVInventoryVariable<T> extends Supplier<T> {
    
    /**
     * Returns the format of this variable.
     *
     * @return the format of this variable
     */
    @NotNull
    @Contract(pure=true)
    abstract Format getFormat();
    
    @Override
    abstract T get();
    
    /**
     * Returns whether the variable is set.
     *
     * @return whether the variable is set
     */
    default boolean isSet() {
        return false;
    }
    
    @Contract(pure=true)
    default boolean isWritable() {
        return false;
    }
    
    /**
     * Optional method for setting the variable.
     *
     * @param value the value
     */
    default void set(T value) {
        throw new UnsupportedOperationException();
    }
    
}
