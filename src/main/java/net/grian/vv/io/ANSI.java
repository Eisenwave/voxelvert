package net.grian.vv.io;

@SuppressWarnings("unused")
public class ANSI {

    private final static String PREFIX = "\u001B[";

    public static final String
            BOLD_ON = PREFIX+"1m",
            UNDERLINE_ON = PREFIX+"4m",
            STRIKETHROUGH_ON = PREFIX+"9",
            BOLD_OFF = PREFIX+"22m",
            UNDERLINE_OFF = PREFIX+"24m",
            STRIKETHROUGH_OFF = PREFIX+"29";

    public static final String
            RESET = PREFIX+"0m",
            BLACK = PREFIX+"30m",
            RED = PREFIX+"31m",
            GREEN = PREFIX+"32m",
            YELLOW = PREFIX+"33m",
            BLUE = PREFIX+"34m",
            PURPLE = PREFIX+"35m",
            CYAN = PREFIX+"36m",
            WHITE = PREFIX+"37m";

}