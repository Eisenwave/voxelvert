package eisenwave.vv.bukkit.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public final class DebugUtil {
    
    public static boolean delete(String file) {
        return new File(file).delete();
    }
    
    public static void write(String file, String text) {
        try (FileWriter writer = new FileWriter(new File(file))) {
            writer.write(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void append(String file, String text) {
        try (FileWriter writer = new FileWriter(new File(file), true)) {
            writer.write(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
