package net.grian.vv;

public final class TestUtil {

    private TestUtil() {}

    public static void printMillis(Runnable r, String action) {
        long before = System.currentTimeMillis();
        r.run();
        long after = System.currentTimeMillis();
        System.out.println(action+" took "+(after-before)+" ms");
    }

}
