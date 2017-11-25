package eisenwave.vv.bukkit.gui.widget;

import eisenwave.inv.menu.Menu;
import eisenwave.inv.style.Stylesheet;
import eisenwave.inv.widget.Button;
import eisenwave.inv.widget.CompoundGroup;
import eisenwave.inv.widget.Display;
import eisenwave.inv.widget.SimpleList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import eisenwave.inv.util.ItemInitUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PageNavigatorCompound extends CompoundGroup {
    
    private final static String
        PREFIX_ON = ChatColor.YELLOW.toString() + ChatColor.BOLD,
        PREFIX_OFF = ChatColor.DARK_GRAY.toString() + ChatColor.BOLD;
    
    private static final ItemStack
        PREV_ON = ItemInitUtil.create(Material.SPECTRAL_ARROW, PREFIX_ON + "<<<"),
        PREV_OFF = ItemInitUtil.create(Material.ARROW, PREFIX_OFF + "<<<"),
        NEXT_ON = ItemInitUtil.create(Material.SPECTRAL_ARROW, PREFIX_ON + ">>>"),
        NEXT_OFF = ItemInitUtil.create(Material.ARROW, PREFIX_OFF + ">>>");
    
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
        System.out.println("navigating " + pages);
        page += pages;
        handle.scrollPages(pages);
        navLeft.setItem(canNavLeft()? PREV_ON : PREV_OFF);
        navRight.setItem(canNavRight()? NEXT_ON : NEXT_OFF);
    
        String pageName = ChatColor.GRAY + "Page " + (this.page + 1);
        display.setItem(ItemInitUtil.create(Material.MAP, this.page + 1, (short) 0, pageName));
    }
    
    private boolean canNavLeft() {
        return handle.getOffset() > 0;
    }
    
    private boolean canNavRight() {
        return handle.getLength() > ((page + 1) * pageSize);
    }
    
    
}
