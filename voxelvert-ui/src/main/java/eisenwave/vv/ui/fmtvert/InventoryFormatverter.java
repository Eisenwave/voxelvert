package eisenwave.vv.ui.fmtvert;

import eisenwave.vv.ui.user.VVUser;
import org.jetbrains.annotations.NotNull;

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
    
    /**
     * Constructs a new inventory formatverter.
     *
     * @param source the source format
     * @param target the target format
     */
    public InventoryFormatverter(@NotNull Format source, @NotNull Format target) {
        this.sourceFormat = source;
        this.targetFormat = target;
    }
    
    @Override
    public int getMaxProgress() {
        return 1;
    }
    
    @Override
    public Option[] getMandatoryOptions() {
        return new Option[0];
    }
    
    @Override
    public Option[] getOptionalOptions() {
        return new Option[0];
    }
    
    @SuppressWarnings("Duplicates")
    @Override
    public void convert(VVUser user, String from, String to, Map<String,String> args) throws Exception {
        Object temp = user.getInventory().load(sourceFormat, from);
        assert temp != null;
    
        user.getInventory().save(targetFormat, temp, to);
        set(getMaxProgress());
        /*
        user.getInventory().save(targetFormat, temp, "%temp");
        handle.addListener((now, max, rel) -> set(now));
        handle.convert(user, "%temp", to, args);
        user.getInventory().delete("%temp");
        set(getMaxProgress());
        */
    }
    
    // MISC
    
    @Override
    public Formatverter clone() {
        return new InventoryFormatverter(sourceFormat, targetFormat);
    }
    
}
