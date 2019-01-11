package eisenwave.vv.bukkit.http;

import eisenwave.torrens.error.FileSyntaxException;
import eisenwave.torrens.io.TextDeserializer;
import eisenwave.vv.bukkit.util.HttpHeaders;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class DeserializerHttpHeaders implements TextDeserializer<HttpHeaders> {
    
    @NotNull
    @Override
    public HttpHeaders fromReader(Reader reader) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        BufferedReader buffReader = new BufferedReader(reader);
        Iterator<String> iter = buffReader.lines().iterator();
        while (iter.hasNext()) {
            String line = iter.next();
            String[] keyVal = line.split(":", 2);
            if (keyVal.length < 2)
                throw new FileSyntaxException("illegal line \"" + line + "\"");
            
            String[] valueSplit = keyVal[1].split(";", 2);
            List<String> values = new ArrayList<>();
            values.add(valueSplit[0].trim());
            if (valueSplit.length > 1)
                for (String value : valueSplit[1].split(";"))
                    values.add(value.trim());
            headers.put(keyVal[0].trim(), values);
        }
        
        return headers;
    }
    
    public static List<String> deserializeMultiSemicolon(String line) {
        String[] keyVal = line.split(":", 2);
        if (keyVal.length < 2)
            throw new IllegalArgumentException("illegal line \"" + line + "\"");
        
        String[] valueSplit = keyVal[1].split(";");
        List<String> result = new ArrayList<>();
        result.add(keyVal[0].trim());
        
        for (String value : valueSplit)
            result.add(value.trim());
        
        return result;
    }
    
}
