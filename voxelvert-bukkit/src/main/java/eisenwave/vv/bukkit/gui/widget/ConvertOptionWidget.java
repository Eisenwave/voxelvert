package eisenwave.vv.bukkit.gui.widget;

import eisenwave.inv.menu.Menu;
import eisenwave.inv.view.*;
import eisenwave.inv.widget.*;
import eisenwave.vv.bukkit.gui.FileBrowserEntry;
import eisenwave.vv.bukkit.gui.FileType;
import eisenwave.vv.bukkit.gui.menu.ConvertMenu;
import eisenwave.vv.ui.fmtvert.Formatverter;
import eisenwave.spatium.enums.Face;
import eisenwave.vv.ui.user.VVUser;
import eisenwave.vv.ui.util.Sets;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import eisenwave.inv.util.ItemUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class ConvertOptionWidget extends ViewGroup<View> {
    
    private final static Set<String> KNOWN = Sets.ofArray("R", "d", "v", "C"/*, "c"*/);
    
    private final static ItemStack
        ITEM_RESOLUTION = ItemUtil.create(Material.MOB_SPAWNER, ChatColor.RESET + "Resolution", "&8-R"),
        ITEM_DIRECTION = ItemUtil.create(Material.COMPASS, ChatColor.RESET + "Direction", "&8-d"),
        ITEM_VERBOSITY = ItemUtil.create(Material.JUKEBOX, ChatColor.RESET + "Verbosity", "&8-v"),
        ITEM_CROP = ItemUtil.create(Material.SHEARS, ChatColor.RESET + "Crop", "&8-C"),
        ITEM_COLORS = ItemUtil.create(Material.INK_SACK, 1, (short) 1, ChatColor.RESET + "Colors", "&8-c"),
        ITEM_UNKNOWN = ItemUtil.create(Material.STRUCTURE_VOID, ChatColor.RESET + "???");
    
    @Contract(pure = true)
    public static boolean isKnownOption(String option) {
        return KNOWN.contains(option);
    }
    
    private final String option;
    private final ItemStack optionDisplay;
    private final VVUser user;
    
    @Nullable
    private String value;
    
    public ConvertOptionWidget(ConvertMenu menu, VVUser user, @NotNull String option) {
        super(menu, new ViewSize(ViewSize.MATCH_PARENT, 1, false, false));
        this.user = user;
        this.option = option;
        
        switch (option) {
            case "R":
                optionDisplay = ITEM_RESOLUTION;
                initOptionResolution();
                break;
            case "d":
                optionDisplay = ITEM_DIRECTION;
                initOptionDirection();
                break;
            case "c":
                optionDisplay = ITEM_COLORS;
                initOptionColors();
                break;
            case "v":
                optionDisplay = ITEM_VERBOSITY;
                initFlagOption();
                break;
            case "C":
                optionDisplay = ITEM_CROP;
                initFlagOption();
                break;
            default:
                optionDisplay = ItemUtil.withLore(ITEM_UNKNOWN, ChatColor.DARK_GRAY + "-" + option);
        }
        
        initDisplay();
    }
    
    private void initDisplay() {
        Display display = new Display(getMenu(), null);
        display.setItem(optionDisplay);
        addChild(display);
    }
    
    private void initOptionResolution() {
        List<String> values = Arrays.stream(new int[] {8, 16, 32, 64, 128})
            .mapToObj(Integer::toString)
            .collect(Collectors.toList());
        
        initSwitchOption(values);
    }
    
    private void initOptionDirection() {
        List<String> values = Arrays.stream(Face.values())
            .map(Object::toString)
            .map(String::toLowerCase)
            .collect(Collectors.toList());
        
        initSwitchOption(values);
    }
    
    private void initOptionColors() {
        Menu menu = getMenu();
        ViewSize size = new ViewSize(2, ViewSize.MIN_POS, ViewSize.MATCH_PARENT, 1);
        InlineList<RadioButton> list = new InlineList<>(menu, size, null);
        
        // get all usable BCT files from the inventory
        List<FileBrowserEntry> entries = user.getInventory().list().stream()
            .map(FileBrowserEntry::new)
            .filter(entry -> !entry.isHidden() && entry.getType() == FileType.BCT)
            .sorted()
            .collect(Collectors.toList());
        
        // convert all BCT files into radio buttons and add them to the InlineList
        boolean first = true;
        int index = 1;
        for (FileBrowserEntry entry : entries) {
            RadioButton button = new RadioButton(menu, null);
            ItemStack checked = ItemUtil.setName(button.getCheckedItem(),
                ChatColor.GREEN + entry.getDisplayName(false));
            ItemStack unchecked = ItemUtil.setName(button.getUncheckedItem(),
                ChatColor.DARK_GRAY + entry.getDisplayName(false));
            
            checked.setAmount(index);
            unchecked.setAmount(index);
            button.setCheckedItem(checked);
            button.setUncheckedItem(unchecked);
            button.addCheckListener(event -> value = entry.getPath());
            
            list.addChild(button);
            
            if (first) {
                value = entry.getPath();
                button.setChecked(true);
                first = false;
            }
            index++;
        }
        
        // "hacky" toggle group for all buttons
        for (RadioButton radioButton : list) {
            radioButton.addCheckListener(event -> {
                list.forEach(otherButton -> {
                    if (radioButton != otherButton)
                        otherButton.setChecked(false);
                });
                list.invalidate();
            });
        }
        
        addChild(list);
    }
    
    private void initSwitchOption(List<String> values) {
        ViewSize size = new ViewSize(2, ViewSize.MIN_POS, ViewSize.MATCH_PARENT, 1);
        RadioList list = new RadioList(getMenu(), size);
        
        boolean first = true;
        for (String str : values) {
            RadioButton button = new RadioButton(getMenu(), null);
            list.addChild(button);
    
            ItemStack checked = ItemUtil.setName(button.getCheckedItem(), ChatColor.GREEN + str);
            button.setCheckedItem(checked);
            ItemStack unchecked = ItemUtil.setName(button.getUncheckedItem(), ChatColor.DARK_GRAY + str);
            button.setUncheckedItem(unchecked);
            
            button.addCheckListener(event -> {
                if (event.isChecked())
                    value = str;
            });
            
            if (first) {
                value = str;
                button.setChecked(true);
                first = false;
            }
        }
        addChild(list);
    }
    
    private void initFlagOption() {
        CheckBox button = new CheckBox(getMenu(), null);
        button.setPosition(2, 0);
        addChild(button);
        
        button.addCheckListener(event -> value = event.isChecked()? "" : null);
    }
    
    @Override
    public ConvertMenu getMenu() {
        return (ConvertMenu) super.getMenu();
    }
    
    public String getOption() {
        return option;
    }
    
    /**
     * Returns the picked argument.
     * <p>
     * A value of {@code null} represents that the option should be ignored and not given to the
     * {@link Formatverter}.
     * <p>
     * A value of {@code ""} represents that the option should be used without any arguments. An example of this is the
     * verbosity option {@code -v} which toggles verbosity during conversion.
     *
     * @return the argument or null if the option should be ignored
     */
    @Nullable
    public String getArgument() {
        return value;
    }
    
}
