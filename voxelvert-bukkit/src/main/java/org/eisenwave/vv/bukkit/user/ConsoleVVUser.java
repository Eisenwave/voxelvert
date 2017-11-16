package org.eisenwave.vv.bukkit.user;

import org.bukkit.command.CommandSender;
import org.eisenwave.vv.bukkit.util.CommandUtil;
import org.eisenwave.vv.object.Language;
import org.eisenwave.vv.ui.VoxelVert;
import org.eisenwave.vv.ui.user.VVInventory;
import org.eisenwave.vv.ui.user.VVInventoryImpl;
import org.eisenwave.vv.ui.user.VVUser;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;
import java.util.logging.Logger;

public class ConsoleVVUser implements VVUser {
    
    private final BukkitVoxelVert vv;
    private final CommandSender sender;
    private final VVInventory inv;
    
    private final String msgFormat, errFormat;
    
    public ConsoleVVUser(@NotNull BukkitVoxelVert vv, @NotNull CommandSender sender) {
        this.vv = Objects.requireNonNull(vv);
        this.sender = Objects.requireNonNull(sender);
        
        File userDir = new File(vv.getDirectory(), "users");
        File dir = new File(userDir, "console");
        this.inv = new VVInventoryImpl(this, dir);
        
        Language lang = vv.getLanguage();
        this.msgFormat = lang.get("user.msg");
        this.errFormat = lang.get("user.err");
    }
    
    public CommandSender getHandle() {
        return sender;
    }
    
    @NotNull
    @Override
    public VoxelVert getVoxelVert() {
        return vv;
    }
    
    @Override
    public String getName() {
        return sender.getName();
    }
    
    @Override
    public void print(String msg) {
        sender.sendMessage( CommandUtil.chatColors(String.format(msgFormat, msg)) );
    }
    
    @Override
    public void error(String err) {
        sender.sendMessage( CommandUtil.chatColors(String.format(errFormat, err)) );
    }
    
    @Override
    public boolean acceptsUpdates() {
        return false;
    }
    
    @Override
    public void update(String msg) {}
    
    @Override
    public Logger getLogger() {
        return null;
    }
    
    @Override
    public VVInventory getInventory() {
        return inv;
    }
    
}
