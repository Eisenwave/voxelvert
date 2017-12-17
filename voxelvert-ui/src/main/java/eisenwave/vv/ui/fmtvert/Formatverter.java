package eisenwave.vv.ui.fmtvert;

import eisenwave.spatium.util.PrimArrays;
import eisenwave.vv.ui.error.FormatverterException;
import eisenwave.vv.ui.user.VVUser;

import java.util.Map;

/**
 * A converter between two formats which can be invoked by a user.
 */
public abstract class Formatverter extends Progress {
    
    /**
     * Returns an array of mandatory options of this Formatverter.
     *
     * @return the names of mandatory parameters
     */
    public Option[] getMandatoryOptions() {
        return new Option[0];
    }
    
    /**
     * Returns an array of optional options of this Formatverter.
     *
     * @return the names of optional options
     */
    public Option[] getOptionalOptions() {
        return new Option[0];
    }
    
    public Option[] getAllOptions() {
        return PrimArrays.concat(getMandatoryOptions(), getOptionalOptions());
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
     */
    public abstract void convert(VVUser user, String from, String to, Map<String, String> args)
        throws Exception;
    
    /*
    @Override
    default float getProgress() {
        return (float) getRawProgress() / getMaxProgress();
    }
    
    default int getMaxProgress() {
        return 1;
    }
    
    default int getRawProgress() {
        return 0;
    }
    */
    
}
