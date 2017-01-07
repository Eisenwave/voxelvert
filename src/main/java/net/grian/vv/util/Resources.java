package net.grian.vv.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.ZipFile;

public final class Resources {

    private Resources() {}

    public static InputStream getStream(Class<?> clazz, String path) {
        return clazz.getClassLoader().getResourceAsStream(path);
    }

    public static File getFile(Class<?> clazz, String path) {
        return new File(clazz.getClassLoader().getResource(path).getFile());
    }

    public static ZipFile getZipFile(Class<?> clazz, String path) throws IOException {
        return new ZipFile(getFile(clazz, path));
    }

    public static URL get(Class<?> clazz, String path) {
        return clazz.getClassLoader().getResource(path);
    }

}
