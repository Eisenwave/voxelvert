package eisenwave.vv.bukkit.http;

import eisenwave.vv.bukkit.util.HttpHeaders;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.*;

public class DeserializerHttpHeadersTest {
    
    @Test
    public void fromReader() throws IOException {
        String testString =
            "Key1:   stuff  \n" +
                "key_2 : more stuff; even more stuff\n" +
                "key.3: foo; bar=1, foobar=barfoo\n";
        HttpHeaders headers = new DeserializerHttpHeaders().fromString(testString);
        
        assertEquals("stuff", headers.getFirst("Key1"));
        assertEquals(
            Arrays.asList("more stuff", "even more stuff"),
            headers.get("key_2"));
        
        assertEquals(
            Arrays.asList("foo", "bar=1, foobar=barfoo"),
            headers.get("key.3"));
    }
    
}
