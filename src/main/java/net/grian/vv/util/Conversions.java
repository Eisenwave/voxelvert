package net.grian.vv.util;

import net.grian.vv.convert.ConvManager;

public final class Conversions {

    private Conversions() {}

    private final static ConvManager manager = ConvManager.getInstance();

    public static <A,B> B convert(A from, Class<A> fromClass, Class<B> toClass, Object... args) {
        return manager.convert(from, fromClass, toClass, args);
    }

}
