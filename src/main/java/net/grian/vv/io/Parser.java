package net.grian.vv.io;

import java.io.*;

public interface Parser<T> extends Deserializer<T> {

    /**
     * Deserializes an object from a {@link Reader}.
     *
     * @param reader the reader
     * @return the deserialized object
     * @throws IOException if the deserialization fails
     */
    abstract T deserialize(Reader reader) throws IOException;

    /**
     * Deserializes an object from a {@link String} using a {@link StringReader}.
     *
     * @param str the string
     * @return the deserialized object
     * @throws IOException if the deserialization fails
     */
    default T deserialize(String str) throws IOException {
        Reader reader = new StringReader(str);
        T result = deserialize(reader);
        reader.close();
        return result;
    }

    @Override
    default T deserialize(InputStream stream) throws IOException {
        return deserialize(new InputStreamReader(stream));
    }

    @Override
    default T deserialize(File file) throws IOException {
        Reader reader = new FileReader(file);
        T result = deserialize(reader);
        reader.close();
        return result;
    }



}
