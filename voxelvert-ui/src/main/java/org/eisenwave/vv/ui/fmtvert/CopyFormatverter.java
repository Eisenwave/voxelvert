package org.eisenwave.vv.ui.fmtvert;

import org.eisenwave.vv.ui.user.VVUser;

import java.util.Map;

/**
 * <p>
 *     Special formatverter which instructs the inventory to copy an entry.
 * </p>
 * <p>
 *     This formatverter should only be used when source- and target formats are identical and a file or object
 *     copy is required.
 * </p>
 */
public class CopyFormatverter extends Formatverter {
    
    @Override
    public String[] getOptionalParams() {
        return new String[] {"v", "verbose"};
    }
    
    @Override
    public int getMaxProgress() {
        return 1;
    }
    
    @Override
    public void convert(VVUser user, String from, String to, Map<String,String> args) throws Exception {
        user.getInventory().copy(from, to);
        set(1);
    }
    
}
