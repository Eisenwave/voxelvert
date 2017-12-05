package eisenwave.vv.bukkit.gui.old_widget;

import nl.klikenklaar.util.gui.buttons.Button;
import nl.klikenklaar.util.gui.menus.Menu;

import java.util.List;

@Deprecated
public class PagedButtonListWidget extends Widget {
    
    private final Button[] buttons;
    private int width, height, page;
    
    public PagedButtonListWidget(Menu menu, List<? extends Button> buttons, int width, int height, int x, int y) {
        super(menu, x, y);
        setWidth(width);
        setHeight(height);
        setPage(0);
        
        this.buttons = buttons.toArray(new Button[buttons.size()]);
    }
    
    public PagedButtonListWidget(Menu menu, List<? extends Button> buttons, int width, int height) {
        this(menu, buttons, width, height, 0, 0);
    }
    
    // DRAW
    
    @Override
    protected void doDraw() {
        int
            area = getArea(),
            offset = page * area,
            lim = Math.min(buttons.length - offset, area);
        
        for (int i = 0; i < lim; i++)
            drawButton(buttons[offset + i], i % width, i / height);
    }
    
    // GETTERS
    
    @Override
    public int getWidth() {
        return width;
    }
    
    @Override
    public int getHeight() {
        return height;
    }
    
    /**
     * Returns the current page.
     *
     * @return the current page
     */
    public int getPage() {
        return page;
    }
    
    /**
     * Returns the amount of pages in this widget.
     *
     * @return the amount of pages
     */
    public int getPages() {
        return buttons.length / getArea();
    }
    
    public Button getButton(int index) {
        return buttons[index];
    }
    
    // MUTATORS
    
    public void setWidth(int width) {
        if (width < 1 || width > 9)
            throw new IllegalArgumentException("width must be in range(1,9)");
        this.width = width;
    }
    
    public void setHeight(int height) {
        if (height < 1 || height > 6)
            throw new IllegalArgumentException("height must be in range(1,6)");
        this.height = height;
    }
    
    /**
     * Sets the current page to the given page.
     *
     * @param page the new page
     * @throws IndexOutOfBoundsException if the page is either negative or >= the amount of pages
     * @see #getPages()
     */
    public void setPage(int page) {
        if (page < 0 || page >= getPages())
            throw new IndexOutOfBoundsException(Integer.toString(page));
        this.page = page;
    }
    
}
