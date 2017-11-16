package org.eisenwave.vv.ui.cmd;

import org.junit.Test;

import static org.junit.Assert.*;

public class CommandCallTest {
    
    @Test
    public void parse_strictOrder_validInput() throws Exception {
        CommandCall call = new CommandCall();
        call.setStrictOrder(true);
        
        call.parse("arg0", "arg1", "-a", "x", "-b", "-c", "y");
    }
    
    @Test
    public void parse_strictOrder_invalidInput() throws Exception {
        CommandCall call = new CommandCall();
        call.setStrictOrder(true);
        
        try {
            call.parse("arg0", "-a", "x", "arg1");
        } catch (IllegalArgumentException ex) {
            return;
        }
        throw new AssertionError("argument 'arg1' should not be allowed");
    }
    
    @Test
    public void parse_strictKwArgs_validInput() throws Exception {
        CommandCall call = new CommandCall();
        call.setStrictKwArgs(true);
        call.addValidKwArgs("a", "b", "c");
        
        call.parse("arg0", "arg1", "-a", "x", "-b", "-c", "y");
    }
    
    @Test
    public void parse_strictKwArgs_invalidInput() throws Exception {
        CommandCall call = new CommandCall();
        call.setStrictKwArgs(true);
        call.addValidKwArgs("a", "b", "c");
        
        try {
            call.parse("-d");
        } catch (IllegalArgumentException ex) {
            return;
        }
        throw new AssertionError("keyword argument '-d' should not be allowed");
    }
    
}