package net.grian.vv.convert;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("SpellCheckingInspection")
public final class ConvManager {

    private final Map<ConvKey<?,?>, Converter<?,?>> converters = new HashMap<>();

    public ConvManager() {}

    public <A,B> boolean add(Converter<A,B> converter) {
        ConvKey<A,B> key = new ConvKey<>(converter.getFrom(), converter.getTo());
        return converters.put(key, converter) == null;
    }

    @SuppressWarnings("unchecked")
    public <A,B> Converter<A,B> get(Class<A> from, Class<B> to) {
        ConvKey<A,B> key = new ConvKey<>(from, to);
        return (Converter<A, B>) converters.get(key);
    }

    @SuppressWarnings("unchecked")
    public <A,B> B convert(A from, Class<A> fromClass, Class<B> toClass, Object... args) {
        ConvKey<A,B> key = new ConvKey<>(fromClass, toClass);
        if (converters.containsKey(key)) {
            return ((Converter<A,B>) converters.get(key)).invoke(from, args);
        }
        else throw new ConversionPathException("missing: "+fromClass.getSimpleName()+" -> "+toClass.getSimpleName());
    }

    /*
    @SuppressWarnings("unchecked")
    public <A, B> B convert(A from, ConvPath<A, B> path) {
        Object obj = from;
        for (ConvPath.Step<?,?> step : path)
            obj = convert(obj, step.getFrom(), step.getTo(), step.getArgs());
        return (B) obj;
    }
    */

    private static class ConvKey<A, B> {

        private final Class<A> from;
        private final Class<B> to;

        public ConvKey(Class<A> from, Class<B> to) {
            Objects.requireNonNull(from);
            Objects.requireNonNull(to);
            this.from = from;
            this.to = to;
        }

        public Class<A> getFrom() {
            return from;
        }

        public Class<B> getTo() {
            return to;
        }

        @Override
        public boolean equals(Object obj) {
            try {
                return equals((ConvKey) obj);
            } catch (ClassCastException ex) {
                return false;
            }
        }

        public boolean equals(ConvKey<?,?> key) {
            return this.from.equals(key.from) && this.to.equals(key.to);
        }

        @Override
        public int hashCode() {
            return from.hashCode() ^ to.hashCode();
        }

    }

}
