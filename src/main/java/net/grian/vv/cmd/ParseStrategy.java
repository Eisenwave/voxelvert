package net.grian.vv.cmd;

import java.util.Arrays;

@FunctionalInterface
public interface ParseStrategy {
    
    public final static ParseStrategy IDENTITY = new ParseStrategy() {
        @Override
        public Parser[] formatOf(String[] args) {
            Parser[] result = new Parser[args.length];
            for (int i = 0; i<args.length; i++)
                result[i] = Parser.IDENTITY;
            return result;
        }
        @Override
        public Object[] parse(String[] args) {
            return Arrays.copyOf(args, 0);
        }
    };
    
    abstract Parser[] formatOf(String[] args);

    default Object[] parse(String[] args) {
        Parser[] parsers = formatOf(args);
        Object[] result = new Object[args.length];
        
        for (int i = 0; i<args.length; i++)
            result[i] = parsers[i].parse(args[i]);
        
        return result;
    }

}
