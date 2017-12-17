package eisenwave.vv.bukkit.browser;

import eisenwave.vv.bukkit.util.PathUtil;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class LocalFileBrowserTest {
    
    private final static Path
        INITIAL_PATH = Paths.get("/home/user"),
        TEMP_PATH = Paths.get(".temp");
    
    @Test
    public void getDirectory() throws Exception {
        FileBrowser browser = new LocalFileBrowser(INITIAL_PATH);
        assertEquals(INITIAL_PATH, browser.getDirectory());
    }
    
    @Test
    public void listDirectoryNames() throws Exception {
    }
    
    @Test
    public void listFileNames() throws Exception {
    }
    
    @Test
    public void changeDirectory() throws Exception {
        FileBrowser browser = setUp();
        assertEquals(PathUtil.concat(INITIAL_PATH, TEMP_PATH), browser.getDirectory());
    }
    
    @Test
    public void copy() throws Exception {
        Path source = Paths.get("source.file");
        Path target = Paths.get("target.file");
        
        FileBrowser browser = setUp();
        browser.createFile(source);
        browser.copy(source, target);
        browser.delete(target);
    }
    
    @Test
    public void move() throws Exception {
        Path source = Paths.get("source.file");
        Path target = Paths.get("target.file");
        
        FileBrowser browser = setUp();
        browser.createFile(source);
        browser.move(source, target);
        browser.delete(target);
    }
    
    @Test
    public void delete() throws Exception {
        Path path = Paths.get("testFile.ext");
        
        FileBrowser browser = setUp();
        browser.createFile(path);
        assertTrue(browser.delete(path));
    }
    
    private FileBrowser setUp() throws IOException {
        FileBrowser browser = new LocalFileBrowser(INITIAL_PATH);
        browser.makeDirectories(TEMP_PATH);
        browser.changeDirectory(TEMP_PATH);
        return browser;
    }
    
    @After
    public void after() throws IOException {
        Path temp = PathUtil.concat(INITIAL_PATH, TEMP_PATH);
        if (!Files.exists(temp)) return;
        
        Files.list(temp).forEach(path -> {
            try {
                Files.delete(path);
            } catch (IOException ignored) {}
        });
        //noinspection ResultOfMethodCallIgnored
        temp.toFile().delete();
    }
    
}
