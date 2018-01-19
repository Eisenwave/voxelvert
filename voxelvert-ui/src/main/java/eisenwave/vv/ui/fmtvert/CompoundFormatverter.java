package eisenwave.vv.ui.fmtvert;

import eisenwave.spatium.util.PrimArrays;
import eisenwave.vv.ui.user.VVUser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CompoundFormatverter extends Formatverter {
    
    private final Formatverter[] fv;
    
    private final Option[] mandatory, optional, all;
    private final int maxProgress;
    
    public CompoundFormatverter(Formatverter... fv) {
        if (fv.length < 2)
            throw new IllegalArgumentException("compound Formatverter requires at least 2 Formatverters");
        this.fv = fv;
        
        this.mandatory = concatAll(Option.class, Formatverter::getMandatoryOptions, fv);
        this.optional = concatAll(Option.class, Formatverter::getOptionalOptions, fv);
        this.all = concatAll(Option.class, Formatverter::getAllOptions, fv);
        this.maxProgress = Arrays.stream(fv).mapToInt(Formatverter::getMaxProgress).sum();
    }
    
    @Override
    public int getMaxProgress() {
        return maxProgress;
    }
    
    @Override
    public Option[] getMandatoryOptions() {
        return Arrays.copyOf(mandatory, mandatory.length);
    }
    
    @Override
    public Option[] getOptionalOptions() {
        return Arrays.copyOf(optional, optional.length);
    }
    
    @Override
    public Option[] getAllOptions() {
        return Arrays.copyOf(all, all.length);
    }
    
    @Override
    public void convert(VVUser user, String from, String to, Map<String, String> args) throws Exception {
        ProgressListener listener1 = (now, max, relative) -> set(now);
        
        Formatverter first = fv[0];
        first.addListener(listener1);
        first.convert(user, from, "%temp", args);
        first.removeListener(listener1);
        
        int lastIndex = fv.length - 1;
        for (int i = 1, offset = 0; i <= lastIndex; i++) {
            final int offsetCopy = offset;
            ProgressListener l = (now, max, relative) -> this.set(offsetCopy + now);
            Formatverter intermediary = fv[i];
            intermediary.addListener(l);
            
            if (i == lastIndex)
                intermediary.convert(user, "%temp", to, args);
            else
                intermediary.convert(user, "%temp", "%temp", args);
            
            intermediary.removeListener(l);
            offset += intermediary.getMaxProgress();
        }
        
        user.getInventory().delete("%temp");
    }
    
    @SuppressWarnings("unchecked")
    private static <T> T[] concatAll(Class<T> type, Function<Formatverter, T[]> getArray,
                                     Formatverter... objects) {
        List<T> result = new ArrayList<>();
        for (Formatverter fv : objects)
            result.addAll(Arrays.asList(getArray.apply(fv)));
        
        return result.toArray((T[]) Array.newInstance(type, result.size()));
    }
    
}
