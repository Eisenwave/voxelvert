package net.grian.vv.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

@Deprecated
@SuppressWarnings("unchecked")
public class IORegistry {

    private final Map<Class<?>, Deserializer<?>> deserializers = new HashMap<>();
    private final Map<Class<?>, Parser<?>> parsers = new HashMap<>();
    private final Map<Class<?>, Serializer<?>> serializers = new HashMap<>();

    public IORegistry() {}

    public <T> T deserialize(Class<T> clazz, InputStream stream) throws IOException {
        if (clazz == null)
            throw new NullPointerException("class may not be null");
        if (!deserializers.containsKey(clazz))
            throw new IllegalStateException("no deserializer found for: "+clazz);

        Deserializer<T> deserializer = (Deserializer<T>) deserializers.get(clazz);
        return deserializer.deserialize(stream);
    }

    public <T> T deserialize(Class<T> clazz, Reader reader) throws IOException {
        if (clazz == null)
            throw new NullPointerException("class may not be null");
        if (!parsers.containsKey(clazz))
            throw new IllegalStateException("no deserializer found for: "+clazz);

        Parser<T> parser = (Parser<T>) parsers.get(clazz);
        return parser.deserialize(reader);
    }

    public <T> void serialize(T object, Class<T> clazz, OutputStream stream) throws IOException {
        if (object == null)
            throw new NullPointerException("object may not be null");
        if (clazz == null)
            throw new NullPointerException("clazz may not be null");
        if (!serializers.containsKey(clazz))
            throw new IllegalStateException("no deserializer found for: "+clazz);

        Serializer<T> serializer = (Serializer<T>) serializers.get(clazz);
        serializer.serialize(object, stream);
    }

    public <T> void serialize(T object, OutputStream stream) throws IOException {
        serialize(object, (Class<T>) object.getClass(), stream);
    }



}
