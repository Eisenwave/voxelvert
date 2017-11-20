package org.eisenwave.vv.bukkit.gui.widget;

import eisenwave.inv.menu.Menu;
import eisenwave.inv.style.Stylesheet;
import eisenwave.inv.widget.Button;
import eisenwave.inv.widget.CompoundGroup;
import eisenwave.inv.widget.Display;
import eisenwave.inv.widget.SimpleList;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PageNavigatorCompound extends CompoundGroup {
    
    private final SimpleList handle;
    private final Button navLeft, navRight;
    private final Display display;
    
    private final int pageSize;
    
    private int page = 0;
    
    public PageNavigatorCompound(@NotNull Menu menu, @Nullable Stylesheet style, @NotNull SimpleList handle) {
        super(menu);
        this.handle = handle;
        this.pageSize = handle.getArea();
        
        this.navLeft = new Button(menu, null);
        navLeft.setPosition(0, 0);
        addChild(navLeft);
        
        this.display = new Display(menu, null);
        display.setPosition(1, 0);
        addChild(display);
        
        this.navRight = new Button(menu, null);
        navRight.setPosition(2, 0);
        addChild(navRight);
        
        navLeft.addClickListener((event) -> {
            if (canNavLeft())
                navigate(-1);
        });
    
        navRight.addClickListener((event) -> {
            if (canNavRight())
                navigate(1);
        });
        
        navigate(0);
    }
    
    /**
     * Returns the current page of this navigator with the first page being 0.
     *
     * @return the current page
     */
    public int getPage() {
        return page;
    }
    
    public SimpleList getHandle() {
        return handle;
    }
    
    // UTIL
    
    private void navigate(int pages) {
        System.out.println("navigating "+pages);
        page += pages;
        handle.scrollPages(pages);
        navLeft.setItem(canNavLeft()? new ItemStack(Material.SPECTRAL_ARROW) : new ItemStack(Material.ARROW));
        display.setItem(new ItemStack(Material.MAP, this.page + 1));
        navRight.setItem(canNavRight()? new ItemStack(Material.SPECTRAL_ARROW) : new ItemStack(Material.ARROW));
    }
    
    private boolean canNavLeft() {
        return handle.getOffset() > 0;
    }
    
    private boolean canNavRight() {
        return handle.getLength() > ((page + 1) * pageSize);
    }
    
    
}
