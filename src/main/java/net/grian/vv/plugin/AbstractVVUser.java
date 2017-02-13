package net.grian.vv.plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
    
    @Override
    public Object getData(String name) {
        return data.get(name);
    }
    
    @Override
    public Set<String> listData() {
        return Collections.unmodifiableSet(data.keySet());
    }
    
    @Override
    public void clearData() {
        data.clear();
    }
    
    @Override
    public boolean hasData(String name) {
        return data.containsKey(name);
    }
    
}
