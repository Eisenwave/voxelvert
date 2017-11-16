package org.eisenwave.vv.bukkit;

import org.eisenwave.vv.ui.user.VVUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public final class UserManager {

    private static UserManager instance = new UserManager();

    public synchronized static UserManager getInstance() {
        return instance;
    }

    private final Map<UUID, VVUser> userMap = new WeakHashMap<>();
    private final VVUser consoleUser = null;
    //private final VVUser debugUser = new ShellVVUser();
    
    @NotNull
    public VVUser getPlayerUser(UUID id) {
        if (userMap.containsKey(id))
            return userMap.get(id);
        
        //VVUser user = new BukkitVVUser(Bukkit.getPlayer(id));
        //userMap.put(id, user);
        return null;
    }
    
    @NotNull
    public VVUser getPlayerUser(Player player) {
        return getPlayerUser(player.getUniqueId());
    }
    
    @NotNull
    public VVUser getConsoleUser() {
        return consoleUser;
    }
    
    @Nullable
    public VVUser getByName(String name) {
        if (name.equalsIgnoreCase("#CONSOLE")) return consoleUser;
        else if (name.equalsIgnoreCase("#DEBUG")) return null;
        
        Player player = Bukkit.getPlayer(name);
        return userMap.get(player.getUniqueId());
    }
    
    public void removeUser(UUID id) {
        userMap.remove(id);
    }
    
    public void clearUsers() {
        userMap.clear();
    }
    
    public int getUserCount() {
        return userMap.size();
    }
    
}
