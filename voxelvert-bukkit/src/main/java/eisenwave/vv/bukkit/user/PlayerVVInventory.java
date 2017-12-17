package eisenwave.vv.bukkit.user;

import eisenwave.vv.ui.fmtvert.Format;
import eisenwave.vv.ui.user.VVInventoryImpl;
import eisenwave.torrens.schematic.BlockStructure;
import eisenwave.vv.ui.user.VVInventoryVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.zip.ZipFile;

public class PlayerVVInventory extends VVInventoryImpl {
    
    public PlayerVVInventory(@NotNull PlayerVVUser owner, @NotNull File directory) {
        super(owner, directory);
    
        SelectionVariable s = new SelectionVariable();
        //this.variables.put("#sel", s);
        this.variables.put("#selection", s);
        
        DefaultResourcePackVariable d = new DefaultResourcePackVariable();
        this.variables.put("#defaultpack", d);
    }
    
    @NotNull
    @Override
    public PlayerVVUser getOwner() {
        return (PlayerVVUser) super.getOwner();
    }
    
    /*
    @Override
    public Object load(@NotNull Format format, @NotNull String name) throws IOException {
        if (format.equals(FormatverterInjector.BLOCKS_FORMAT)) {
            PlayerVVUser player = getOwner();
            return player.getBlocks();
        }
        else return super.load(format, name);
    }
    
    @Override
    public boolean contains(@Nullable Format format, @NotNull String name) {
        if (name.startsWith("#"))
            return variables.containsKey(name.substring(1));
        if (format == null)
            return super.contains(null, name);
        else
            return super.contains(format, name);
    }
    */
    
    private class SelectionVariable implements VVInventoryVariable<BlockStructure> {
    
        @Override
        public Format getFormat() {
            return Format.BLOCK_ARRAY;
        }
    
        @Nullable
        @Override
        public BlockStructure get() {
            return getOwner().getBlocks();
        }
    
        @Override
        public boolean isSet() {
            return getOwner().getBlockSelection() != null;
        }
        
    }
    
    private static class DefaultResourcePackVariable implements VVInventoryVariable<ZipFile> {
    
        @Nullable
        private final URL url;
    
        public DefaultResourcePackVariable() {
            this.url = getClass().getClassLoader().getResource("resourcepacks/default.zip");
        }
        
        @Override
        public Format getFormat() {
            return Format.RESOURCE_PACK;
        }
        
        @Nullable
        @Override
        public ZipFile get() {
            if (url == null) throw new IllegalStateException();
            try {
                return new ZipFile(new File(url.getFile()));
            } catch (IOException e) {
                return null;
            }
        }
    
        @Override
        public boolean isSet() {
            return url != null;
        }
        
    }
    
}
