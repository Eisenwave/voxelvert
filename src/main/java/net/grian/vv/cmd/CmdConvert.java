package net.grian.vv.cmd;

import net.grian.spatium.cache.CacheMath;
import net.grian.spatium.util.Enumerations;
import net.grian.vv.arg.Argument;
import net.grian.vv.arg.ArgumentFactory;
import net.grian.vv.cache.Language;
import net.grian.vv.fmtvert.Format;
import net.grian.vv.fmtvert.Formatverter;
import net.grian.vv.fmtvert.FormatverterFactory;
import net.grian.vv.plugin.UserManager;
import net.grian.vv.plugin.VVUser;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class CmdConvert implements ParsingCommand {
    
    public final static long MAX_TIME = 3500;
    
    @Override
    public Parser[] formatOf(String[] args) {
        if (args.length < 4) return ParseStrategy.IDENTITY.formatOf(args);
        
        if (args.length == 4) {
            Parser<Format> formatParser = Parser.fromEnum(Format.class);
            Parser<String> identityParser = Parser.IDENTITY;
            
            return new Parser[] {formatParser, identityParser, formatParser, identityParser};
        }
        
        return new Parser[0];
    }
    
    @Override
    public void onCommand(CommandSender sender, Command command, String label, Object[] args) {
        String usage = ChatColor.RED+"Usage: /"+label+" ";
        
        if (args.length < 4) {
            String msg = Language.translate("vv:cmd.convert.usage.main");
            sender.sendMessage(usage+msg);
            return;
        }
        
        Format fromType = (Format) args[0];
        String from = (String) args[1];
        Format toType = (Format) args[2];
        String to = (String) args[3];
        
        Formatverter fmtverter = FormatverterFactory.fromFormats(fromType, toType);
        if (fmtverter == null) {
            String msg = Language.translate("vv:cmd.convert.error.invalid_types", fromType, toType);
            sender.sendMessage(ChatColor.RED+"Error: "+msg);
            return;
        }
        
        VVUser user;
        if (sender instanceof Player) {
            user = UserManager.getInstance().getPlayerUser((Player) sender);
        } else if (sender instanceof ConsoleCommandSender) {
            user = UserManager.getInstance().getConsoleUser();
        } else if (sender instanceof DebugCommandSender) {
            user = UserManager.getInstance().getDebugUser();
        } else {
            sender.sendMessage(ChatColor.RED+"Error: Command sender is not a VoxelVert user");
            return;
        }
    
        Argument[] fmtvertArgs = toArguments(args, 4);
        
        Runnable task = () -> {
            try {
                long now = System.currentTimeMillis();
                fmtverter.convert(user, from, to, fmtvertArgs);
                
                long millis = System.currentTimeMillis() - now;
                double secs = millis / CacheMath.THOUSAND;
                String msg = Language.translate("vv:cmd.convert.msg.finish", millis, secs);
                sender.sendMessage(ChatColor.GREEN+msg);
            } catch (Exception ex) {
                ex.printStackTrace();
                String error = ex.getClass().getSimpleName()+" \""+ex.getMessage()+"\"";
                sender.sendMessage(ChatColor.DARK_RED+"Error: "+error);
            }
        };
    
        Thread convThread = new Thread() {
            @Override
            public void run() {
                try {
                    runWithMaxTime(task, MAX_TIME);
                } catch (ExecutionException ex) {
                    ex.printStackTrace();
                    Throwable cause = ex.getCause();
                    String error = cause.getClass().getSimpleName()+": "+cause.getMessage();
                    sender.sendMessage(ChatColor.DARK_RED+"ERROR: An unknown error occurred when converting: "+error);
                } catch (InterruptedException ex) {
                    String msg = Language.translate("vv:cmd.convert.error.interrupt");
                    sender.sendMessage(ChatColor.DARK_RED+"ERROR: "+msg);
                } catch (TimeoutException ex) {
                    String msg = Language.translate("vv:cmd.convert.error.timeout", MAX_TIME);
                    sender.sendMessage(ChatColor.DARK_RED+"ERROR: "+msg);
                }
            }
        };
        convThread.start();
    
        String
            fromStr = "<"+fromType+"> \""+from+"\"",
            toStr = "<"+toType+"> \""+to+"\"";
        sender.sendMessage(ChatColor.YELLOW+"Converting "+fromStr+" -> "+toStr+" ...");
    
        //comment out joining when not debugging
        try {
            convThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    @NotNull
    public static Argument[] toArguments(Object[] args, int start) {
        List<Argument> argList = new ArrayList<>();
        for (int i = start; i<args.length; i += 2) {
            final int index = i-start;
            String name = (String) args[index];
            Object value = args[index+1];
            argList.add(ArgumentFactory.create(name, value));
        }
        return argList.toArray(new Argument[argList.size()]);
    }
    
    public static void runWithMaxTime(Runnable task, long maxMillis) throws ExecutionException, InterruptedException,
        TimeoutException {
        
        ExecutorService executor = Executors.newFixedThreadPool(4);
    
        Future<?> future =  executor.submit(task);
        executor.shutdown();
    
        try {
            future.get(maxMillis, TimeUnit.MILLISECONDS);
        } catch (TimeoutException ex) {
            future.cancel(true);
            throw ex;
        }
        
        if (!executor.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
            executor.shutdownNow();
        }
    }
    
    @Override
    public String[] onTabComplete(CommandSender sender, Command command, String label, Object[] args) {
        if (args.length == 1) return Enumerations.toStrings(Format.class);
        if (args.length == 3) return Enumerations.toStrings(Format.class);
        
        return new String[0];
    }
    
}
