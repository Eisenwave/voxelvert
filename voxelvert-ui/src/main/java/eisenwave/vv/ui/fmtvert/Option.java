package eisenwave.vv.ui.fmtvert;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Option {
    
    private final String id;
    private final String[] aliases;
    
    public Option(String id, String... aliases) {
        this.id = id;
        this.aliases = Arrays.copyOf(aliases, aliases.length);
    }
    
    public Option(String id) {
        this.id = id;
        this.aliases = new String[0];
    }
    
    public String getId() {
        return id;
    }
    
    public boolean matches(String option) {
        if (id.equals(option)) return true;
        for (String alias : aliases)
            if (alias.equals(option))
                return true;
        return false;
    }
    
    public Set<String> getAliases() {
        return new HashSet<>(Arrays.asList(aliases));
    }
    
    // MISC
    
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
