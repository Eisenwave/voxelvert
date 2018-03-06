package eisenwave.vv.bukkit.gui.menu;

import eisenwave.inv.menu.Menu;
import eisenwave.inv.menu.MenuResponse;
import eisenwave.inv.view.ViewSize;
import eisenwave.inv.widget.MenuPane;
import eisenwave.inv.widget.Pane;
import eisenwave.vv.bukkit.gui.widget.FileList;
import eisenwave.vv.bukkit.gui.widget.FileOptionsWidget;
import eisenwave.vv.bukkit.gui.widget.PageNavigatorCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import eisenwave.vv.bukkit.gui.FileBrowserEntry;
import eisenwave.vv.bukkit.gui.FileOptionsMode;
import eisenwave.inv.util.ItemUtil;
import eisenwave.vv.object.Language;
import eisenwave.vv.ui.user.VVInventory;
import eisenwave.vv.ui.user.VVInventoryVariable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FileBrowserMenu extends Menu {
    
    //private final static Button BACKGROUND = new Button(Material.STAINED_GLASS_PANE, 1, (short) 15, " ");
    
    private final VVInventory handle;
    private final Language lang;
    
    private FileOptionsWidget fileOptions;
    private FileList fileList;
    
    //private final static int pageSize = 45;
    //private final int pages = 1;
    //private int page;
    
    public FileBrowserMenu(@NotNull VVInventory handle) {
        super(54, ChatColor.BOLD + "File Browser");
        this.handle = handle;
        this.lang = handle.getOwner().getVoxelVert().getLanguage();
        
        initWidgets();
    }
    
    private void initWidgets() {
        MenuPane contentPane = getContentPane();
        
        this.fileList = new FileList(this, new ViewSize(ViewSize.MATCH_PARENT, 5));
        this.fileList.addAll(entriesOf(handle));
        contentPane.addChild(fileList);
        
        initFileOptions();
        initNavigator();
    }
    
    private void refreshFileList() {
        int offset = this.fileList.getOffset();
        this.fileList.clearChildren();
        this.fileList.addAll(entriesOf(handle));
        this.fileList.setOffset(offset);
    
        setOptionsMode(FileOptionsMode.DEFAULT);
    }
    
    private void initNavigator() {
        Pane separatorPane = new Pane(this, null);
        separatorPane.setItem(ItemUtil.create(Material.STAINED_GLASS_PANE, 1, (short) 15, " "));
        separatorPane.setPosition(5, ViewSize.MAX_POS);
        getContentPane().addChild(separatorPane);
        
        if (fileList.getLength() > fileList.getArea()) {
            PageNavigatorCompound navigator = new PageNavigatorCompound(this, null, fileList);
            navigator.setPosition(6, ViewSize.MAX_POS);
            getContentPane().addChild(navigator);
            separatorPane.setSize(1, 1);
        }
        else {
            separatorPane.setSize(ViewSize.MATCH_PARENT, 1);
        }
    }
    
    private void initFileOptions() {
        this.fileOptions = new FileOptionsWidget(this);
        fileOptions.setPosition(ViewSize.MIN_POS, ViewSize.MAX_POS); // bottom-left corner
        getContentPane().addChild(fileOptions);
        //fileOptions.setEnabled(true);
    }
    
    // GET & SET
    
    /**
     * Returns the file system of this browser.
     *
     * @return the file system
     */
    public VVInventory getFileSystem() {
        return handle;
    }
    
    public Language getLanguage() {
        return lang;
    }
    
    /*
     * Returns the selected entry or {@code null} if there is no selected entry.
     *
     * @return the selected entry
     *
    public FileBrowserEntry getSelected() {
        return fileList.getSelEntry();
    }
    */
    
    public void setOptionsMode(@NotNull FileOptionsMode mode) {
        this.fileOptions.setMode(mode);
    }
    
    /**
     * Lets the player upload a file.
     *
     * @param player the player who performs this action
     */
    public void performUpload(Player player) {
        player.performCommand("vv upload");
    }
    
    /**
     * Deletes the currently selected item.
     *
     * @param player the player who performs this action
     */
    public void performOpen(Player player) {
        if (!fileList.hasSelection()) return;
        
        FileBrowserEntry entry = fileList.getSelEntry();
        player.performCommand("vv convert " + entry.getPath());
    }
    
    /**
     * Shares the currently selected item.
     *
     * @param recipient the recipient to share the item with
     * @param player the player who performs this action
     */
    public void performShare(String recipient, Player player) {
        if (!fileList.hasSelection()) return;
        
        String path = fileList.getSelEntry().getPath();
        player.performCommand("vv-share " + recipient + " " + path);
    }
    
    /**
     * Deletes the currently selected item.
     *
     * @param player the player who performs this action
     */
    public void performDelete(Player player) {
        if (!fileList.hasSelection()) return;
        
        String path = fileList.getSelEntry().getPath();
        player.performCommand("vv-rm " + path);
        refreshFileList();
    }
    
    /**
     * Renames the currently selected item.
     *
     * @param player the player who performs this action
     */
    public void performRename(Player player, String target) {
        if (!fileList.hasSelection()) return;
        
        String source = fileList.getSelEntry().getPath();
        player.performCommand("vv-mv " + source + " " + target);
        refreshFileList();
    }
    
    /**
     * Renames the currently selected item.
     *
     * @param player the player who performs this action
     */
    public void performCopy(Player player, String target) {
        if (!fileList.hasSelection()) return;
        
        String source = fileList.getSelEntry().getPath();
        player.performCommand("vv-cp " + source + " " + target);
        refreshFileList();
    }
    
    /*
    public void performRename() {
        int index = fileList.getSelIndex();
        if (index > -1) {
            FileButton button = fileList.removeFile(index);
            String path = button.getEntry().getPath();
            getFileSystem().delete(path);
        }
    }
    */
    
    // MENU IMPL
    
    @Override
    public MenuResponse performClick(Player player, int x, int y, ClickType click) {
        MenuResponse response = super.performClick(player, x, y, click);
        if (response == MenuResponse.EMPTY) {
            fileList.select(-1);
            setOptionsMode(FileOptionsMode.DEFAULT);
        }
        return response;
    }
    
    // UTIL
    
    private static FileBrowserEntry[] entriesOf(VVInventory inventory) {
        List<FileBrowserEntry> entries = new ArrayList<>();
        for (String name : inventory.list()) {
            FileBrowserEntry entry = new FileBrowserEntry(name);
            if (entry.isHidden()) continue;
            if (entry.isVariable()) {
                VVInventoryVariable var = inventory.getVariable(entry.getName());
                if (var == null || !var.isSet()) continue;
            }
            entries.add(entry);
        }
        
        entries.sort(null);
        return entries.toArray(new FileBrowserEntry[entries.size()]);
    }
    
}
