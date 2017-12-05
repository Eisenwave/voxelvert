package eisenwave.vv.bukkit.cmd;

import eisenwave.torrens.schematic.BlockKey;

import java.util.Objects;

@SuppressWarnings("unused")
@FunctionalInterface
public interface Parser<T> {
    
    /** Parser which performs no parsing and simply returns the given string.*/
    public final static Parser<String> IDENTITY = str -> str;
    
    /** Long parser.*/
    public final static Parser<Long> LONG_PARSER = Long::parseLong;
    
    /** Int parser.*/
    public final static Parser<Integer> INT_PARSER = Integer::parseInt;
    
    /** Short parser.*/
    public final static Parser<Short> SHORT_PARSER = Short::parseShort;
    
    /** Byte parser.*/
    public final static Parser<Byte> BYTE_PARSER = Byte::parseByte;
    
    /** Double parser.*/
    public final static Parser<Double> DOUBLE_PARSER = Double::parseDouble;
    
    /** Float parser.*/
    public final static Parser<Float> FLOAT_PARSER = Float::parseFloat;
    
    /** Parser which parses block keys of format <code>id:data</code> or just <code>id</code>. */
    public final static Parser<BlockKey> BLOCK_KEY_PARSER = str -> {
        String[] parts = str.split(":", 2);
        short id = Short.parseShort(parts[0]);
        if (parts.length == 1)
            return new BlockKey(id, (byte) 0);
        
        byte data = Byte.parseByte(parts[1]);
        return new BlockKey(id, data);
    };
    
    /** Parser which first tries to parse a string as long, then as double if unsuccessful. */
    public final static Parser<Number> NUMBER_PARSER = str -> {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException ex) {
            return Double.parseDouble(str);
        }
    };
    
    static <T extends Enum<T>> Parser<T> fromEnum(Class<T> clazz, boolean upperCase) {
        Objects.requireNonNull(clazz);
        return str -> {
            try {
                if (upperCase) str = str.toUpperCase();
                return Enum.valueOf(clazz, str);
            } catch (IllegalArgumentException ex) {
                String error = "\""+str+"\" is not a "+clazz.getSimpleName();
                IllegalArgumentException ex2 = new IllegalArgumentException(error);
                ex2.setStackTrace(ex.getStackTrace());
                throw ex2;
            }
        };
    }
    
    static <T extends Enum<T>> Parser<T> fromEnum(Class<T> clazz) {
        return fromEnum(clazz, true);
    }
    
    /**
     * Parses a string.
     *
     * @param str the string
     * @return the parsed object
     * @throws IllegalArgumentException if the string is not parsable
     */
    T parse(String str);
    
}
