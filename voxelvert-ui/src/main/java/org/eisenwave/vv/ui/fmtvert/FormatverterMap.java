package org.eisenwave.vv.ui.fmtvert;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class FormatverterMap {
    
    private final Map<FmtversionKey, Formatverter> converters = new HashMap<>();
    
    public FormatverterMap() {}
    
    public boolean put(@NotNull Format from, @NotNull Format to, Formatverter classverter) {
        FmtversionKey key = new FmtversionKey(from, to);
        return converters.put(key, classverter) == null;
    }
    
    public Formatverter get(Format from, Format to) {
        FmtversionKey key = new FmtversionKey(from, to);
        return converters.get(key);
    }
    
    public Formatverter[] getFormatverters() {
        Collection<Formatverter> result = converters.values();
        
        return result.toArray(new Formatverter[result.size()]);
    }
    
    public Format[] getOutputFormats(Format input) {
        Collection<Format> result = converters.keySet().stream()
            .filter(key -> key.getSource().equals(input))
            .map(FmtversionKey::getTarget)
            .collect(Collectors.toCollection(LinkedList::new));
        
        return result.toArray(new Format[result.size()]);
    }
    
    public Format[] getInputFormats(Format output) {
        Collection<Format> result = converters.keySet().stream()
            .filter(key -> key.getTarget().equals(output))
            .map(FmtversionKey::getSource)
            .collect(Collectors.toCollection(LinkedList::new));
        
        return result.toArray(new Format[result.size()]);
    }
    
    public Set<Format> sourceSet() {
        return this.converters.keySet().stream()
            .map(FmtversionKey::getSource)
            .collect(Collectors.toSet());
    }
    
    public Set<Format> targetSet() {
        return this.converters.keySet().stream()
            .map(FmtversionKey::getTarget)
            .collect(Collectors.toSet());
    }
    
    private static class FmtversionKey {
        
        private final Format from;
        private final Format to;
        
        public FmtversionKey(Format from, Format to) {
            Objects.requireNonNull(from);
            Objects.requireNonNull(to);
            this.from = from;
            this.to = to;
        }
        
        public Format getSource() {
            return from;
        }
        
        public Format getTarget() {
            return to;
        }
        
        @Override
        public boolean equals(Object obj) {
            return super.equals(obj) || obj instanceof FmtversionKey && equals((FmtversionKey) obj);
        }
        
        public boolean equals(FmtversionKey key) {
            return this.from.equals(key.from) && this.to.equals(key.to);
        }
        
        @Override
        public int hashCode() {
            return (from.getId()+to.getId()).hashCode();
        }
        
    }
    
}
