package eisenwave.vv.bukkit.cmd;

import eisenwave.vv.ui.cmd.*;
import eisenwave.vv.ui.fmtvert.Option;
import eisenwave.torrens.io.TextDeserializerPlain;
import eisenwave.vv.bukkit.VoxelVertPlugin;
import eisenwave.vv.bukkit.async.VoxelVertQueue;
import eisenwave.vv.ui.user.VVInventory;
import eisenwave.vv.bukkit.util.CommandUtil;
import eisenwave.vv.object.Language;
import eisenwave.vv.ui.user.VVUser;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.*;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

public class CmdConvert extends VoxelVertCommand implements VVInitializer {
    
    private final static Pattern MOSTLY_ALPHANUMERIC = Pattern.compile("[a-zA-Z0-9._/#]+");
    
    private final FormatverterInitializer handle = new FormatverterInitializer();
    
    public CmdConvert(@NotNull VoxelVertPlugin plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "convert";
    }
    
    @Override
    public String getUsage() {
        return "<input> <output> [options] OR /convert help";
    }
    
    @Override
    public boolean onCommand(CommandSender sender, VVUser user, String[] args) {
        VoxelVertQueue queue = voxelVert.getQueue();
        //VVInventory inv = user.getInventory();
        
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            try {
                String[] help = new TextDeserializerPlain().fromResource(getClass(), "help_pages/convert.txt");
                for (String line : help)
                    sender.sendMessage(CommandUtil.chatColors(line));
                
            } catch (IOException e) {
                user.printLocalized("cmd.convert.err.no_help", e.getClass().getSimpleName());
                return true;
            }
            
            return true;
        }
        else if (args.length < 2) {
            return false;
        }
        
        final CommandCall call;
        try {
            call = new CommandCall().setStrictOrder(true).setStrictKwArgs(true);
            handle.getAcceptedOptions().forEach(call::addKeyword);
            call.parse(args);
        } catch (IllegalArgumentException ex) {
            user.errorLocalized("cmd.convert.err.parse", ex.getMessage());
            ex.printStackTrace();
            return true;
        }
        
        final VoxelVertTask task;
        try {
            task = execute(user, call);
        } catch (VVInitializerException e) {
            user.errorLocalized("cmd.convert.err.init", e.getMessage());
            return true;
        }
        
        if (!queue.isEmpty())
            user.printLocalized("cmd.convert.queue.full", queue.size());
        
        queue.add(task);
        return true;
    }
    
    @Override
    public Set<Option> getAcceptedOptions() {
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
        if (!MOSTLY_ALPHANUMERIC.matcher(out).matches())
            throw new VVInitializerException(lang.get("cmd.convert.err.out_alphanumeric"));
        if (in.startsWith("/") || out.startsWith("/"))
            throw new VVInitializerException(lang.get("error.path_absolute"));
        if (in.startsWith(".") || out.startsWith("."))
            throw new VVInitializerException(lang.get("error.path_hidden"));
        
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
