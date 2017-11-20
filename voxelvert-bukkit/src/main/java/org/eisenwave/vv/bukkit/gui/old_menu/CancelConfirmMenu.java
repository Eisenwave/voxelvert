package org.eisenwave.vv.bukkit.gui.old_menu;

import org.eisenwave.vv.bukkit.gui.old_widget.LargeButtonWidget;
import nl.klikenklaar.util.gui.buttons.Button;
import nl.klikenklaar.util.gui.buttons.CommandButton;
import nl.klikenklaar.util.gui.menus.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;

@Deprecated
public class CancelConfirmMenu extends Menu {
    
    public CancelConfirmMenu(int size, String name, String cancelCmd, String confirmCmd) {
        super(size, name);
        
        Button cancel = new CommandButton(Material.CONCRETE, 1, (short) 14, ChatColor.RED+"Cancel", cancelCmd);
        Button confirm = new CommandButton(Material.CONCRETE, 1, (short) 9, ChatColor.GREEN+"Confirm", confirmCmd);
    
        final int
            rows = size / 9,
            minY = Math.min(0, rows - 1);
        
        new LargeButtonWidget(this, cancel,  2, 2, 1, minY).draw();
        new LargeButtonWidget(this, confirm, 2, 2, 8, minY).draw();
    }
    
}
