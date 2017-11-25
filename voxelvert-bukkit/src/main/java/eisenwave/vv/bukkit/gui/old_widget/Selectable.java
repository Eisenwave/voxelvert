package eisenwave.vv.bukkit.gui.old_widget;

import eisenwave.inv.event.ClickListener;
import org.bukkit.entity.Player;

public interface Selectable {
    
    /**
     * Performs a clicking of this object.
     * <p>
     * This will invoke all listeners.
     *
     * @param player the player
     */
    public void performSelect(Player player);
    
    public void setSelected(boolean selected);
    
    public boolean isSelected();
    
    public void addSelectListener(ClickListener listener);
    
    public void removeSelectListener(ClickListener listener);
    
}
