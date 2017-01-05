package net.grian.vv.io;

import java.io.*;

/**
 * Throwaway object for writing objects to files.
 */
public interface Serializer<T> {

    public void serialize(T object, OutputStream stream) throws IOException;

    public default void serialize(T object, File file) throws ParseException {
        try (FileOutputStream stream = new FileOutputStream(file)) {
            serialize(object, stream);
        } catch (IOException ex) {
            throw new ParseException(ex);
        }
    }

}
