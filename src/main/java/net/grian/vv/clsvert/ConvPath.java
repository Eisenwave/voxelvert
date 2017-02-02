package net.grian.vv.clsvert;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ConvPath<A, B> implements Iterable<ConvPath.Step<?,?>> {

    private boolean started = false;
    private boolean ended = false;

    private final Class<A> from;
    private final Class<B> to;

    private final List<Step<?, ?>> steps = new LinkedList<>();

    public ConvPath(Class<A> from, Class<B> to) {
        this.from = from;
        this.to = to;
    }

    public ConvPath<A, B> append(Class<?> from, Class<?> to, Object... args) {
        if (!hasStarted() && this.from != from)
            throw new IllegalStateException("conversion needs a start");
        if (hasEnded())
            throw new IllegalStateException("conversion is already complete");

        steps.add(new Step<>(from, to, args));
        if (this.from == from) started = true;
        if (this.to == to) ended = true;
        return this;
    }

    public boolean hasStarted() {
        return started;
    }

    public boolean hasEnded() {
        return ended;
    }

    public boolean isComplete() {
        return started && ended;
    }

    @Override
    public Iterator<Step<?, ?>> iterator() {
        return steps.iterator();
    }

    public static class Step<X, Y> {

        private final Class<X> from;
        private final Class<Y> to;
        private final Object[] args;

        private Step(Class<X> from, Class<Y> to, Object[] args) {
            this.from = from;
            this.to = to;
            this.args = args;
        }

        public Class<X> getFrom() {
            return from;
        }

        public Class<Y> getTo() {
            return to;
        }

        public Object[] getArgs() {
            return args;
        }

    }

}
