package net.grian.vv.cmd;

import net.grian.vv.plugin.UserManager;
import net.grian.vv.plugin.VVUser;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;

public class CmdVVAdmin implements ParsingCommand {
    
    @Override
    public Parser[] formatOf(String[] args) {
        return new Parser[0];
    }
    
    @Override
    public void onCommand(CommandSender sender, Command command, String label, Object[] args) {
        String usage = "Usage: "+ChatColor.RED+"/"+label+" ";
        String result = usage+"(data|info|user) ...";
        
        if (args.length < 1) {
            sender.sendMessage(result);
            return;
        }
        else if (args[0].equals("data")) {
            sender.sendMessage("command is WIP");
            return;
        }
        else if (args[0].equals("info")) {
            sender.sendMessage("command is WIP");
            return;
        }
        else if (args[0].equals("user")) {
            result = usage+"user <user> (clear|info)";
            if (args.length < 3) {
                sender.sendMessage(result);
                return;
            }
    
            VVUser user = UserManager.getInstance().getByName((String) args[1]);
            if (user == null) {
                sender.sendMessage(ChatColor.RED+"User \""+args[1]+"\" does not exist");
                return;
            }
            
            if (args[2].equals("clear")) {
                user.clearData();
                sender.sendMessage(ChatColor.GREEN+"Cleared data of user");
                return;
            }
            else if (args[2].equals("info")) {
                sender.sendMessage(ChatColor.YELLOW+"Info for user \""+args[1]+"\":");
                printUser(sender, user);
                return;
            }
        }
        
        sender.sendMessage(result);
    }
    
    private void printUser(CommandSender sender, VVUser user) {
        StringBuilder builder = new StringBuilder();
        
        builder
            .append("Name: ")
            .append(user.getName())
            .append("\n");
        
        builder.append("Data: ");
        for (String key : user.listData()) {
            builder
                .append("\n \u2022")
                .append(key)
                .append(": ")
                .append(user.getData(key));
        }
        builder.append("\nDirectory: ");
    
        File dir;
        try {
            dir = user.getFileDirectory();
        } catch (IOException e) {
            dir = null;
        }
        builder
            .append("Directory")
            .append(dir);
        
        sender.sendMessage(builder.toString());
    }
    
    @Override
    public String[] onTabComplete(CommandSender sender, Command command, String label, Object[] args) {
        return new String[0];
    }
    
}
