package eisenwave.vv.bukkit.util;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class HttpHeaders extends HashMap<String, List<String>> {
    
    @Override
    public List<String> get(@NotNull Object key) {
        return super.get(key);
    }
    
    public String getFirst(@NotNull String key) {
        return containsKey(key)? get(key).get(0) : null;
    }
    
    public List<String> put(@NotNull String key, String value) {
        return super.put(key, Collections.singletonList(value));
    }
    
    @Override
    public List<String> put(@NotNull String key, List<String> value) {
        return value.isEmpty()? get(key) : super.put(key, value);
    }
    
}
