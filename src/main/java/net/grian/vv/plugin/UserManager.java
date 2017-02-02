package net.grian.vv.plugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public final class UserManager {

    private static UserManager instance = new UserManager();

    public synchronized static UserManager getInstance() {
        return instance;
    }

    private final Map<UUID, VVUser> userMap = new WeakHashMap<>();
    private VVUser consoleUser;
    private VVUser debugUser;
    
    @Nonnull
    public VVUser getPlayerUser(UUID id) {
        if (userMap.containsKey(id))
            return userMap.get(id);
        
        VVUser user = new BukkitVVUser(Bukkit.getPlayer(id));
        userMap.put(id, user);
        return user;
    }
    
    @Nonnull
    public VVUser getPlayerUser(Player player) {
        return getPlayerUser(player.getUniqueId());
    }
    
    @Nonnull
    public VVUser getConsoleUser() {
        return consoleUser;
    }
    
    @Nonnull
    public VVUser getDebugUser() {
        return debugUser;
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
