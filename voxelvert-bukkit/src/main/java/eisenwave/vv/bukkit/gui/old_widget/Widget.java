package eisenwave.vv.bukkit.gui.old_widget;

import net.grian.spatium.util.PrimMath;
import nl.klikenklaar.util.gui.buttons.Button;
import nl.klikenklaar.util.gui.menus.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * A GUI-Widget.
 */
public abstract class Widget {
    
    private final Menu menu;
    private boolean showDrawFails = true;
    
    protected int posX, posY;
    
    public Widget(Menu menu, int x, int y) {
        this.menu = menu;
        setPosX(x);
        setPosY(y);
    }
    
    public Widget(Menu menu) {
        this(menu, 0, 0);
    }
    
    // DRAW
    
    /**
     * Draws the widget into its menu.
     *
     * @return <code>true</code> if the widget could be drawn, <code>false</code> if it couldn't (f.e. if the widget
     * exceeds the boundaries of the menu)
     */
    public boolean draw() {
        final int w = getWidth(), h = getHeight();
        
        if (posX + w <= 9 && posY + h <= rowsOf(menu)) {
            doDraw();
            return true;
        }
        
        else if (showsDrawFails()) {
            String name = String.format(ChatColor.RED+"Draw Fail %s", getClass().getSimpleName());
            Button debug_0 = new Button(Material.CONCRETE, 1, (short) 2, name);
            Button debug_1 = new Button(Material.CONCRETE, 15, (short) 2, name);
            
            final int
                rows = rowsOf(menu),
                minX = PrimMath.clamp(1, posX, 9),
                minY = PrimMath.clamp(1, posY, rows),
                maxX = PrimMath.clamp(1, posX+getWidth(), 9),
                maxY = PrimMath.clamp(1, posY+getHeight(), rows);
            
            for (int x = minX; x <= maxX; x++) for (int y = minY; y <= maxY; y++) {
                Button button = (x+y)%2==0? debug_0 : debug_1;
                menu.setButton(button, x, y);
            }
            return false;
        }
        
        else return false;
    }
    
    /**
     * Erases the widget by removing all buttons within the area of the widget from the menu.
     *
     * @return <code>true</code> if the widget could be erased, <code>false</code> if it couldn't (f.e. if the widget
     * exceeds the boundaries of the menu)
     */
    public boolean erase() {
        final int w = getWidth(), h = getHeight();
    
        if (posX + w <= 9 && posY + h <= rowsOf(menu)) {
            for (int x = 0; x < w; x++)
                for (int y = 0; y < h; y++)
                    menu.setButton(null, posX + x, posY + y);
            return true;
        }
        else return false;
    }
    
    /**
     * The draw function to be implemented by widget subclasses.
     */
    protected abstract void doDraw();
    
    /**
     * Draws a button into the menu with an offset equal to the position of the widget.
     *
     * @param button the button to draw
     * @param relX the relative x-coordinate
     * @param relY the relative y-coordinate
     */
    protected void drawButton(Button button, int relX, int relY) {
        menu.setButton(button, posX + relX, posY + relY);
    }
    
    // GET
    
    public abstract int getWidth();
    
    public abstract int getHeight();
    
    public int getPosX() {
        return posX;
    }
    
    public int getPosY() {
        return posY;
    }
    
    /**
     * <p>
     *     Returns whether the widget shows drawing failures. A draw failure occurs when the widget is attempted to be
     *     drawn outside of the boundaries of its containing menu.
     * </p>
     * <p>
     *     In this case the area which the widget tried to draw is being clamped to the boundaries of the menu and the
     *     area is being filled with debug buttons to visualize the mistake.
     * </p>
     *
     * @return whether the widget shows draw failures
     */
    public boolean showsDrawFails() {
        return showDrawFails;
    }
    
    public int getArea() {
        return getWidth() * getHeight();
    }
    
    // MUTATORS
    
    public void setPosition(int x, int y) {
        setPosX(x);
        setPosY(y);
    }
    
    public void setPosX(int x) {
        if (x < 0) throw new IndexOutOfBoundsException("x must be positive");
        this.posX = x;
    }
    
    public void setPosY(int y) {
        if (y < 0) throw new IndexOutOfBoundsException("x must be positive");
        this.posY = y;
    }
    
    public void setShowDrawFails(boolean flag) {
        this.showDrawFails = flag;
    }
    
    // UTIL
    
    private static int rowsOf(Menu menu) {
        return (menu.getButtons().length / 9) + 1;
    }
    
}
