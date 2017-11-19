package org.eisenwave.vv.bukkit.gui.old_widget;

import eisenwave.inv.event.ViewEvent;
import eisenwave.inv.view.View;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SelectEvent extends ViewEvent {
    
    private final boolean previous, checked;
    
    public SelectEvent(@NotNull View view, @NotNull Player player, boolean previous, boolean checked) {
        super(view, player);
        this.previous = previous;
        this.checked = checked;
    }
    
    /**
     * Returns whether the checking state has changed during this action.
     *
     * @return whether the checking state has changed
     */
    public boolean hasChanged() {
        return previous != checked;
    }
    
    /**
     * Returns the previous checking state
     *
     * @return the previous checking state
     */
    public boolean getPrevious() {
        return previous;
    }
    
    /**
     * Returns the new checking state
     *
     * @return the new checking state
     */
    public boolean isSelected() {
        return checked;
    }
    
}
