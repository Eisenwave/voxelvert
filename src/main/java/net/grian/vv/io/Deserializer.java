package net.grian.vv.io;

import net.grian.vv.util.Resources;

import java.io.*;
import java.net.URL;

/**
 * Throwaway object only meant to perform one deserialization of a stream.
 *
 * @param <T> the object that is the result of parsing the file
 */
public interface Deserializer<T> {

    /**
     * Deserializes an object from an {@link InputStream}.
     *
     * @param stream the stream
     * @return the deserialized object
     * @throws IOException if the deserialization fails
     */
    public abstract T deserialize(InputStream stream) throws IOException;

    /**
     * Deserializes an object from a {@link File} using a {@link FileInputStream}.
     *
     * @param file the file
     * @return the deserialized object
     * @throws IOException if the deserialization fails
     */
    public default T deserialize(File file) throws IOException {
        FileInputStream stream = new FileInputStream(file);
        T result = deserialize(stream);
        stream.close();

        return result;
    }

    /**
     * Deserializes an object from a {@link Class} and a resource path by opening a stream to the resource via the
     * {@link ClassLoader}.
     *
     * @param clazz the class which's class loader is to be used
     * @param resPath the path to the resource
     * @return the deserialized object
     * @throws IOException if the deserialization fails
     */
    public default T deserialize(Class<?> clazz, String resPath) throws IOException {
        InputStream stream = Resources.getStream(clazz, resPath);
        T result = deserialize(stream);
        stream.close();

        return result;
    }

    /**
     * Deserializes an object from a {@link URL} by opening a stream to it.
     *
     * @param url the url
     * @return the deserialized object
     * @throws IOException if the deserialization fails
     */
    public default T deserialize(URL url) throws IOException {
        InputStream stream = url.openStream();
        T result = deserialize(stream);
        stream.close();

        return result;
    }

}
