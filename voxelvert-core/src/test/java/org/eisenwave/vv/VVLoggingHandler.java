package org.eisenwave.vv;

import eisenwave.commons.io.ANSI;
import eisenwave.commons.util.PrimMath;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class VVLoggingHandler extends Handler {
    
    private final static String[] LEVEL_PREFIX = {
        ANSI.FG_BLUE,    //0
        ANSI.FG_BLUE,   //100
        ANSI.FG_BLUE,   //200
        ANSI.FG_BLUE,   //FINEST = 300
        ANSI.FG_BLUE,   //FINER = 400
        ANSI.FG_BLUE,   //FINE = 500
        ANSI.FG_CYAN,   //600
        ANSI.FG_CYAN,   //CONFIG = 700
        ANSI.FG_GREEN,  //INFO = 800
        ANSI.FG_YELLOW, //WARNING = 900
        ANSI.FG_RED,    //SEVERE = 1000
    };
    
    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    private final static String[][] REPLACE = {};
    
    private final static DateTimeFormatter TIME_FORMATTER = new DateTimeFormatterBuilder()
        .appendValue(ChronoField.HOUR_OF_DAY, 2)
        .appendLiteral(':')
        .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
        .appendLiteral(':')
        .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
        .appendLiteral(':')
        .appendValue(ChronoField.MILLI_OF_SECOND, 4)
        .toFormatter()
        .withZone(ZoneId.systemDefault());
    
    @Override
    public void publish(LogRecord record) {
        StringBuilder builder = new StringBuilder();
        
        Instant now = Instant.ofEpochMilli(record.getMillis());
        builder
            .append("[")
            .append(TIME_FORMATTER.format(now))
            .append("] ");
        
        String clsName = record.getSourceClassName();
        clsName = clsName.substring(clsName.lastIndexOf('.')+1);
        
        builder
            .append(clsName)
            .append("::")
            .append(record.getSourceMethodName())
            .append("()")
            .append('\n');
        
        Level level = record.getLevel();
        int value = PrimMath.clamp(0, level.intValue(), 1000);
        builder
            .append(LEVEL_PREFIX[value / 100])
            .append(level.getName())
            .append(": ")
            .append(ANSI.RESET);
        
        String message = record.getMessage();
        for (String[] pair : REPLACE)
            message = message.replace(pair[0], pair[1]);
        
        builder.append(message);
        System.out.println(builder);
    }
    
    @Override
    public void flush() {}
    
    @Override
    public void close() throws SecurityException {}
    
}
