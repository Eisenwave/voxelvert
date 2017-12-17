package eisenwave.vv.bukkit.browser;

import eisenwave.vv.bukkit.util.PathUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class LocalFileBrowser implements FileBrowser {
    
    private Path wd;
    
    public LocalFileBrowser(@NotNull Path workingDir) {
        if (!Files.isDirectory(workingDir))
            throw new IllegalArgumentException(workingDir + " is not a directory");
        this.wd = workingDir;
    }
    
    @Override
    public Path getDirectory() {
        return wd;
    }
    
    @Override
    public List<Path> listDirectoryNames() throws IOException {
        List<Path> result = new ArrayList<>();
        Files.newDirectoryStream(wd).forEach(result::add);
        return result;
    }
    
    @Override
    public List<Path> listFileNames() throws IOException {
        List<Path> result = new ArrayList<>();
        Files.newDirectoryStream(wd).forEach(result::add);
        return result;
    }
    
    @Override
    public Path changeDirectory(Path directory) {
        if (!Files.isDirectory(actualPath(directory)))
            throw new IllegalArgumentException(directory + " is not a directory");
        return this.wd = PathUtil.concat(wd, directory);
    }
    
    @Override
    public boolean makeDirectory(@NotNull Path path) throws IOException {
        Path actual = actualPath(path);
        if (!Files.exists(actual)) {
            Files.createDirectory(actual);
            return true;
        }
        else return false;
    }
    
    @Override
    public boolean makeDirectories(@NotNull Path path) throws IOException {
        Path actual = actualPath(path);
        if (!Files.exists(actual)) {
            Files.createDirectories(actual);
            return true;
        }
        else return false;
    }
    
    @Override
    public void createFile(@NotNull Path path) throws IOException {
        Files.createFile(actualPath(path));
    }
    
    @Override
    public boolean copy(@NotNull Path source, @NotNull Path target, boolean replace) throws IOException {
        if (replace && Files.exists(target)) {
            return false;
        }
        CopyOption[] options = replace? new CopyOption[] {StandardCopyOption.REPLACE_EXISTING} : new CopyOption[0];
        Files.copy(actualPath(source), actualPath(target), options);
        return true;
    }
    
    @Override
    public boolean move(@NotNull Path source, @NotNull Path target, boolean replace) throws IOException {
        if (replace && Files.exists(target)) {
            return false;
        }
        CopyOption[] options = replace? new CopyOption[] {StandardCopyOption.REPLACE_EXISTING} : new CopyOption[0];
        Files.move(actualPath(source), actualPath(target), options);
        return true;
    }
    
    @Override
    public boolean delete(@NotNull Path path) throws IOException {
        return Files.deleteIfExists(actualPath(path));
    }
    
    private Path actualPath(Path relative) {
        return PathUtil.concat(wd, relative);
    }
    
}
