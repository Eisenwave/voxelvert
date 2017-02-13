package net.grian.vv.arg;

import net.grian.vv.core.BlockSet;

public class ArgumentFactory {
    
    public static Argument create(String name, Object value) {
        return null; //TODO implement
    }
    
    public static Argument selection(BlockSet blocks) {
        return new ArgumentImpl("-s", blocks);
    }
    
    public static Argument flags(int flags) {
        return new ArgumentImpl("-f", flags);
    }

    
}
