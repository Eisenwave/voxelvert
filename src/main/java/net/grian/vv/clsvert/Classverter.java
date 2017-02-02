package net.grian.vv.clsvert;

/**
 * A converter between two classes which can be invoked by a user.
 */
public interface Classverter<A, B> {

    /**
     * Returns the class to convert from.
     *
     * @return the class to convert from
     */
    public Class<A> getFrom();

    /**
     * Returns the class to convert into.
     *
     * @return the class to convert into
     */
    public Class<B> getTo();

    /**
     * Lets a user convert an object into another.
     *
     * @param from the object to convert
     * @param args the arguments providing further conversion details
     * @return the converted object
     */
    public B invoke(A from, Object... args);

}
