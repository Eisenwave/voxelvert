package eisenwave.vv.bukkit.util;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class PathUtil {
    
    private PathUtil() {}
    
    public static Path concat(Path a, Path b) {
        return b.isAbsolute()? b : Paths.get(a.toString(), b.toString());
    }
    
    public static Path concat(Path a, String b) {
        return Paths.get(a.toString(), b);
    }
    
}
