package eisenwave.vv.ui.fmtvert;

import eisenwave.vv.ui.user.VVUser;
import eisenwave.vv.ui.error.*;
import eisenwave.vv.ui.util.Sets;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * A converter between two formats which can be invoked by a user.
 */
public abstract class Formatverter extends Progress {
    
    /**
     * Returns an array of mandatory options of this Formatverter.
     *
     * @return the names of mandatory parameters
     */
    @NotNull
    public Set<Option> getMandatoryOptions() {
        return Collections.emptySet();
    }
    
    /**
     * Returns an array of optional options of this Formatverter.
     *
     * @return the names of optional options
     */
    @NotNull
    public Set<Option> getOptionalOptions() {
        return Collections.emptySet();
    }
    
    @NotNull
    public Set<Option> getAllOptions() {
        return Sets.union(getMandatoryOptions(), getOptionalOptions());
    }
    
    @Override
    public int getMaxProgress() {
        return 1;
    }
    
    /**
     * Executes the conversion between formats.
     *
     * @param user the voxelvert user who converts
     * @param from the name of the object which to convert from
     * @param to the name of the object to which to convert
     * @param args the additional arguments for the conversion
     * @throws FormatverterArgumentException if a mandatory argument is missing or there is some other issue
     * @throws FormatverterException if a known error occurs in the formatverter
     * @throws Exception if an unknown error such as an I/O error occurs in the formatverter
     */
    public abstract void convert(VVUser user, String from, String to, Map<String, String> args) throws Exception;
    
}
