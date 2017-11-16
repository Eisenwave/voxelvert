package org.eisenwave.vv.io;

import eisenwave.commons.io.TextDeserializer;
import org.eisenwave.vv.object.Language;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DeserializerLanguage implements TextDeserializer<Language> {
    
    private final String name;
    
    public DeserializerLanguage(@NotNull String name) {
        this.name = name;
    }
    
    @NotNull
    @Override
    public Language fromReader(Reader reader) throws IOException {
        Properties properties = new Properties();
        properties.load(reader);
        
        return new Language(name, vocabOf(properties));
    }
    
    @NotNull
    @Override
    public Language fromStream(InputStream stream) throws IOException {
        Properties properties = new Properties();
        properties.load(stream);
        
        return new Language(name, vocabOf(properties));
    }
    
    private static Map<String, String> vocabOf(Properties properties) {
        Map<String, String> map = new HashMap<>();
        properties.stringPropertyNames().forEach(name -> map.put(name, properties.getProperty(name)));
        
        return map;
    }
    
}
