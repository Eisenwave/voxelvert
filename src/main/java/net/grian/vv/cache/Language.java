package net.grian.vv.cache;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class Language {
    
    private final static Namespace DEFAULT_NS = new Namespace();
    private final static Map<String, Namespace> NAMESPACES = new HashMap<>();
    static {
        NAMESPACES.put("", DEFAULT_NS); //default namespace
    }
    
    @Nonnull
    public static String translate(@Nonnull String key) {
        Objects.requireNonNull(key);
        String[] parts = key.split(":", 2);
        
        if (parts.length == 1)
            return DEFAULT_NS.get(parts[0]);
        else
            return getNamespace(parts[0]).get(parts[1]);
    }
    
    private static Namespace getNamespace(String name) {
        if (hasNamespace(name)) return NAMESPACES.get(name);
        Namespace result = new Namespace();
        NAMESPACES.put(name, result);
        return result;
    }
    
    private static boolean hasNamespace(String name) {
        return NAMESPACES.containsKey(name);
    }
    
    /**
     * Convenience method for formatting the translation of a key.
     *
     * @param key the key
     * @param args the arguments
     * @return the translated and formatted string
     * @see String#format(String, Object...)
     */
    public static String translate(@Nonnull String key, Object... args) {
        return String.format(translate(key), args);
    }
    
    /**
     * Defines a translation.
     *
     * @param key the translation key
     * @param translation the translation
     */
    public static void define(@Nonnull String key, String translation) {
        Objects.requireNonNull(key);
        String[] parts = key.split(":", 2);
    
        if (parts.length == 1)
            DEFAULT_NS.set(parts[0], translation);
        else
            getNamespace(parts[0]).set(parts[1], translation);
    }
    
    private static class Namespace {
    
        private final Map<String, String> dictionary = new HashMap<>();
        
        @Nonnull
        public String get(String key) {
            String result = dictionary.get(key);
            return result==null? "" : result;
        }
        
        public void set(String key, String translation) {
            dictionary.put(key, translation);
        }
        
    }

}
