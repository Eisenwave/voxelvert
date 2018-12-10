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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import eisenwave.vv.bukkit.gui.FileOptionsMode;
import eisenwave.inv.util.ItemUtil;
import org.jetbrains.annotations.NotNull;

public class FileOptionsWidget extends Widget {
    
    /*
    private static final String
        RM_SUCCESS = ChatColor.BLUE + "[VoxelVert] " + ChatColor.RESET + "Deleted the file",
        RM_FAIL = ChatColor.RED + "[VoxelVert] " + ChatColor.RESET + "Couldn't delete the file",
        MV_SUCCESS = ChatColor.BLUE + "[VoxelVert] " + ChatColor.RESET + "Renamed the file",
        MV_FAIL = ChatColor.RED + "[VoxelVert] " + ChatColor.RESET + "Couldn't rename the file",
        MV_NEED_SUF = ChatColor.BLUE + "[VoxelVert] " + ChatColor.RESET + "You must provide a file suffix",
        WIP = ChatColor.BLUE + "[VoxelVert] " + ChatColor.RESET + "This feature is WIP";
    */
    
    private final static ItemStack
        ITEM_BACKGROUND = ItemUtil.create("black_stained_glass_pane", " "),
        ITEM_OPEN = ItemUtil.create("diamond_pickaxe", ChatColor.AQUA + "Open", "&7Open with VoxelVert"),
        ITEM_SHARE = ItemUtil.create("ender_pearl", ChatColor.AQUA + "Share", "&7Share with friends"),
        ITEM_SHARE_WITH_PLAYER = ItemUtil.create("skeleton_skull", ChatColor.GREEN + "Player",
            "&7Share with\n&7a player"),
        ITEM_SHARE_WITH_WE = ItemUtil.hideAttributes(ItemUtil.create("wooden_axe",
            ChatColor.GREEN + "WorldEdit", "&7Share with WorldEdit\n&8(schematic only)")),
        ITEM_SHARE_DOWNLOAD = ItemUtil.create("chest_minecart",
            ChatColor.GREEN + "Download", "&7Download file\n&8(over http)"),
        ITEM_UPLOAD = ItemUtil.create("lead", ChatColor.GREEN + "Upload", "&7Upload file\n&8(over http)"),
        ITEM_COPY = ItemUtil.create("name_tag", ChatColor.YELLOW + "Copy", "&7Copy this file"),
        ITEM_RENAME = ItemUtil.create("name_tag", ChatColor.YELLOW + "Rename", "&7Rename this file"),
        ITEM_DELETE = ItemUtil.create("lava_bucket", ChatColor.RED + "Delete",
            "&7Delete this file\n\n&cWARNING:\n&7You may not be able\n&7to undo this action"),
        ITEM_CONFIRM = ItemUtil.create("lime_stained_glass_pane", ChatColor.GREEN + "Confirm",
            "&7Delete this file"),
        ITEM_CANCEL_DELETE = ItemUtil.create("red_stained_glass_pane", ChatColor.RED + "Cancel",
            "&7&nDo not\n&7delete this file"),
        ITEM_CANCEL_SHARE = ItemUtil.create("red_stained_glass_pane", ChatColor.RED + "Cancel",
            "&7&nDo not\n&7share this file");
    
    static {
        ItemMeta meta = ITEM_OPEN.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        ITEM_OPEN.setItemMeta(meta);
    }
    
    private FileOptionsMode prevMode, mode;
    
    private Button
        btnUpload,
        btnOpen, btnShare, btnCopy, btnDelete, btnRename,
        btnShareWithPlayer, btnShareWithWE, btnShareDownload, btnCancelShare,
        btnConfirmDelete, btnCancelDelete;
    
    // INIT
    
    public FileOptionsWidget(@NotNull FileBrowserMenu menu) {
        super(menu, new ViewSize(ViewSize.WRAP_CONTENT, ViewSize.WRAP_CONTENT), null);
        this.mode = FileOptionsMode.DEFAULT;
    
        initDefault();
        initOpen();
        initShare();
        initShareMode();
        initCopy();
        initRename();
        initDelete();
        initDeleteMode();
    }
    
    private void initDefault() {
        btnUpload = new Button(getMenu(), null);
        btnUpload.setParent(this);
        btnUpload.setItem(ITEM_UPLOAD);
        
        btnUpload.addClickListener(event -> getMenu().performUpload(event.getPlayer()));
    }
    
    private void initOpen() {
        btnOpen = new Button(getMenu(), null);
        btnOpen.setParent(this);
        btnOpen.setItem(ITEM_OPEN);
    
        btnOpen.addClickListener(event -> getMenu().performOpen(event.getPlayer()));
    }
    
    private void initShare() {
        btnShare = new Button(getMenu(), null);
        btnShare.setParent(this);
        btnShare.setItem(ITEM_SHARE);
    
        btnShare.addClickListener(event -> this.setMode(FileOptionsMode.SHARE_FILE));
    }
    
    private void initShareMode() {
        btnShareWithPlayer = new Button(getMenu(), null);
        btnShareWithPlayer.setParent(this);
        btnShareWithPlayer.setItem(ITEM_SHARE_WITH_PLAYER);
        btnShareWithPlayer.addClickListener(event -> getMenu().performShare("player", event.getPlayer()));
        
        btnShareWithWE = new Button(getMenu(), null);
        btnShareWithWE.setParent(this);
        btnShareWithWE.setItem(ITEM_SHARE_WITH_WE);
        btnShareWithWE.addClickListener(event -> getMenu().performShare("worldedit", event.getPlayer()));
        
        btnShareDownload = new Button(getMenu(), null);
        btnShareDownload.setParent(this);
        btnShareDownload.setItem(ITEM_SHARE_DOWNLOAD);
        btnShareDownload.addClickListener(event -> getMenu().performShare("download", event.getPlayer()));
        
        btnCancelShare = new Button(getMenu(), null);
        btnCancelShare.setParent(this);
        btnCancelShare.setItem(ITEM_CANCEL_SHARE);
        btnCancelShare.addClickListener(event -> this.setMode(prevMode));
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
    
    private void initDeleteMode() {
        btnConfirmDelete = new Button(getMenu(), null);
        btnConfirmDelete.setParent(this);
        btnConfirmDelete.setItem(ITEM_CONFIRM);
        
        btnConfirmDelete.addClickListener(event -> {
            //Player player = event.getPlayer();
            getMenu().performDelete(event.getPlayer());
            //event.getPlayer().sendMessage(success? RM_SUCCESS : RM_FAIL);
        });
        
        btnCancelDelete = new Button(getMenu(), null);
        btnCancelDelete.setParent(this);
        btnCancelDelete.setItem(ITEM_CANCEL_DELETE);
        
        btnCancelDelete.addClickListener(event -> this.setMode(prevMode));
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
            case DEFAULT: {
                buffer.set(0, 0, btnUpload.draw());
                buffer.fill(1, 0, 5, 1, backgroundIcon);
                break;
            }
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
                buffer.set(0, 0, btnConfirmDelete.draw());
                buffer.set(1, 0, backgroundIcon);
                buffer.set(2, 0, backgroundIcon);
                buffer.set(3, 0, btnCancelDelete.draw());
                buffer.set(4, 0, btnDelete.draw());
                break;
            }
            case SHARE_FILE: {
                buffer.set(0, 0, btnShareWithPlayer.draw());
                buffer.set(1, 0, btnShareWithWE.draw());
                buffer.set(2, 0, btnShareDownload.draw());
                buffer.set(3, 0, backgroundIcon);
                buffer.set(4, 0, btnCancelShare.draw());
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
