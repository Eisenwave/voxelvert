package eisenwave.vv.bukkit.http;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class DeserializerMultipartFormTest {
    
    @Test
    public void test_Resource() throws IOException {
        MultipartFormEntry[] result = new DeserializerMultipartForm("BOUNDARY").fromResource(getClass(), "multipart.txt");
        
        assertEquals(result.length, 1);
        assertEquals("text/plain", result[0].getType().toString());
        assertEquals("file", result[0].getName());
        assertEquals("multipart.txt", result[0].getFilename());
        assertEquals(
            "abcdef\\r\\nakgaiwhgp\\r\\n--BOUND\\r\\n--BOUND\\r\\n--BOUNDAR\\r\\nY\\r\\n",
            new String(result[0].getData()).replace("\r", "\\r").replace("\n", "\\n"));
    }
    
    /*
    @Test
    public void test_Random() throws IOException {
        MultipartFormEntry[] result = new DeserializerMultipartForm("BOUNDARY").fromResource(getClass(), "multipart.txt");
        
        assertEquals(result.length, 1);
        assertEquals("text/plain", result[0].getType().toString());
        assertEquals("file", result[0].getName());
        assertEquals("multipart.txt", result[0].getFilename());
        assertEquals(
            "abcdef\\r\\nakgaiwhgp\\r\\n--BOUND\\r\\n--BOUND\\r\\n--BOUNDAR\\r\\nY\\r\\n",
            new String(result[0].getData()).replace("\r", "\\r").replace("\n", "\\n"));
    }
    */
    
}
