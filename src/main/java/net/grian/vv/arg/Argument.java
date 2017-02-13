package net.grian.vv.arg;

import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import java.util.NoSuchElementException;

public interface Argument {
    
    /**
     * Returns the argument with given name from the array.
     *
     * @param name the argument name
     * @param args the argument array
     * @return the argument with the given name
     * @throws NoSuchElementException if the argument can not be found
     */
    @Nonnull
    @Contract(pure = true)
    static Argument find(String name, Argument[] args) {
        for (Argument arg : args)
            if (arg.getName().equals(name))
                return arg;
        throw new NoSuchElementException("arg \""+name+"\" is absent");
    }
    
    abstract String getName();
    
    abstract Object getValue();
    
}
