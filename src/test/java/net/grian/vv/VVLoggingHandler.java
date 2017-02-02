package net.grian.vv;

import net.grian.spatium.util.PrimMath;
import net.grian.torrens.util.ANSI;

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
        ANSI.BLUE,    //0
        ANSI.BLUE,   //100
        ANSI.BLUE,   //200
        ANSI.BLUE,   //FINEST = 300
        ANSI.BLUE,   //FINER = 400
        ANSI.BLUE,   //FINE = 500
        ANSI.CYAN,   //600
        ANSI.CYAN,   //CONFIG = 700
        ANSI.GREEN,  //INFO = 800
        ANSI.YELLOW, //WARNING = 900
        ANSI.RED,    //SEVERE = 1000
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
