package eisenwave.vv.ui.fmtvert;

import eisenwave.vv.ui.user.VVUser;
import eisenwave.vv.ui.util.Sets;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CompoundFormatverter extends Formatverter {
    
    private final Formatverter[] fv;
    
    private final Set<Option> mandatory, optional, all;
    private final int maxProgress;
    
    public CompoundFormatverter(Formatverter... fv) {
        if (fv.length < 2)
            throw new IllegalArgumentException("compound Formatverter requires at least 2 Formatverters");
        this.fv = fv;
    
        mandatory = Sets.union(Arrays.stream(fv).map(Formatverter::getMandatoryOptions).collect(Collectors.toList()));
        optional = Sets.union(Arrays.stream(fv).map(Formatverter::getOptionalOptions).collect(Collectors.toList()));
        all = Sets.union(Arrays.stream(fv).map(Formatverter::getAllOptions).collect(Collectors.toList()));
        maxProgress = Arrays.stream(fv).mapToInt(Formatverter::getMaxProgress).sum();
    }
    
    @Override
    public int getMaxProgress() {
        return maxProgress;
    }
    
    @Override
    public Set<Option> getMandatoryOptions() {
        return Collections.unmodifiableSet(mandatory);
    }
    
    @Override
    public Set<Option> getOptionalOptions() {
        return Collections.unmodifiableSet(optional);
    }
    
    @Override
    public Set<Option> getAllOptions() {
        return Collections.unmodifiableSet(all);
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
    
        if (!to.equals("%temp"))
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
