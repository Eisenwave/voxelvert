package org.eisenwave.vv.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>
 *     An abstract iterator which validates entries each step.
 * </p>
 * <p>
 *     While the {@link #hasNext()} method behaves as usual, the {@link #next()} method skips all following irrelevant
 *     results after being called.
 * </p>
 *
 */
@Deprecated
public abstract class ValidatingIterator<E> implements Iterator<E> {

    public ValidatingIterator() {
        skipToValid();
    }

    @Override
    public E next() {
        E result = peek();
        skip();
        skipToValid();
        return result;
    }

    private void skipToValid() {
        while (hasNext()) {
            if (validate(peek())) break;
            else skip();
        }
    }

    /**
     * Skips one element, doing the same as the {@link #next()} method but without returning an element.
     *
     * @throws NoSuchElementException if the iterator can not skip any further
     */
    protected abstract void skip() throws NoSuchElementException;

    /**
     * Returns the next element without changing the iterator's position.
     *
     * @return the next element
     * @throws NoSuchElementException if there is no more elements
     */
    protected abstract E peek() throws NoSuchElementException;

    /**
     * Checks whether a given element is valid.
     *
     * @param e the element
     * @return whether the element is valid
     */
    protected abstract boolean validate(E e);

}
