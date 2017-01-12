package net.grian.vv.io;

import net.grian.vv.util.Resources;

import java.io.*;

/**
 * Throwaway object for writing objects to files.
 */
public interface Serializer<T> {

    public void serialize(T object, OutputStream stream) throws IOException;

    public default void serialize(T object, File file) throws IOException {
        FileOutputStream stream = new FileOutputStream(file);
        serialize(object, stream);
        stream.close();
    }

    public default void serialize(T object, Class<?> clazz, String resPath) throws IOException {
        serialize(object, Resources.getFile(clazz, resPath));
    }

}
