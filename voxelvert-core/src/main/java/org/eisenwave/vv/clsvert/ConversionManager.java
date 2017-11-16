package org.eisenwave.vv.clsvert;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Deprecated
public final class ConversionManager {
    
    private final Map<ConversionKey<?, ?>, Classverter<?, ?>> converters = new HashMap<>();
    
    public ConversionManager() {}
    
    public <A, B> boolean add(Classverter<A, B> cv) {
        ConversionKey<A, B> key = new ConversionKey<>(cv.getFrom(), cv.getTo());
        return converters.put(key, cv) == null;
    }
    
    @SuppressWarnings("unchecked")
    public <A, B> Classverter<A, B> get(Class<A> from, Class<B> to) {
        ConversionKey<A, B> key = new ConversionKey<>(from, to);
        return (Classverter<A, B>) converters.get(key);
    }
    
    @SuppressWarnings("unchecked")
    public <A, B> B convert(A from, Class<A> fromClass, Class<B> toClass, Object... args) {
        ConversionKey<A, B> key = new ConversionKey<>(fromClass, toClass);
        if (converters.containsKey(key))
            return ((Classverter<A, B>) converters.get(key)).invoke(from, args);
        else
            throw new IllegalStateException("missing: " + fromClass.getSimpleName() + " -> " + toClass.getSimpleName());
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
    
    private static class ConversionKey<A, B> {
        
        private final Class<A> from;
        private final Class<B> to;
        
        public ConversionKey(Class<A> from, Class<B> to) {
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
                return equals((ConversionKey) obj);
            } catch (ClassCastException ex) {
                return false;
            }
        }
        
        public boolean equals(ConversionKey<?, ?> key) {
            return this.from.equals(key.from) && this.to.equals(key.to);
        }
        
        @Override
        public int hashCode() {
            return from.hashCode() ^ to.hashCode();
        }
        
    }
    
}
