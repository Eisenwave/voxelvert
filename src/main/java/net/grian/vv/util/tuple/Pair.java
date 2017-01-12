package net.grian.vv.util.tuple;

public class Pair<A,B> implements Tuple {

    public final static int SIZE = 2;

    private final A a;
    private final B b;

    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public A get0() {
        return a;
    }

    public B get1() {
        return b;
    }

    @Override
    public Object get(int i) {
        switch (i) {
            case 0: return get0();
            case 1: return get1();
            default: throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public int size() {
        return SIZE;
    }

}
