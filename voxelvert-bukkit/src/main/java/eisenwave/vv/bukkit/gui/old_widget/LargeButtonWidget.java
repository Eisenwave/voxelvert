package eisenwave.vv.bukkit.gui.old_widget;

import nl.klikenklaar.util.gui.buttons.Button;
import nl.klikenklaar.util.gui.menus.Menu;
import org.jetbrains.annotations.NotNull;

@Deprecated
public class LargeButtonWidget extends Widget {
    
    private Button button;
    private int w, h;
    
    public LargeButtonWidget(Menu menu, Button button, int w, int h, int x, int y) {
        super(menu, x, y);
        this.button = button;
        setWidth(w);
        setHeight(h);
    }
    
    public LargeButtonWidget(Menu menu, Button button, int w, int h) {
        this(menu, button, w, h, 0, 0);
    }
    
    @Override
    public void doDraw() {
        for (int x = 0; x < w; x++)
            for (int y = 0; y < h; y++)
                drawButton(button, x, y);
    }
    
    // GETTERS
    
    @Override
    public int getWidth() {
        return w;
    }
    
    @Override
    public int getHeight() {
        return h;
    }
    
    @NotNull
    public Button getButton() {
        return button;
    }
    
    // MUTATORS
    
    public void setWidth(int width) {
        if (width < 1 || width > 9) throw new IllegalArgumentException("x must be in range(1,9)");
        this.w = width;
    }
    
    public void setHeight(int height) {
        if (height < 1 || height > 6) throw new IllegalArgumentException("y must be in range(1,6)");
        this.h = height;
    }
    
    public void setButton(@NotNull Button button) {
        this.button = button;
    }
    
}
