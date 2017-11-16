package org.eisenwave.vv.bukkit.cmd;

import net.grian.torrens.io.TextDeserializerPlain;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.eisenwave.vv.bukkit.VoxelVertPlugin;
import org.eisenwave.vv.bukkit.async.VoxelVertQueue;
import org.eisenwave.vv.bukkit.user.BukkitVoxelVert;
import org.eisenwave.vv.ui.user.VVInventory;
import org.eisenwave.vv.bukkit.util.CommandUtil;
import org.eisenwave.vv.object.Language;
import org.eisenwave.vv.ui.cmd.*;
import org.eisenwave.vv.ui.user.VVUser;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;

public class CmdConvert implements CommandExecutor, VVInitializer {
    
    /*
    private final static String[] OPTIONS = {
        "c",
        "C", "crop",
        "full_blocks",
        "dir",
        "v", "verbose"
    };
    */
    
    @RegExp
    private final static String MOSTLY_ALPHANUMERIC = "[a-zA-Z0-9._/#]+";
    
    private final static String
        USAGE = CommandUtil.chatColors("&cUsage: /convert <input> <output> [options] OR /convert help");
    
    
    
    private final VoxelVertPlugin plugin;
    private final FormatverterInitializer handle = new FormatverterInitializer();
    
    public CmdConvert(@NotNull VoxelVertPlugin plugin) {
        Objects.requireNonNull(plugin, "plugin");
        
        this.plugin = plugin;
        
        //this.sysVars.put("#selection", Format.BLOCK_ARRAY);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        command.setUsage(USAGE);
        BukkitVoxelVert vv = plugin.getVoxelVert();
        Language lang = vv.getLanguage();
        VoxelVertQueue queue = vv.getQueue();
        VVUser user = CommandUtil.userOf(vv, sender);
        //VVInventory inv = user.getInventory();
        
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            try {
                String[] help = new TextDeserializerPlain().fromResource(getClass(), "help_pages/convert.txt");
                for (String line : help)
                    sender.sendMessage(CommandUtil.chatColors(line));
                
            } catch (IOException e) {
                user.print(lang.get("cmd.convert.err.no_help"), e.getClass().getSimpleName());
                return true;
            }
            
            return true;
        }
        else if (args.length < 2) {
            return false;
        }
    
        CommandCall call;
        try {
            call = new CommandCall()
                .setStrictOrder(true)
                .setStrictKwArgs(true)
                .addValidKwArgs(handle.getAcceptedOptions())
                .parse(args);
        } catch (IllegalArgumentException ex) {
            user.error(lang.get("cmd.convert.err.parse"), ex.getMessage());
            return true;
        }
        
        final VoxelVertTask task;
        try {
            task = execute(user, call);
        } catch (VVInitializerException e) {
            user.error(lang.get("cmd.convert.err.init"), e.getMessage());
            return true;
        }
    
        if (!queue.isEmpty())
            user.print(lang.get("cmd.convert.queue.full"), queue.size());
    
        queue.add(task);
        return true;
    }
    
    @Override
    public Set<String> getAcceptedOptions() {
        return handle.getAcceptedOptions();
    }
    
    @Override
    public VoxelVertTask execute(@NotNull VVUser user, @NotNull CommandCall args) throws VVInitializerException {
        Language lang = user.getVoxelVert().getLanguage();
        VVInventory inv = user.getInventory();
        
        String in = args.get(0), out = args.get(1);
        
        if (in.isEmpty())
            throw new VVInitializerException(lang.get("cmd.convert.err.in_empty"));
        if (out.isEmpty())
            throw new VVInitializerException(lang.get("cmd.convert.err.out_empty"));
        if (!out.matches(MOSTLY_ALPHANUMERIC))
            throw new VVInitializerException(lang.get("cmd.convert.err.out_alphanumeric"));
        if (in.startsWith("/") || out.startsWith("/"))
            throw new VVInitializerException(lang.get("err.path_absolute"));
        if (in.startsWith(".") || out.startsWith("."))
            throw new VVInitializerException(lang.get("err.path_hidden"));
    
        File dir = inv.getDirectory();
        if (!dir.exists() && !dir.mkdirs())
            throw new VVInitializerException(lang.get("cmd.convert.err.dir"), dir);
    
        /* this verification is now done by the FormatverterInitializer itself
        if (inv.hasVariable(in)) {
        
            if (args.hasKeyword("i"))
                throw new VVInitializerException(lang.get("cmd.convert.err.var.in_format_conflict"));
        
            VVInventoryVariable var = inv.getVariable(in);
            assert var != null;
        
            if (!var.isSet())
                throw new VVInitializerException(lang.get("cmd.convert.err.var.unset"), in);
        
            // append the format of the system variable so that the right FormatverterInitializer is being picked
            // the value of the system variable should then be recognized by the VVInventory of a VVUser
            args = PrimArrays.concat(args, new String[] {"-i", var.getFormat().getId()});
        }*/
    
        return handle.execute(user, args);
    }
    
}
