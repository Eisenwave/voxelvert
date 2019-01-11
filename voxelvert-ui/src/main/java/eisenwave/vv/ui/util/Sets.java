package eisenwave.vv.ui.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class Sets {
    
    private Sets() {}
    
    @SafeVarargs
    public static <E> Set<E> ofArray(E... array) {
        return new HashSet<>(Arrays.asList(array));
    }
    
    public static <E> Set<E> union(Collection<E> a, Collection<E> b) {
        Set<E> result = new HashSet<>(a);
        result.addAll(b);
        return result;
    }
    
    @SafeVarargs
    public static <E> Set<E> union(Collection<E>... collections) {
        int initSize = 0;
        for (Collection<E> c : collections)
            initSize += c.size();
        Set<E> result = new HashSet<>(initSize);
        for (Collection<E> c : collections)
            result.addAll(c);
        return result;
    }
    
    public static <E> Set<E> union(Iterable<Collection<E>> collections) {
        int initSize = 0;
        for (Collection<E> c : collections)
            initSize += c.size();
        Set<E> result = new HashSet<>(initSize);
        for (Collection<E> c : collections)
            result.addAll(c);
        return result;
    }
    
}
