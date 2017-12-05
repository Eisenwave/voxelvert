package eisenwave.vv.bukkit.gui.widget;

import eisenwave.inv.menu.MenuManager;
import eisenwave.inv.query.ChatQuery;
import eisenwave.inv.view.Icon;
import eisenwave.inv.view.IconBuffer;
import eisenwave.inv.view.ViewSize;
import eisenwave.inv.widget.Button;
import eisenwave.inv.widget.Widget;
import eisenwave.vv.bukkit.gui.menu.FileBrowserMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import eisenwave.vv.bukkit.gui.FileOptionsMode;
import eisenwave.inv.util.ItemUtil;
import org.jetbrains.annotations.NotNull;

public class FileOptionsWidget extends Widget {
    
    private static final String
        RM_SUCCESS = ChatColor.BLUE + "[VoxelVert] " + ChatColor.RESET + "Deleted the file",
        RM_FAIL = ChatColor.RED + "[VoxelVert] " + ChatColor.RESET + "Couldn't delete the file",
        MV_SUCCESS = ChatColor.BLUE + "[VoxelVert] " + ChatColor.RESET + "Renamed the file",
        MV_FAIL = ChatColor.RED + "[VoxelVert] " + ChatColor.RESET + "Couldn't rename the file",
        MV_NEED_SUF = ChatColor.BLUE + "[VoxelVert] " + ChatColor.RESET + "You must provide a file suffix";
    
    private final static ItemStack
        ITEM_BACKGROUND = ItemUtil.create(Material.STAINED_GLASS_PANE, 1, (short) 15, " "),
        ITEM_OPEN = ItemUtil.create(Material.DIAMOND_PICKAXE, ChatColor.AQUA + "Open", "&7Open with VoxelVert"),
        ITEM_SHARE = ItemUtil.create(Material.ENDER_PEARL, ChatColor.AQUA + "Share", "&7Share with friends"),
        ITEM_COPY = ItemUtil.create(Material.MINECART, ChatColor.YELLOW + "Copy", "&7Copy this file"),
        ITEM_RENAME = ItemUtil.create(Material.NAME_TAG, ChatColor.YELLOW + "Rename", "&7Rename this file"),
        ITEM_DELETE = ItemUtil.create(Material.LAVA_BUCKET, ChatColor.RED + "Delete",
            "&7Delete this file\n\n&cWARNING:\n&7You may not be able\n&7to undo this action"),
        ITEM_CONFIRM = ItemUtil.create(Material.STAINED_GLASS_PANE, 1, (short) 5, ChatColor.GREEN + "Confirm",
            "&7Delete this file"),
        ITEM_CANCEL = ItemUtil.create(Material.STAINED_GLASS_PANE, 1, (short) 14, ChatColor.RED + "Cancel",
            "&7&nDo not\n&7delete this file");
    
    static {
        ItemMeta meta = ITEM_OPEN.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        ITEM_OPEN.setItemMeta(meta);
    }
    
    private FileOptionsMode prevMode, mode;
    
    private Button btnOpen, btnShare, btnCopy, btnDelete, btnRename, btnConfirm, btnCancel;
    
    // INIT
    
    public FileOptionsWidget(@NotNull FileBrowserMenu menu) {
        super(menu, new ViewSize(ViewSize.WRAP_CONTENT, ViewSize.WRAP_CONTENT), null);
        this.mode = FileOptionsMode.EMPTY;
        
        initOpen();
        initShare();
        initCopy();
        initRename();
        initDelete();
        initConfirm();
        initCancel();
    }
    
    private void initOpen() {
        btnOpen = new Button(getMenu(), null);
        btnOpen.setParent(this);
        btnOpen.setItem(ITEM_OPEN);
        
        btnOpen.addClickListener(event -> {
            getMenu().performOpen(event.getPlayer());
        });
    }
    
    private final static String WIP = ChatColor.BLUE + "[VoxelVert] " + ChatColor.RESET + "This feature is WIP";
    
    private void initShare() {
        btnShare = new Button(getMenu(), null);
        btnShare.setParent(this);
        btnShare.setItem(ITEM_SHARE);
        
        btnShare.addClickListener(event -> event.getPlayer().sendMessage(WIP));
    }
    
    private void initCopy() {
        btnCopy = new Button(getMenu(), null);
        btnCopy.setParent(this);
        btnCopy.setItem(ITEM_COPY);
        
        btnCopy.addClickListener(event -> {
            ChatQuery query = new ChatQuery() {
                @Override
                public void onResult(Player player, String result) {
                    getMenu().performCopy(player, result);
                }
                
                @Override
                public void onFail(Player player) {}
            };
            query.inform(event.getPlayer());
            MenuManager.getInstance().startQuery(event.getPlayer(), query);
        });
    }
    
    private void initRename() {
        btnRename = new Button(getMenu(), null);
        btnRename.setParent(this);
        btnRename.setItem(ITEM_RENAME);
        
        btnRename.addClickListener(event -> {
            ChatQuery query = new ChatQuery() {
                @Override
                public void onResult(Player player, String result) {
                    getMenu().performRename(player, result);
                }
                
                @Override
                public void onFail(Player player) {}
            };
            query.inform(event.getPlayer());
            MenuManager.getInstance().startQuery(event.getPlayer(), query);
        });
    }
    
    private void initDelete() {
        btnDelete = new Button(getMenu(), null);
        btnDelete.setParent(this);
        btnDelete.setItem(ITEM_DELETE);
        
        btnDelete.addClickListener(event -> this.setMode(FileOptionsMode.DELETE));
    }
    
    private void initConfirm() {
        btnConfirm = new Button(getMenu(), null);
        btnConfirm.setParent(this);
        btnConfirm.setItem(ITEM_CONFIRM);
        
        btnConfirm.addClickListener(event -> {
            //Player player = event.getPlayer();
            getMenu().performDelete(event.getPlayer());
            //event.getPlayer().sendMessage(success? RM_SUCCESS : RM_FAIL);
        });
    }
    
    private void initCancel() {
        btnCancel = new Button(getMenu(), null);
        btnCancel.setParent(this);
        btnCancel.setItem(ITEM_CANCEL);
        
        btnCancel.addClickListener(event -> this.setMode(prevMode));
    }
    
    // GETTERS
    
    @NotNull
    @Override
    public FileBrowserMenu getMenu() {
        return (FileBrowserMenu) super.getMenu();
    }
    
    public FileOptionsMode getMode() {
        return mode;
    }
    
    /**
     * Returns whether this view is enabled.
     *
     * @return whether this view is enabled
     */
    public boolean isEnabled() {
        return mode != FileOptionsMode.EMPTY;
    }
    
    /**
     * Enables or disables this view.
     *
     * @param mode the mode
     * @see #isEnabled()
     */
    public void setMode(@NotNull FileOptionsMode mode) {
        if (this.mode != mode) {
            this.prevMode = this.mode;
            this.invalidate();
        }
        this.mode = mode;
    }
    
    @Override
    public int getContentWidth() {
        return 5;
    }
    
    @Override
    public int getContentHeight() {
        return 1;
    }
    
    // DRAW
    
    @Override
    protected void drawContent(IconBuffer buffer) {
        Icon backgroundIcon = new Icon(this, ITEM_BACKGROUND);
        switch (mode) {
            case EMPTY:
                buffer.fill(backgroundIcon);
                break;
            case KNOWN_FILE: {
                buffer.set(0, 0, btnOpen.draw());
                buffer.set(1, 0, btnShare.draw());
                buffer.set(2, 0, btnCopy.draw());
                buffer.set(3, 0, btnRename.draw());
                buffer.set(4, 0, btnDelete.draw());
                break;
            }
            case FILE: {
                buffer.set(0, 0, backgroundIcon);
                buffer.set(1, 0, backgroundIcon);
                buffer.set(2, 0, btnCopy.draw());
                buffer.set(3, 0, btnRename.draw());
                buffer.set(4, 0, btnDelete.draw());
                break;
            }
            case DELETE: {
                buffer.set(0, 0, btnConfirm.draw());
                buffer.set(1, 0, backgroundIcon);
                buffer.set(2, 0, backgroundIcon);
                buffer.set(3, 0, btnCancel.draw());
                buffer.set(4, 0, btnDelete.draw());
                break;
            }
            case VARIABLE: {
                buffer.set(0, 0, btnOpen.draw());
                buffer.fill(1, 0, 5, 1, backgroundIcon);
                break;
            }
            case FOLDER: {
                buffer.set(0, 0, backgroundIcon);
                buffer.set(1, 0, backgroundIcon);
                buffer.set(2, 0, backgroundIcon);
                buffer.set(3, 0, btnRename.draw());
                buffer.set(4, 0, btnDelete.draw());
                break;
            }
        }
    }
    
}
