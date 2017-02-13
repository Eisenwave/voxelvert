package net.grian.vv.arg;

public class ArgumentImpl implements Argument {
    
    private final String name;
    private final Object value;
    
    public ArgumentImpl(String name, Object value) {
        this.name = name;
        this.value = value;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public Object getValue() {
        return value;
    }
    
}
