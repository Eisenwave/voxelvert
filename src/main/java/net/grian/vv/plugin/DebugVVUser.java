package net.grian.vv.plugin;

import net.grian.vv.core.BlockSet;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;

public class DebugVVUser extends AbstractVVUser {
    
    @Override
    public File getFileDirectory() throws IOException {
        return new File("D:\\Users\\Jan\\Desktop\\SERVER\\SERVERS\\TEST\\plugins\\VoxelVert\\users\\debug");
    }
    
    @Override
    public String getName() {
        return "DEBUG_USER";
    }
    
    @Override
    public BlockSet getSelection() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public World getWorld() {
        throw new UnsupportedOperationException();
    }
    
}
