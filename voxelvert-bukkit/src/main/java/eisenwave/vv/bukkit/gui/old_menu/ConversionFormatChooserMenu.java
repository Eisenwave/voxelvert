package eisenwave.vv.bukkit.gui.old_menu;

import eisenwave.vv.ui.fmtvert.Format;
import eisenwave.vv.bukkit.gui.FileType;
import eisenwave.vv.bukkit.gui.old_widget.LargeButtonWidget;
import eisenwave.vv.bukkit.gui.old_widget.ParameterOptionsWidget;
import nl.klikenklaar.util.gui.buttons.Button;
import nl.klikenklaar.util.gui.buttons.CommandButton;
import nl.klikenklaar.util.gui.menus.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import eisenwave.vv.ui.fmtvert.FormatverterFactory;

@Deprecated
public class ConversionFormatChooserMenu extends Menu {
    
    private final static Button
        BUTTON_CANCEL = new CommandButton(Material.CONCRETE, 1, (short) 14, ChatColor.RED+"Cancel", "vv gui"),
        BUTTON_CONFIRM_OFF = new Button(Material.CONCRETE, 1, (short) 13, ChatColor.DARK_GRAY+"Confirm"),
        BUTTON_PARAM = new Button(Material.MAGENTA_GLAZED_TERRACOTTA, ChatColor.DARK_GRAY+"Confirm");
    
    public ConversionFormatChooserMenu(Format format, String name) {
        super(54, name);
        
        drawInfoButton(name);
        drawLargeButtons();
        drawOptionsWidget(format);
    }
    
    private void drawInfoButton(String name) {
        Material material = FileType.fromPath(name).getIcon();
        
        setButton(new Button(material, name), 5, 6);
    }
    
    private void drawLargeButtons() {
        new LargeButtonWidget(this, BUTTON_CANCEL,      2, 2, 0, 5).draw();
        new LargeButtonWidget(this, BUTTON_CONFIRM_OFF, 2, 2, 8, 5).draw();
    }
    
    private void drawOptionsWidget(Format format) {
        Format[] formats = FormatverterFactory.getInstance().getOutputFormats(format);
        String[] options = new String[formats.length];
        for (int i = 0; i < options.length; i++)
            options[i] = formats[i].getId();
        
        ParameterOptionsWidget optionsWidget = new ParameterOptionsWidget(this, BUTTON_PARAM, options);
        optionsWidget.draw();
        optionsWidget.addActionListener((index,option) -> drawConfirmButton(option));
    }
    
    private void drawConfirmButton(String format) {
        String command = String.format("vv convert %s", format);
        Button button = new CommandButton(Material.CONCRETE, 1, (short) 5, ChatColor.GREEN+"Confirm", command);
        new LargeButtonWidget(this, button, 2, 2, 8, 5).draw();
    }
    
}
