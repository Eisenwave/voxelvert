package eisenwave.vv.bukkit.util;

import com.google.common.net.MediaType;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public final class HttpUtil {
    
    private final static URL CHECKIP_URL;
    
    static {
        try {
            CHECKIP_URL = new URL("http://checkip.amazonaws.com/");
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
    }
    
    private final static MediaType
        BLOCK_COLOR_TABLE = MediaType.create("application", "x.voxelvert-bct"),
        QUBICLE_BINARY = MediaType.create("application", "qubicle-binary"),
        SCHEMATIC = MediaType.create("application", "schematic"),
        STL = MediaType.create("application", "sla");
    
    private HttpUtil() {}
    
    @NotNull
    public static String getPublicIP() throws IOException {
        try (InputStream stream = CHECKIP_URL.openStream()) {
            byte[] buffer = new byte[64];
            int length = stream.read(buffer) - 1; // checkip returns the IP followed by a line feed
            return new String(Arrays.copyOf(buffer, length));
        }
    }
    
    /**
     * Parses a website query string of form:
     * <blockquote>
     * {@code key1=value1&key2=value2...}
     * </blockquote>
     * If there is no equals sign and value for a key, the key name itself is being used as the value for the key. For
     * example:
     * <blockquote>
     * {@code hidden -> hidden=hidden}
     * </blockquote>
     *
     * @param query the query
     * @return the map of query keys and values
     */
    public static Map<String, String> parseQuery(String query) {
        Map<String, String> result = new HashMap<>();
        if (query == null) return result;
        
        for (String field : query.trim().split("&")) {
            String[] keyVal = field.trim().split("=", 2);
            if (keyVal.length < 2)
                result.put(keyVal[0], keyVal[0]);
            else
                result.put(keyVal[0], keyVal[1]);
        }
        
        return result;
    }
    
    @NotNull
    public static MediaType getMediaType(String extension, boolean custom) {
        switch (extension.toLowerCase()) {
            case "bct": return custom? BLOCK_COLOR_TABLE : MediaType.APPLICATION_BINARY;
            case "png": return MediaType.PNG;
            case "jpg":
            case "jpeg": return MediaType.JPEG;
            case "bmp": return MediaType.BMP;
            case "qef": return MediaType.PLAIN_TEXT_UTF_8;
            case "qb": return custom? QUBICLE_BINARY : MediaType.APPLICATION_BINARY;
            case "zip": return MediaType.ZIP;
            case "schem":
            case "schematic": return custom? SCHEMATIC : MediaType.APPLICATION_BINARY;
            case "stl": return STL;
            case "mtl": return MediaType.PLAIN_TEXT_UTF_8;
            case "obj": return MediaType.PLAIN_TEXT_UTF_8;
            default: return MediaType.APPLICATION_BINARY;
        }
    }
    
    public static String longToBase64(long l) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);
        try {
            dataStream.writeLong(l);
            dataStream.close();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        byte[] result = Base64.getUrlEncoder().withoutPadding().encode(byteStream.toByteArray());
        return new String(result);
    }
    
}
