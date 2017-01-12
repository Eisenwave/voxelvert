package net.grian.vv.util.tuple;

import java.util.Iterator;

public interface Tuple extends Iterable<Object> {

    abstract Object get(int i);

    abstract int size();

    default int indexOf(Object value) {
        for (int i = 0; i<size(); i++) {
            if (value.equals(get(i))) return i;
        }
        return -1;
    }

    @Override
    default Iterator<Object> iterator() {
        return new TupleIterator(this);
    }

    public static class TupleIterator implements Iterator<Object> {

        private final Tuple tuple;
        private final int max;

        private int index = 0;

        public TupleIterator(Tuple tuple) {
            this.tuple = tuple;
            this.max = tuple.size()-1;
        }

        @Override
        public boolean hasNext() {
            return index < max;
        }

        @Override
        public Object next() {
            return tuple.get(index++);
        }

    }

}
