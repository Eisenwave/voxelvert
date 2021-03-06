package eisenwave.vv.ui.fmtvert;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class FormatverterMap {
    
    private final Map<FmtversionKey, Supplier<? extends Formatverter>> converters = new HashMap<>();
    
    public FormatverterMap() {}
    
    public boolean put(@NotNull Format from, @NotNull Format to, Supplier<? extends Formatverter> supplier) {
        FmtversionKey key = new FmtversionKey(from, to);
        return converters.put(key, supplier) == null;
    }
    
    @Nullable
    public Formatverter get(Format from, Format to) {
        FmtversionKey key = new FmtversionKey(from, to);
        Supplier<? extends Formatverter> supplier = converters.get(key);
        return supplier == null? null : supplier.get();
    }
    
    public Collection<Formatverter> getFormatverters() {
        /* converters.forEach((key, value) -> {
            try {
                System.out.println(key.from.getId() + "->" + key.to.getId() + ": " + (value.get() == null));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }); */
        return converters.values().stream()
            .map(Supplier::get)
            .collect(Collectors.toSet());
    }
    
    public Format[] getOutputFormats(Format input) {
        return converters.keySet().stream()
            .filter(key -> key.getSource().equals(input))
            .map(FmtversionKey::getTarget)
            .toArray(Format[]::new);
    }
    
    public Format[] getInputFormats(Format output) {
        return converters.keySet().stream()
            .filter(key -> key.getTarget().equals(output))
            .map(FmtversionKey::getSource)
            .toArray(Format[]::new);
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
            return (from.getId() + to.getId()).hashCode();
        }
        
    }
    
}
