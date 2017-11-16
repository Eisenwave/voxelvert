package org.eisenwave.vv.ui.user.shell;

import org.eisenwave.vv.object.Language;
import org.eisenwave.vv.io.DeserializerLanguage;
import org.eisenwave.vv.ui.VoxelVert;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class ShellVoxelVert implements VoxelVert {
    
    /*
    private final File jarFile = new java.io.File(getClass()
        .getProtectionDomain()
        .getCodeSource()
        .getLocation()
        .getPath());
    
    private final File jarDir = jarFile.getParentFile();
    */
    
    private final Language lang = loadLang();
    private final File dir = new File(System.getProperty("user.dir"));
    
    @NotNull
    private static Language loadLang() {
        try {
            return new DeserializerLanguage("default/en_us").fromResource(ShellVoxelVert.class, "lang/en_us.lang");
        } catch (IOException ex) {
            ex.printStackTrace();
            return new Language("empty");
        }
    }
    
    @Override
    public File getDirectory() {
        return dir;
    }
    
    @Override
    public Language getLanguage() {
        return lang;
    }
    
}
