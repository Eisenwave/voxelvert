package org.eisenwave.vv.bukkit.util;

import java.util.regex.Pattern;

public final class SyntaxUtil {
    
    private final static Pattern
        INTEGER = Pattern.compile("(?<![0-9.])[0-9]+(?!\\.)"),
        FLOAT = Pattern.compile("[0-9]+?\\.[0-9]+"),
        STRING = Pattern.compile(".*?(?<!\\\\)");
    
    private SyntaxUtil() {}
    
    
}
