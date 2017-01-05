package net.grian.vv.cache;

import java.util.HashMap;
import java.util.Map;

import net.grian.vv.convert.Converter;
import net.grian.vv.core.*;

/**
 * A VoxelVert registry.
 */
public class Registry {

    private final Map<String, Class<?>> knownFormats = new HashMap<>();
    private final Map<Class<?>[], Converter<?, ?>> converters = new HashMap<>();

    /**
     * Returns the class of a known format by its name. For example, {@code getKnownFormat("voxels")} returns
     * {@link VoxelArray#getClass()}.
     *
     * @param name the name of the format
     * @return the class of the format
     */
    public Class<?> getKnownFormat(String name) {
        return knownFormats.get(name);
    }

    @SuppressWarnings("unchecked")
    public <A, B> Converter<A, B> getConverter(Class<A> from, Class<B> to) {
        return (Converter<A, B>) converters.get(new Class[] {from, to});
    }

}
