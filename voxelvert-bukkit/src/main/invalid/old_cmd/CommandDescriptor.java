package eisenwave.vv.bukkit.cmd;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    @NotNull
    public String getName() {
        return name;
    }
    
    /**
     * Returns an array of of {@link NotNull} aliases for this command.
     *
     * @return the command aliases
     */
    @NotNull
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
    
    public void setAliases(@NotNull String[] aliases) {
        //Arguments.requireAllNotNull(aliases);
        this.aliases = Arrays.copyOf(aliases, aliases.length);
    }
    
    public void setPermission(@Nullable String permission) {
        this.permission = permission;
    }
    
    public void setPermissionMessage(@Nullable String message) {
        this.permMessage = message;
    }
    
}
