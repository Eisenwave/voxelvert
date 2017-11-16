package org.eisenwave.vv.bukkit.inv.widget;

import nl.klikenklaar.util.gui.menus.Menu;

import java.util.List;

/**
 * <p>
 *    A layout with fixed width 9 which arranges its widgets in rows at one widget per row. The width of a row depends
 *    on the width of its widget.
 * </p>
 * <p>
 *     The layout guarantees to draw all widgets completely, meaning that it won't draw a widget partially if it doesn't
 *     fit into the layout page anymore.
 * </p>
 */
public class PagedRowListLayout extends Widget {
    
    private final Widget[] widgets;
    private int height, page;
    
    public PagedRowListLayout(Menu menu, List<? extends Widget> widgets, int height, int y) {
        super(menu, 0, y);
        setHeight(height);
        setPage(0);
        
        for (Widget w : widgets)
            if (w.getHeight() > height)
                throw new IllegalArgumentException("widget exceeds layout height ("+height+")");
        
        this.widgets = widgets.toArray(new Widget[widgets.size()]);
    }
    
    public PagedRowListLayout(Menu menu, List<? extends Widget> buttons, int height) {
        this(menu, buttons, height, 0);
    }
    
    // DRAW
    
    @Override
    protected void doDraw() {
        int curPage = 0, filled = 0;
        
        for (int i = 0; i < widgets.length; i++) {
            
            if (curPage == page) {
                filled = 0;
                for (int j = i; j < widgets.length; j++) {
                    widgets[i].setPosition(0, posY + filled);
                    if ((filled += widgets[i].getHeight()) > this.height) {
                        break;
                    }
                }
                break;
            }
            
            int h = widgets[i].getHeight();
            if ((filled += h) > this.height) {
                filled = h;
                curPage++;
            }
        }
    }
    
    // GETTERS
    
    @Override
    public int getWidth() {
        return 9;
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
        int result = 0, filled = 0;
        
        for (Widget widget : this.widgets) {
            int h = widget.getHeight();
            if ((filled += h) > this.height) {
                filled = h;
                result++;
            }
        }
        
        return result;
    }
    
    // MUTATORS
    
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
