package eisenwave.vv.clsvert;

import org.jetbrains.annotations.NotNull;

/**
 * A converter between two classes which can be invoked by a user.
 */
public interface Classverter<A, B> {
    
    /**
     * Lets a user convert an object into another.
     *
     * @param from the object to convert
     * @param args the arguments providing further conversion details
     * @return the converted object
     */
    @Deprecated
    public B invoke(@NotNull A from, @NotNull Object... args);
    
}
