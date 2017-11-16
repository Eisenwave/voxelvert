package org.eisenwave.vv.ui.fmtvert;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

public abstract class Progress {
    
    private final Collection<ProgressListener> observers = new HashSet<>();
    
    private int current = 0;
    
    protected abstract int getMaxProgress();
    
    /**
     * Returns the current process progress, a value between 0 and 1.
     *
     * @return the current progress
     */
    public float getProgress() {
        int max = getMaxProgress();
        if (current > max) throw new IllegalStateException("current > max");
        
        return (float) current / max;
    }
    
    public int getRawProgress() {
        return current;
    }
    
    public void addListener(ProgressListener observer) {
        observers.add(Objects.requireNonNull(observer, "observer"));
    }
    
    public void removeListener(ProgressListener observer) {
        observers.remove(observer);
    }
    
    // MUTATORS
    
    protected void set(int current) {
        this.current = current;
        
        for (ProgressListener observer : observers)
            observer.update(getRawProgress(), getMaxProgress(), getProgress());
    }
    
}
