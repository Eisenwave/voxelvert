package net.grian.vv.io;

import java.io.*;

/**
 * Throwaway object only meant to perform one deserialization of a stream.
 *
 * @param <T> the object that is the result of parsing the file
 */
public interface Deserializer<T> {

    public T deserialize(InputStream stream) throws ParseException;

    public default T deserialize(File file) throws ParseException {
        try (FileInputStream stream = new FileInputStream(file)) {
            return deserialize(stream);
        } catch (IOException ex) {
            throw new ParseException(ex);
        }
    }

}
