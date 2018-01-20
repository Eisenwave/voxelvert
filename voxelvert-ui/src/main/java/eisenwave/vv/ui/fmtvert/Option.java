package eisenwave.vv.ui.fmtvert;

import eisenwave.vv.ui.util.Sets;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Option {
    
    private final String id;
    private final Set<String> aliases;
    
    public Option(String id, String... aliases) {
        this.id = id;
        this.aliases = Sets.ofArray(aliases);
    }
    
    public Option(String id) {
        this.id = id;
        this.aliases = Collections.emptySet();
    }
    
    public String getId() {
        return id;
    }
    
    public boolean matches(String option) {
        return id.equals(option)
            || aliases.contains(option);
    }
    
    public Set<String> getAliases() {
        return Collections.unmodifiableSet(aliases);
    }
    
    // MISC
    
    @Override
    public String toString() {
        return id;
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Option && equals((Option) obj);
    }
    
    public boolean equals(Option option) {
        return this.id.equals(option.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
}
