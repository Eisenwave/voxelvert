package eisenwave.vv.bukkit.gui.old_widget;

import nl.klikenklaar.util.gui.buttons.Button;
import nl.klikenklaar.util.gui.menus.Menu;
import org.jetbrains.annotations.NotNull;

public class ButtonWidget extends Widget {
    
    private final Button button;
    
    public ButtonWidget(@NotNull Menu menu, @NotNull Button button, int x, int y) {
        super(menu, x, y);
        this.button = button;
    }
    
    public ButtonWidget(@NotNull Menu menu, @NotNull Button button) {
        super(menu);
        this.button = button;
    }
    
    @Override
    protected void doDraw() {
        drawButton(button, 0, 0);
    }
    
    @Override
    public int getWidth() {
        return 1;
    }
    
    @Override
    public int getHeight() {
        return 1;
    }
    
    @Override
    public int getArea() {
        return 1;
    }
    
    public Button getButton() {
        return button;
    }
    
}
