package eisenwave.vv.util;

public final class Arguments {
    
    private Arguments() {}
    
    public static void requireMin(Object[] args, int min, String format) {
        if (args.length < min)
            throw new IllegalArgumentException(String.format(format, min));
    }
    
    public static void requireMin(Object[] args, int min) {
        requireMin(args, min, "at least %s arguments required");
    }
    
    public static void requireMax(Object[] args, int max, String format) {
        if (args.length > max)
            throw new IllegalArgumentException(String.format(format, max));
    }
    
    public static void requireMax(Object[] args, int max) {
        requireMax(args, max, "no more than %s arguments allowed");
    }
    
    public static <T> T requireType(Object arg, Class<T> type, String name, String format) {
        if (!type.isAssignableFrom(arg.getClass()))
            throw new IllegalArgumentException(String.format(format, name, type.getSimpleName()));
        return type.cast(arg);
    }
    
    public static <T> T requireType(Object arg, Class<T> type, String name) {
        return requireType(arg, type, name, "%s must be a %s");
    }
    
    public static <T> T requireType(Object arg, Class<T> type) {
        return requireType(arg, type, arg.getClass().getSimpleName());
    }
    
    public static void requireRange(float arg, float min, float max, String name, String format) {
        if (arg < min || arg > max)
            throw new IllegalArgumentException(String.format(format, name, arg, min, max));
    }
    
    public static void requireRange(float arg, float min, float max, String name) {
        requireRange(arg, min, max, name, "%s (%s) must be in range from %s, %s");
    }
    
    public static void requireRange(int arg, int min, int max, String name, String format) {
        if (arg < min || arg > max)
            throw new IllegalArgumentException(String.format(format, name, arg, min, max));
    }
    
    public static void requireRange(int arg, int min, int max, String name) {
        requireRange(arg, min, max, name, "%s (%s) must be in range from %s, %s");
    }
    
    public static void requireNonnull(Object arg, String msg) {
        if (arg == null)
            throw new IllegalArgumentException(msg);
    }
    
    public static void requireNonnull(Object arg) {
        if (arg == null)
            throw new IllegalArgumentException("arg must not be null");
    }
    
    //ALL NONNULL
    
    /**
     * Throws an {@link IllegalArgumentException} if any of the iterated arguments are null.
     *
     * @param args the arguments
     * @param msg the exception message
     */
    public static void requireAllNonnull(Iterable<?> args, String msg) {
        for (Object arg : args)
            if (arg == null) throw new IllegalArgumentException(msg);
    }
    
    /**
     * Throws an {@link IllegalArgumentException} if any of the iterated arguments are null.
     *
     * @param args the arguments
     */
    public static void requireAllNonnull(Iterable<?> args) {
        requireAllNonnull(args, "args must not be null");
    }
    
}
