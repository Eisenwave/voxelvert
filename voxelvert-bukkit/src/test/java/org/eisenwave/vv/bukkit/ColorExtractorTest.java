package org.eisenwave.vv.bukkit;

import org.apache.commons.io.IOUtils;
import org.bukkit.Material;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;

public class ColorExtractorTest {
    
    public void run() throws IOException {
        String str = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("default.json"));
        
        for (Material m : Material.values()) {
            str = str.replace("\"block\"", "\"id\"");
            str = str.replace("\""+m.toString()+"\"", Integer.toString(m.getId()));
            str = str.replace("\n\n", "\n");
        }
        
        IOUtils.write(str, new FileOutputStream("/home/user/Files/output.json"));
    }
    
}
