package eisenwave.vv.bukkit.http;

import com.google.common.net.MediaType;
import javafx.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.BiConsumer;

@SuppressWarnings("UnstableApiUsage")
public class MultipartFormData extends ArrayList<Pair<MediaType, byte[]>> {
    
    public MultipartFormData() {
        super(8);
    }
    
    public void add(MediaType type, byte[] bytes, @Nullable String filename) {
        add(new Pair<>(type, bytes));
    }
    
    public MediaType getType(int index) {
        return get(index).getKey();
    }
    
    public byte[] getBytes(int index) {
        return get(index).getValue();
    }
    
    public void forEach(BiConsumer<MediaType, byte[]> action) {
        forEach(pair -> action.accept(pair.getKey(), pair.getValue()));
    }
    
}
