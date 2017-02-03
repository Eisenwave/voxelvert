package net.grian.vv.plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractVVUser implements VVUser {
    
    private final Map<String, Object> data = new HashMap<>();
    
    @Override
    public File getFile(String relPath) throws IOException {
        return new File(getFileDirectory(), relPath);
    }
    
    @Override
    public void putData(String name, Object object) {
        Objects.requireNonNull(name);
        data.put(name, object);
    }
    
    @Override
    public boolean removeData(String name) {
        Objects.requireNonNull(name);
        return data.remove(name) != null;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getData(String name) {
        return (T) data.get(name);
    }
    
    @Override
    public boolean hasData(String name) {
        return data.containsKey(name);
    }
    
}
