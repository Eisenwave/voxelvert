package net.grian.vv.fmtvert;

import net.grian.vv.arg.Argument;
import net.grian.vv.arg.ArgumentImpl;
import net.grian.vv.plugin.VVUser;

/**
 * A converter between two formats which can be invoked by a user.
 */
public interface Formatverter {
    
    /**
     * Returns the class to convert from.
     *
     * @return the class to convert from
     */
    public abstract Format getFrom();
    
    /**
     * Returns the class to convert into.
     *
     * @return the class to convert into
     */
    public abstract Format getTo();
    
    /**
     * Executes the conversion between formats.
     */
    public abstract void convert(VVUser user, String from, String to, Argument... args) throws Exception;
    
}
