package net.grian.vv.cache;

import net.grian.spatium.geo.BlockSelection;
import net.grian.spatium.geo.BlockVector;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public class UserManager {

    private static UserManager instance = null;

    public synchronized static UserManager getInstance() {
        return instance;
    }

    private final Map<Player, UserSettings> settingsMap = new WeakHashMap<>();

    private UserManager() {}

    private UserSettings getSettings(Player player) {
        Objects.requireNonNull(player);
        if (settingsMap.containsKey(player))
            return settingsMap.get(player);
        UserSettings settings = new UserSettings();
        settingsMap.put(player, settings);
        return settings;
    }

    public BlockSelection getSelection(Player player) {
        UserSettings settings = getSettings(player);
        if (settings.selection == null || settings.selection[0] == null || settings.selection[1] == null) return null;
        return BlockSelection.between(settings.selection[0], settings.selection[1]);
    }

    public BlockVector getFirstPosition(Player player) {
        UserSettings settings = getSettings(player);
        return settings.selection==null? null : settings.selection[0];
    }

    public BlockVector getSecondPosition(Player player) {
        UserSettings settings = getSettings(player);
        return settings.selection==null? null : settings.selection[1];
    }

    public void setFirstPosition(Player player, BlockVector block) {
        UserSettings settings = getSettings(player);
        if (settings.selection == null)
            settings.selection = new BlockVector[2];
        settings.selection[0] = block;
    }

    public void setSecondPosition(Player player, BlockVector block) {
        UserSettings settings = getSettings(player);
        if (settings.selection == null)
            settings.selection = new BlockVector[2];
        settings.selection[1] = block;
    }

    public ColorMap getColorMap(Player player) {
        return getSettings(player).colors;
    }

    private static class UserSettings {

        private BlockVector[] selection;
        private ColorMap colors;

    }

}
