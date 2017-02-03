package net.grian.vv.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface ParsingCommand extends CommandExecutor, TabCompleter, ParseStrategy {
    
    /**
     * Executes the command with pre-parsed arguments.
     *
     * @param sender the command sender
     * @param command the command description
     * @param label the label (the command name or an alias)
     * @param args the pre-parsed arguments
     */
    abstract void onCommand(CommandSender sender, Command command, String label, Object[] args);
    
    /**
     * <p>
     *     Tab-completes the command with pre-parsed arguments.
     * </p>
     * <p>
     *     In this case the result is not a list of strings which will be directly sent to the command sender but
     *     an array of unsorted suggestions based on the final argument of the command.
     * </p>
     * <p>
     *     For example, should the sender attempt to tab-complete a plugin name, the result may be an alphabetically
     *     sorted array of all plugin names.
     * </p>
     * <p>
     *     The {@link #onTabComplete(CommandSender, Command, String, String[])} method will then automatically pick
     *     the results that start with the final argument and return them as a list.
     * </p>
     *
     * @param sender the command sender
     * @param command the command description
     * @param label the label (the command name or an alias)
     * @param args the pre-parsed arguments
     * @return an array containing sorted tab-complete suggestions
     */
    abstract String[] onTabComplete(CommandSender sender, Command command, String label, Object[] args);
    
    @Override
    default boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Object[] parsed;
        try {
            parsed = parse(args);
        } catch (IllegalArgumentException ex) {
            sender.sendMessage(ChatColor.RED+"Parsing Error: "+ex.getMessage());
            return true;
        }
        
        onCommand(sender, command, label, parsed);
        return true;
    }
    
    @Override
    default List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        String[] suggestions = onTabComplete(sender, command, label, parse(args));
        List<String> result = new ArrayList<>();
        
        for (String suggestion : suggestions) {
            if (args[args.length-1].startsWith(suggestion))
                result.add(suggestion);
        }
        
        return result;
    }
    
}
