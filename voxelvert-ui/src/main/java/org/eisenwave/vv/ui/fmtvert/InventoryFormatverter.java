package org.eisenwave.vv.ui.fmtvert;

import org.eisenwave.vv.ui.user.VVUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * <p>
 *     Special formatverter which loads from the inventory in one format and saves to the inventory right away,
 *     then (optionally) runs an existing formatverter on the temporary inventory entry.
 * </p>
 * <p>
 *     This formatverter should be used when converting from a temporary source format such as
 *     {@link Format#BLOCK_ARRAY} into a target format is already supported. In that case a new format such as
 *     {@link Format#SCHEMATIC} can be added easily and solely use the inventory of a user to complete the first
 *     step.
 * </p>
 */
public class InventoryFormatverter extends Formatverter {
    
    @NotNull
    private final Format sourceFormat, targetFormat;
    @Nullable
    private final Formatverter handle;
    
    /**
     * Constructs a new inventory formatverter.
     *
     * @param source the source format
     * @param intermediary an intermediary format, used by the handle formatverter
     * @param handle a formatverter which converts from the intermediary format into some target format
     */
    public InventoryFormatverter(@NotNull Format source, @NotNull Format intermediary, @Nullable Formatverter handle) {
        this.sourceFormat = source;
        this.targetFormat = intermediary;
        this.handle = handle;
    }
    
    /**
     * Constructs a new inventory formatverter.
     *
     * @param source the source format
     * @param target the target format
     */
    public InventoryFormatverter(@NotNull Format source, @NotNull Format target) {
        this(source, target, null);
    }
    
    @Override
    public int getMaxProgress() {
        return handle == null? 1 : handle.getMaxProgress();
    }
    
    @Override
    public String[] getMandatoryParams() {
        return handle == null? new String[0] : handle.getMandatoryParams();
    }
    
    @Override
    public String[] getOptionalParams() {
        return handle == null? new String[0] : handle.getOptionalParams();
    }
    
    @SuppressWarnings("Duplicates")
    @Override
    public void convert(VVUser user, String from, String to, Map<String,String> args) throws Exception {
        Object temp = user.getInventory().load(sourceFormat, from);
        assert temp != null;
        
        if (handle == null) {
            user.getInventory().save(targetFormat, temp, to);
            set(getMaxProgress());
            return;
        }
        
        user.getInventory().save(targetFormat, temp, "%temp");
        
        handle.addListener((now, max, rel) -> set(now));
        handle.convert(user, "%temp", to, args);
        user.getInventory().delete("%temp");
        set(getMaxProgress());
    }
    
}
