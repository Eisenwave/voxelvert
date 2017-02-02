package net.grian.vv.cmd;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.io.PrintStream;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Implementation of {@link CommandSender} which allows for testing commands using artificial senders.
 */
public class DebugCommandSender implements CommandSender {
    
    private final PermissibleBase permissions = new PermissibleBase(this);
    private boolean op = true;
    
    @Nonnull
    private final Consumer<String> msgHandler;
    
    public DebugCommandSender(Consumer<String> msgHandler) {
        this.msgHandler = Objects.requireNonNull(msgHandler);
    }
    
    public DebugCommandSender(PrintStream out) {
        this(out::println);
    }
    
    @Override
    public void sendMessage(String message) {
        msgHandler.accept(message);
    }
    
    @Override
    public void sendMessage(String... messages) {
        for (String msg : messages)
            sendMessage(msg);
    }
    
    @Override
    public Server getServer() {
        return Bukkit.getServer();
    }
    
    @Override
    public String getName() {
        return "DEBUG";
    }
    
    @Override
    public boolean isPermissionSet(String name) {
        return permissions.isPermissionSet(name);
    }
    
    @Override
    public boolean isPermissionSet(Permission perm) {
        return permissions.isPermissionSet(perm);
    }
    
    @Override
    public boolean hasPermission(String name) {
        return permissions.hasPermission(name);
    }
    
    @Override
    public boolean hasPermission(Permission perm) {
        return permissions.hasPermission(perm);
    }
    
    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        return permissions.addAttachment(plugin, name, value);
    }
    
    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return permissions.addAttachment(plugin);
    }
    
    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        return permissions.addAttachment(plugin, name, value, ticks);
    }
    
    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        return permissions.addAttachment(plugin, ticks);
    }
    
    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        permissions.removeAttachment(attachment);
    }
    
    @Override
    public void recalculatePermissions() {
        permissions.recalculatePermissions();
    }
    
    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return permissions.getEffectivePermissions();
    }
    
    @Override
    public boolean isOp() {
        return op;
    }
    
    @Override
    public void setOp(boolean value) {
        this.op = value;
    }
    
}
