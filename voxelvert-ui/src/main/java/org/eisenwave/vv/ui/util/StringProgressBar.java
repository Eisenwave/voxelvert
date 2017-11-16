package org.eisenwave.vv.ui.util;

import net.grian.spatium.util.*;

public class StringProgressBar {
    
    private final String prefixOn, prefixOff, suffixOn, suffixOff;
    
    private final char off;
    private final char on;
    private final int length;
    
    public StringProgressBar(String prefixOn, char on, String suffixOn,
                             String prefixOff, char off, String suffixOff,
                             int length) {
        this.prefixOn = prefixOn;
        this.suffixOff = suffixOff;
        this.suffixOn = suffixOn;
        this.prefixOff = prefixOff;
        
        this.off = off;
        this.on = on;
        this.length = length;
    }
    
    public StringProgressBar(char on, char off, int length) {
        this("", on, "", "", off, "", length);
    }
    
    public String print(float progress) {
        int onLen = (int) (length * progress), offLen = length - onLen;
        
        return prefixOn
            + Strings.repeat(on, onLen)
            + suffixOn
            + prefixOff
            + Strings.repeat(off, offLen)
            + suffixOff;
    }
    
}
