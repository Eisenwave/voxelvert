package net.grian.vv.cmd;

import net.grian.vv.util.Arguments;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;

public class CommandDescriptor {
    
    private final String name;
    
    private String[] aliases = new String[] {};
    private String permission;
    private String permMessage;
    
    public CommandDescriptor(String name) {
        this.name = Objects.requireNonNull(name);
    }
    
    /**
     * Returns the command name.
     *
     * @return the command name
     */
    @Nonnull
    public String getName() {
        return name;
    }
    
    /**
     * Returns an array of of {@link Nonnull} aliases for this command.
     *
     * @return the command aliases
     */
    @Nonnull
    public String[] getAliases() {
        return aliases;
    }
    
    /**
     * Returns the permission required to run this command.
     *
     * @return the permission required to run this command
     */
    @Nullable
    public String getPermission() {
        return permission;
    }
    
    /**
     * Returns the message sent to the player when they don't have the permission requried to run this command.
     *
     * @return the permission message
     */
    @Nullable
    public String getPermMessage() {
        return permMessage;
    }
    
    public void setAliases(@Nonnull String[] aliases) {
        Arguments.requireAllNonnull(aliases);
        this.aliases = Arrays.copyOf(aliases, aliases.length);
    }
    
    public void setPermission(@Nullable String permission) {
        this.permission = permission;
    }
    
    public void setPermissionMessage(@Nullable String message) {
        this.permMessage = message;
    }
    
}
