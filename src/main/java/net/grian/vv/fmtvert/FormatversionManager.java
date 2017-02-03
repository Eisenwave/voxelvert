package net.grian.vv.fmtvert;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FormatversionManager {
    
    private final Map<FmtversionKey, Formatverter> converters = new HashMap<>();
    
    public FormatversionManager() {}
    
    public boolean add(Formatverter classverter) {
        FmtversionKey key = new FmtversionKey(classverter.getFrom(), classverter.getTo());
        return converters.put(key, classverter) == null;
    }
    
    public Formatverter get(Format from, Format to) {
        FmtversionKey key = new FmtversionKey(from, to);
        return converters.get(key);
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
        
        public Format getFrom() {
            return from;
        }
        
        public Format getTo() {
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
            return from.ordinal() | (to.ordinal() << 16);
        }
        
    }
    
}
