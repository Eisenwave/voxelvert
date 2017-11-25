package eisenwave.vv.bukkit.gui.widget;

import eisenwave.inv.view.*;
import eisenwave.inv.widget.*;
import eisenwave.vv.bukkit.gui.menu.ConvertMenu;
import eisenwave.vv.ui.fmtvert.Formatverter;
import net.grian.spatium.enums.Face;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import eisenwave.inv.util.ItemInitUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConvertOptionWidget extends ViewGroup<View> {
    
    private final static ItemStack
        ITEM_RESOLUTION = ItemInitUtil.create(Material.MOB_SPAWNER, ChatColor.RESET + "Resolution", "&8-R"),
        ITEM_DIRECTION = ItemInitUtil.create(Material.COMPASS, ChatColor.RESET + "Direction", "&8-d"),
        ITEM_VERBOSITY = ItemInitUtil.create(Material.JUKEBOX, ChatColor.RESET + "Verbosity", "&8-v"),
        ITEM_CROP = ItemInitUtil.create(Material.SHEARS, ChatColor.RESET + "Crop", "&8-C"),
        ITEM_UNKNOWN = ItemInitUtil.create(Material.STRUCTURE_VOID, ChatColor.RESET + "???");
    
    private final String option;
    private final ItemStack optionDisplay;
    
    @Nullable
    private String value;
    
    public ConvertOptionWidget(ConvertMenu menu, @NotNull String option) {
        super(menu, new ViewSize(ViewSize.MATCH_PARENT, 1, false, false));
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
            case "v":
                optionDisplay = ITEM_VERBOSITY;
                initFlagOption();
                break;
            case "C":
                optionDisplay = ITEM_CROP;
                initFlagOption();
                break;
            default:
                optionDisplay = ItemInitUtil.withLore(ITEM_UNKNOWN, ChatColor.DARK_GRAY + "-" + option);
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
    
    private void initSwitchOption(List<String> values) {
        ViewSize size = new ViewSize(2, ViewSize.MIN_POS, ViewSize.MATCH_PARENT, 1);
        RadioList list = new RadioList(getMenu(), size);
        
        boolean first = true;
        for (String str : values) {
            RadioButton button = new RadioButton(getMenu(), null);
            list.addChild(button);
            
            ItemStack checked = ItemInitUtil.setName(button.getCheckedItem(), ChatColor.GREEN + str);
            button.setCheckedItem(checked);
            ItemStack unchecked = ItemInitUtil.setName(button.getUncheckedItem(), ChatColor.DARK_GRAY + str);
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
