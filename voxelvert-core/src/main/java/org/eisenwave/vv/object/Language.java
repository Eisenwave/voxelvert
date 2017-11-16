package org.eisenwave.vv.object;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class Language {
    
    private final String name;
    private final Map<String, String> vocab = new HashMap<>();
    
    public Language(@NotNull String name) {
        this.name = name;
    }
    
    public Language(@NotNull String name, Map<String, String> vocab) {
        this.name = name;
        defAll(vocab);
    }
    
    @Contract(pure = true)
    @NotNull
    public String getName() {
        return name;
    }
    
    /**
     * Returns the translation of a key or message indicating that the key is missing.
     *
     * @param key the key
     * @return the translation
     */
    @NotNull
    public String get(@NotNull String key) {
        return vocab.getOrDefault(key, "MISSING TRANSLATION \"" + key + "\"");
    }
    
    /**
     * Convenience method for formatting the translation of a key.
     *
     * @param key the key
     * @param args the arguments
     * @return the translated and formatted string
     * @see String#format(String, Object...)
     */
    public String get(@NotNull String key, Object... args) {
        return String.format(get(key), args);
    }
    
    /**
     * Returns the size of the vocabulary of this language.
     *
     * @return the vocabulary size
     */
    public int size() {
        return vocab.size();
    }
    
    // SETTERS
    
    /**
     * Defines a translation.
     *
     * @param key the translation key
     * @param translation the translation
     */
    public void def(@NotNull String key, @NotNull String translation) {
        vocab.put(
            Objects.requireNonNull(key),
            Objects.requireNonNull(translation));
    }
    
    /**
     * Defines all translations in a vocabulary.
     *
     * @param vocab the vocabulary
     */
    public void defAll(@NotNull Map<? extends String, ? extends String> vocab) {
        vocab.forEach(this::def);
    }
    
    // MISC
    
    @Override
    public String toString() {
        return Language.class.getSimpleName() + "{name=" + name + ", entries=" + size() + "}";
    }
    
}
