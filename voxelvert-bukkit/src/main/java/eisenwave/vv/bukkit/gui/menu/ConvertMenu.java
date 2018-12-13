package eisenwave.vv.bukkit.gui.menu;

import eisenwave.inv.event.ClickListener;
import eisenwave.inv.menu.Menu;
import eisenwave.inv.menu.MenuManager;
import eisenwave.inv.menu.MenuResponse;
import eisenwave.inv.util.LegacyUtil;
import eisenwave.inv.view.ViewGroup;
import eisenwave.inv.view.ViewSize;
import eisenwave.inv.widget.*;
import eisenwave.vv.ui.fmtvert.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import eisenwave.vv.bukkit.VoxelVertPlugin;
import eisenwave.vv.bukkit.async.VoxelVertQueue;
import eisenwave.vv.bukkit.gui.FileType;
import eisenwave.vv.bukkit.gui.widget.ConvertOptionWidget;
import eisenwave.vv.bukkit.user.BukkitVoxelVert;
import eisenwave.vv.bukkit.util.CommandUtil;
import eisenwave.inv.util.ItemUtil;
import eisenwave.vv.object.Language;
import eisenwave.vv.ui.cmd.VoxelVertTask;
import eisenwave.vv.ui.user.VVInventory;
import eisenwave.vv.ui.user.VVUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class ConvertMenu extends Menu {
    
    private final static Material
        END_CRYSTAL = LegacyUtil.getByMinecraftKey13("end_crystal").getMaterial();
    
    private final static ItemStack
        ITEM_CANCEL = ItemUtil.create("red_wool", ChatColor.RED + "Cancel", "&7Back to Inventory"),
        ITEM_CONFIRM_OFF = ItemUtil.create("gray_wool", ChatColor.DARK_GRAY + "Confirm", "&7Convert"),
        ITEM_CONFIRM_ON = ItemUtil.create("lime_wool", ChatColor.GREEN + "Confirm", "&7Convert"),
        ITEM_PROGRESS_ON = ItemUtil.create("lime_stained_glass_pane", " "),
        ITEM_PROGRESS_OFF = ItemUtil.create("black_stained_glass_pane", " "),
        ITEM_PROGRESS_FAIL = ItemUtil.create("red_stained_glass_pane", ChatColor.RED + "ERROR"),
        ITEM_PANE = ItemUtil.create("black_stained_glass_pane", " ");
    
    // INIT
    
    private final ClickListener returnToFileBrowser;
    
    private ViewGroup<ConvertOptionWidget> optionsGroup;
    private ButtonPane confirmButton;
    private ProgressBar progressBar;
    
    private State state = State.NO_FORMAT;
    
    private final VVUser user;
    private String sourcePath, targetPath;
    private Format sourceFormat, targetFormat;
    
    private long startTime;
    
    public ConvertMenu(VVUser user, @NotNull String name, @NotNull Format format) {
        super(Menu.MAX_SIZE, ChatColor.BOLD + "Convert: " + ChatColor.RESET + continuedName(name, 16));
        this.user = user;
        this.sourcePath = name;
        this.sourceFormat = format;
        this.returnToFileBrowser = event -> {
            Menu menu = new FileBrowserMenu(user.getInventory());
            MenuManager.getInstance().startSession(event.getPlayer(), menu);
        };
        
        initFormatPicker(format);
        initCancel();
        initConfirm();
        initSeparator();
        initOptionsGroup();
    }
    
    private void initFormatPicker(Format sourceFormat) {
        Format[] options = FormatverterFactory.getInstance().getOutputFormats(sourceFormat);
        Arrays.sort(options, (x, y) ->
            x.equals(sourceFormat)? -1 :
                y.equals(sourceFormat)? 1 : x.getId().compareTo(y.getId()));
        
        RadioList list = new RadioList(this, new ViewSize(ViewSize.MATCH_PARENT, 1));
        Language lang = getUser().getVoxelVert().getLanguage();
        
        for (Format format : options) {
            RadioButton child = new RadioButton(this, null);
            final ItemStack unchecked, checked;
            {
                FileType type = FileType.fromFormat(format);
                final String icon, prefix, formatName;
                if (type == null) {
                    icon = "white_shulker_box";
                    prefix = ChatColor.RESET.toString();
                    formatName = ChatColor.GRAY + lang.get("media.application/x-binary");
                }
                else {
                    icon = type.getIcon();
                    prefix = type.getPrefix();
                    formatName = ChatColor.GRAY + lang.get(type.getLanguageName());
                }
                
                String name = prefix + lang.get("format." + format.getId());
                if (format.equals(sourceFormat))
                    name += " " + lang.get("menu.convert.copy");
                List<String> lore = Collections.singletonList(formatName);
                
                unchecked = ItemUtil.create(icon, 1, name, lore);
                checked = ItemUtil.create(END_CRYSTAL, 1, name, lore);
            }
            //checked = unchecked.clone();
            //unchecked.setType(LegacyUtil.getByMinecraftKey13("end_crystal").getMaterial());
            
            child.setCheckedItem(checked);
            child.setUncheckedItem(unchecked);
            
            child.addCheckListener(event -> {
                if (event.isChecked())
                    this.performFormatPick(event.getPlayer(), format);
            });
            
            list.addChild(child);
        }
        
        getContentPane().addChild(list);
        
        int length = list.getLength();
        if (length < 9) {
            Pane pane = new Pane(this, new ViewSize(ViewSize.MAX_POS, 0, 9 - length, 1), null);
            pane.setItem(ITEM_PANE);
            getContentPane().addChild(pane);
        }
    }
    
    private void initCancel() {
        ViewSize size = new ViewSize(ViewSize.MIN_POS, ViewSize.MAX_POS, 2, 1);
        ButtonPane button = new ButtonPane(this, size, null);
        button.setItem(ITEM_CANCEL);
        getContentPane().addChild(button);
        
        button.addClickListener(returnToFileBrowser);
    }
    
    private void initConfirm() {
        ViewSize size = new ViewSize(ViewSize.MAX_POS, ViewSize.MAX_POS, 2, 1);
        confirmButton = new ButtonPane(this, size, null);
        confirmButton.setItem(ITEM_CONFIRM_OFF);
        getContentPane().addChild(confirmButton);
        
        confirmButton.addClickListener(event -> performConvert(event.getPlayer()));
    }
    
    private void initSeparator() {
        ViewSize size = new ViewSize(2, ViewSize.MAX_POS, 5, 1);
        Pane pane = new Pane(this, size, null);
        pane.setItem(ITEM_PANE);
        getContentPane().addChild(pane);
    }
    
    private void initOptionsGroup() {
        this.optionsGroup = new ViewGroup<>(this, new ViewSize(0, 1, ViewSize.MATCH_PARENT, 4));
        getContentPane().addChild(optionsGroup);
    }
    
    private void initProgressBar() {
        ViewSize size = new ViewSize(ViewSize.MIN_POS, ViewSize.MAX_POS, ViewSize.MATCH_PARENT, 1);
        progressBar = new ProgressBar(this, size, null);
        progressBar.setOffItem(ITEM_PROGRESS_OFF);
        progressBar.setOnItem(ITEM_PROGRESS_ON);
        
        getContentPane().addChild(progressBar);
    }
    
    private void initFinishButton() {
        getContentPane().removeChild(progressBar);
        
        ViewSize size = new ViewSize(ViewSize.MIN_POS, ViewSize.MAX_POS, ViewSize.MATCH_PARENT, 1);
        ButtonPane button = new ButtonPane(this, size, null);
        
        String name = ChatColor.GREEN + "Done!";
        String lore = "&8Result was saved as:\n&7" + targetPath + "\n\n&aClick to return";
        ItemStack item = ItemUtil.setName(ItemUtil.withInlineLore(ITEM_CONFIRM_ON, lore), name);
        button.setItem(item);
        getContentPane().addChild(button);
        
        button.addClickListener(returnToFileBrowser);
    }
    
    // ACTIONS
    
    private void setDone() {
        this.state = State.DONE;
        
        long duration = System.currentTimeMillis() - startTime;
        user.printLocalized("menu.convert.done", duration, duration / 1000f);
        
        initFinishButton();
    }
    
    private void setFailed(String error) {
        this.state = State.FAILED;
    
        user.error("Converting failed: " + error);
        progressBar.setOnItem(ITEM_PROGRESS_FAIL);
        progressBar.setOffItem(ITEM_PROGRESS_FAIL);
    }
    
    @Override
    public MenuResponse performClick(Player player, int x, int y, ClickType click) {
        //System.out.println(player+" "+state+" "+x+" "y+" ");
        if (state == State.CONVERTING)
            return MenuResponse.BLOCK;
        if (state.isFinal() && y != getHeight() - 1)
            return MenuResponse.BLOCK;
        else
            return super.performClick(player, x, y, click);
    }
    
    @SuppressWarnings("unused")
    public void performFormatPick(Player player, Format targetFormat) {
        optionsGroup.clearChildren();
        
        Formatverter fv = FormatverterFactory.getInstance().fromFormats(sourceFormat, targetFormat);
        assert fv != null;
        List<ConvertOptionWidget> widgets = fv.getAllOptions()
            .stream()
            .map(Option::getId)
            .filter(ConvertOptionWidget::isKnownOption)
            .sorted()
            .map(optionId -> new ConvertOptionWidget(ConvertMenu.this, user, optionId))
            .filter(ConvertOptionWidget::isInitialized)
            .collect(Collectors.toList());
        int size = widgets.size();
        
        int lim = Math.min(4, size);
        for (int i = 0; i < lim; i++) {
            ConvertOptionWidget widget = widgets.get(i);
            widget.setPosition(ViewSize.MIN_POS, i);
            optionsGroup.addChild(widget);
        }
        
        if (state == State.NO_FORMAT) {
            this.state = State.READY;
        }
        
        this.targetFormat = targetFormat;
        setTargetPath();
        
        String lore = "&8Result will be saved as:\n&7" + targetPath;
        confirmButton.setItem(ItemUtil.withInlineLore(ITEM_CONFIRM_ON, lore));
    }
    
    private void setTargetPath() {
        VVInventory inventory = user.getInventory();
        String name = nameWithoutExtensionOf(sourcePath)
            .replace(' ', '_')
            .replace("#", "");
        String ext = "." + extensionOf(targetFormat);
        
        this.targetPath = name + ext;
        if (inventory.contains(targetPath)) {
            boolean isNamedCopy = name.endsWith("_copy");
            if (!isNamedCopy) {
                targetPath = name + "_copy" + ext;
            }
            for (int i = 1; inventory.contains(targetPath); i++) {
                targetPath = name + (isNamedCopy? "_" : "_copy_") + i + ext;
            }
        }
    }
    
    @SuppressWarnings("unused")
    public void performConvert(Player player) {
        if (state != State.READY) return;
        
        BukkitVoxelVert vv = VoxelVertPlugin.getInstance().getVoxelVert();
        VoxelVertQueue queue = vv.getQueue();
        
        Formatverter fv = FormatverterFactory.getInstance().fromFormats(sourceFormat, targetFormat);
        assert fv != null;
        Map<String, String> options = getOptions();
        
        initProgressBar();
        
        ProgressListener listener = (now, max, relative) -> progressBar.setProgress(relative);
        
        VoxelVertTask task = new VoxelVertTask(user, sourceFormat, sourcePath, targetFormat, targetPath) {
            @Override
            public void run() throws Exception {
                try {
                    fv.addListener(listener);
                    fv.convert(user, sourcePath, targetPath, options);
                    setDone();
                } catch (Exception ex) {
                    setFailed(ex.getMessage());
                    throw ex;
                } finally {
                    fv.removeListener(listener);
                }
            }
            
            @Override
            protected int getMaxProgress() {
                return fv.getMaxProgress();
            }
        };
        
        state = State.CONVERTING;
        queue.add(task);
        startTime = System.currentTimeMillis();
    }
    
    // GETTERS
    
    public VVUser getUser() {
        return user;
    }
    
    public String getSourcePath() {
        return sourcePath;
    }
    
    public Format getSourceFormat() {
        return sourceFormat;
    }
    
    public Map<String, String> getOptions() {
        Map<String, String> result = new HashMap<>();
        for (ConvertOptionWidget widget : optionsGroup) {
            String arg = widget.getArgument();
            //Bukkit.broadcastMessage(widget.getOption()+" = "+arg);
            if (arg != null)
                result.put(widget.getOption(), widget.getArgument());
        }
        
        return result;
    }
    
    /**
     * Returns the target format or null if none has been selected yet.
     *
     * @return the target format
     */
    @Nullable
    public Format getTargetFormat() {
        return targetFormat;
    }
    
    // CLASSES
    
    private static enum State {
        NO_FORMAT,
        READY,
        CONVERTING,
        DONE,
        FAILED;
        
        public boolean isFinal() {
            return this == DONE || this == FAILED;
        }
    }
    
    // UTIL
    
    private static String nameWithoutExtensionOf(String filePath) {
        return CommandUtil.nameAndExtensionOf(filePath)[0];
    }
    
    private static String extensionOf(Format format) {
        if (!format.isFile())
            throw new IllegalArgumentException(format + " is not a file format");
        
        if (format.equals(Format.IMAGE)) return "png";
        String[] extensions = format.getExtensions();
        return extensions.length == 0? "out" : extensions[0];
    }
    
    private static String continuedName(String name, int lim) {
        return (name.length() > lim)?
            name.substring(0, lim) + "..." :
            name;
    }
    
}
