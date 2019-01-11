package eisenwave.vv.util;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 * An iterator which loops endlessly through a list instead from start to finish.
 *
 * @param <E>
 */
public class LoopListIterator<E> implements Iterator<E> {
    
    private final List<E> list;
    private Iterator<E> iterator;
    
    public LoopListIterator(@NotNull List<E> list, int index) {
        this.list = list;
        this.iterator = list.listIterator(index);
    }
    
    @Override
    public boolean hasNext() {
        if (iterator.hasNext())
            return true;
        else {
            iterator = list.iterator();
            return iterator.hasNext();
        }
    }
    
    @Override
    public E next() {
        if (iterator.hasNext())
            return iterator.next();
        else {
            iterator = list.iterator();
            return iterator.next();
        }
    }
    
    @Override
    public void remove() {
        iterator.remove();
    }
    
    @Override
    public void forEachRemaining(Consumer<? super E> action) {
        throw new UnsupportedOperationException("Loop iterator could enter an infinite loop");
    }
    
}
