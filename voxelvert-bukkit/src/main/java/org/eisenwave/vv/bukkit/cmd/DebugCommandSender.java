package org.eisenwave.vv.bukkit.cmd;

import net.grian.torrens.util.ANSI;
import org.eisenwave.vv.bukkit.util.CommandUtil;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Implementation of {@link CommandSender} which allows for testing commands using artificial senders.
 */
public class DebugCommandSender implements CommandSender {
    
    private boolean op = true;
    
    @Nonnull
    private final Consumer<String> msgHandler;
    
    public DebugCommandSender(Consumer<String> msgHandler) {
        this.msgHandler = Objects.requireNonNull(msgHandler);
    }
    
    public DebugCommandSender(PrintStream out) {
        this(out::println);
    }
    
    public DebugCommandSender(Logger logger) {
        this(logger::info);
    }
    
    public DebugCommandSender() {
        this.msgHandler = msg -> {
            /*StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            for (StackTraceElement e : stack) {
                if (e.isNativeMethod()) break;
                System.out.println(e);
            }*/
            System.out.println(CommandUtil.chatColorToAnsi(msg)+ANSI.RESET);
        };
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
    public void sendLocalizedMessage(String s, Object... objects) {
        sendMessage(s);
    }
    
    @Override
    public void sendPrefixedLocalizedMessage(String s, String s1, Object... objects) {
        sendMessage(s);
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
        return true;
    }
    
    @Override
    public boolean isPermissionSet(Permission perm) {
        return true;
    }
    
    @Override
    public boolean hasPermission(String name) {
        return true;
    }
    
    @Override
    public boolean hasPermission(Permission perm) {
        return true;
    }
    
    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void recalculatePermissions() {}
    
    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return Collections.emptySet();
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
