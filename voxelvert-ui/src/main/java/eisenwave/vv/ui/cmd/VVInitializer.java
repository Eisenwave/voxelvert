package eisenwave.vv.ui.cmd;

import eisenwave.vv.ui.fmtvert.Option;
import eisenwave.vv.ui.user.VVUser;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Platform independent VoxelVert command.
 */
public interface VVInitializer {
    
    /**
     * Returns all accepted command call options for this initializer in a safely mutable set. Mutating the set will
     * have no effect on the accepted options of the initializer.
     *
     * @return all accepted command call options
     */
    abstract Set<Option> getAcceptedOptions();
    
    /**
     * Executes a platform independent command.
     *
     * @param user the command user
     * @param args the pre-parsed arguments
     */
    abstract VoxelVertTask execute(@NotNull VVUser user, @NotNull CommandCall args) throws VVInitializerException;
    
    /*
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
     
    
    abstract String[] onTabComplete(VVUser user, String label, Object[] args);
    */
    
    /*
    @Override
    default boolean execute(VVUser user, Command command, String label, String[] args) {
        Object[] parsed;
        try {
            parsed = parse(args);
        } catch (IllegalArgumentException ex) {
            sender.print(ChatColor.RED+"Parsing Error: "+ex.getMessage());
            return true;
        }
        
        execute(user, command, label, parsed);
        return true;
    }
    
    @Override
    default List<String> onTabComplete(VVUser user, Command command, String label, String[] args) {
        String[] suggestions = onTabComplete(sender, command, label, parse(args));
        List<String> result = new ArrayList<>();
        
        for (String suggestion : suggestions) {
            if (args[args.length-1].startsWith(suggestion))
                result.add(suggestion);
        }
        
        return result;
    }
    */
    
}
