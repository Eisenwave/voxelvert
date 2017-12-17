package eisenwave.vv.ui.user.shell;

import eisenwave.spatium.util.Strings;
import eisenwave.vv.ui.user.VVInventory;
import eisenwave.vv.ui.user.VVInventoryImpl;
import eisenwave.vv.ui.VoxelVert;
import eisenwave.vv.ui.cmd.ClsvertLogger;
import eisenwave.vv.ui.user.VVUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

public class ShellVVUser implements VVUser {
    
    @NotNull
    private final VoxelVert vv;
    private final VVInventory inventory;
    private final Logger logger;
    
    private String lastUpdate = null;
    
    public ShellVVUser(@NotNull ShellVoxelVert vv) {
        this.vv = vv;
        this.inventory = new VVInventoryImpl(this, vv.getDirectory());
        this.logger = Logger.getLogger("vv.convert");
        
        logger.addHandler(new ClsvertLogger(this));
        logger.setUseParentHandlers(false);
        //logger.setParent(null);
        //System.out.println(logger.getHandlers().length+" LOGGERS");
    }
    
    @NotNull
    @Override
    public VoxelVert getVoxelVert() {
        return vv;
    }
    
    @NotNull
    @Override
    public VVInventory getInventory() {
        return inventory;
    }
    
    @Override
    public String getName() {
        return "SHELL";
    }
    
    @Override
    public Logger getLogger() {
        return logger;
    }
    
    @Override
    public boolean acceptsUpdates() {
        return true;
    }
    
    @Override
    public void print(@NotNull String msg) {
        String update = lastUpdate;
        eraseUpdate();
        System.out.println(msg);
        if (update != null) update(update);
    }
    
    @Override
    public void error(@NotNull String err) {
        System.err.println(err);
    }
    
    @Override
    public void printRaw(String raw) {
        print(raw);
    }
    
    @Override
    public void update(@Nullable String msg) {
        eraseUpdate();
        if (msg != null) System.out.print(msg);
        lastUpdate = msg;
    }
    
    private boolean eraseUpdate() {
        if (lastUpdate != null) {
            System.out.print('\r' + Strings.repeat(' ', lastUpdate.length()) + "\r");
            lastUpdate = null;
            return true;
        }
        
        return false;
    }
    
    /*
    @Override
    public BlockSet getSelection() {
        throw new UnsupportedOperationException("shell user can not select blocks");
    }
    */
    
}
