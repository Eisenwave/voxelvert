package net.grian.vv.cmd;

import net.grian.spatium.util.Enumerations;
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

import java.util.Optional;
import java.util.concurrent.*;

public class CmdConvert implements ParsingCommand {
    
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
        String usage = ChatColor.RED+"/"+label+" ";
        
        if (args.length < 4) {
            sender.sendMessage(usage+"<fromType> <from> <toType> <to> ['-'<argType> <arg>+]*");
            return;
        }
        
        Format fromType = (Format) args[0];
        String from = (String) args[1];
        Format toType = (Format) args[2];
        String to = (String) args[3];
        
        Formatverter fmtverter = FormatverterFactory.fromFormats(fromType, toType);
        VVUser user;
        if (sender instanceof Player) {
            user = UserManager.getInstance().getPlayerUser((Player) sender);
        } else if (sender instanceof ConsoleCommandSender) {
            user = UserManager.getInstance().getConsoleUser();
        } else {
            user = UserManager.getInstance().getDebugUser();
        }
    
        Object[] fmtvertArgs = new Object[args.length - 4];
        System.arraycopy(args, 4, fmtvertArgs, 0, args.length - 4);
        
        Runnable task = () -> {
            try {
                fmtverter.convert(user, from, to, fmtvertArgs);
                sender.sendMessage(ChatColor.GREEN+"Finished converting :)");
            } catch (Exception ex) {
                sender.sendMessage(ChatColor.DARK_RED+ex.getClass().getSimpleName()+" occurred when converting");
            }
        };
    
        new Thread() {
            @Override
            public void run() {
                try {
                    runWithMaxTime(task, 3000);
                } catch (ExecutionException ex) {
                    ex.printStackTrace();
                    Throwable cause = ex.getCause();
                    String error = cause.getClass().getSimpleName()+": "+cause.getMessage();
                    sender.sendMessage(ChatColor.DARK_RED+"An unknown error occurred when converting: "+error);
                } catch (InterruptedException ex) {
                    sender.sendMessage(ChatColor.DARK_RED+"Converting has been interrupted");
                } catch (TimeoutException ex) {
                    sender.sendMessage(ChatColor.DARK_RED+"Converting took too long and has been cancelled :/");
                }
            }
        }.start();
    
        sender.sendMessage("Converting from ("+fromType+") "+from+" to ("+toType+") "+to+" ...");
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
