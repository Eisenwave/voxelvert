package org.eisenwave.vv.bukkit.gui.old_menu;

import org.eisenwave.vv.bukkit.gui.FileType;
import org.eisenwave.vv.bukkit.gui.old_widget.LargeButtonWidget;
import org.eisenwave.vv.bukkit.gui.old_widget.ParameterOptionsWidget;
import nl.klikenklaar.util.gui.buttons.Button;
import nl.klikenklaar.util.gui.buttons.CommandButton;
import nl.klikenklaar.util.gui.menus.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.eisenwave.vv.bukkit.gui.old_widget.PagedRowListLayout;
import org.eisenwave.vv.ui.fmtvert.Format;
import org.eisenwave.vv.ui.fmtvert.Formatverter;
import org.eisenwave.vv.ui.fmtvert.FormatverterFactory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class ConversionOptionsMenu extends Menu {
    
    private final static Button
        BUTTON_CANCEL = new CommandButton(Material.CONCRETE, 1, (short) 14, ChatColor.RED+"Cancel", "vv gui"),
        BUTTON_CONFIRM_OFF = new Button(Material.CONCRETE, 1, (short) 13, ChatColor.DARK_GRAY+"Confirm");
    
    private PagedRowListLayout optionsLayout;
    
    public ConversionOptionsMenu(String in, String out, Format inFormat, Format outFormat) {
        super(54, String.format(ChatColor.BOLD+"%s => %s", inFormat, outFormat));
        
        drawInfoButtons(in, out);
        drawLargeButtons();
        //drawParameterLayout(inFormat, outFormat);
        
        if (this.optionsLayout.getPages() > 0) {
            drawPageButtons(this.optionsLayout.getPage());
        }
    }
    
    private void drawInfoButtons(String in, String out) {
        Material mIn = FileType.fromPath(in).getIcon();
        Material mOut = FileType.fromPath(in).getIcon();
        
        setButton(new Button(mIn, in), 4, 6);
        setButton(new Button(mOut, out), 6, 6);
    }
    
    private void drawLargeButtons() {
        new LargeButtonWidget(this, BUTTON_CANCEL,      2, 2, 0, 5).draw();
        new LargeButtonWidget(this, BUTTON_CONFIRM_OFF, 2, 2, 8, 5).draw();
    }
    
    /*
    private void drawParameterLayout(Format source, Format target) {
        Formatverter fv = FormatverterFactory.getInstance().fromFormats(source, target);
    
        List<ParameterOptionsWidget> widgets = new ArrayList<>();
        for (String opt : fv.getMandatoryOptions()) {
            ParameterOptionsWidget widget = optionsWidgetOf(opt);
            if (widget != null) widgets.add(widget);
        }
        for (String opt : fv.getOptionalOptions()) {
            ParameterOptionsWidget widget = optionsWidgetOf(opt);
            if (widget != null) widgets.add(widget);
        }
        
        this.optionsLayout = new PagedRowListLayout(this, widgets, 4);
        this.optionsLayout.draw();
    }
    */
    
    private void drawPageButtons(int page) {
        if (page > 0)
            setButton(new PageNavigatorButton(page-1), 3, 5);
        if (page+1 < optionsLayout.getPages())
            setButton(new PageNavigatorButton(page+1), 7, 5);
    }
    
    // MUTATORS
    
    private void setPage(int page) {
        this.optionsLayout.setPage(page);
        drawPageButtons(page);
    }
    
    // UTIL
    
    @Nullable
    private static ParameterOptionsWidget optionsWidgetOf(String option) {
        return null;
    }
    
    // CLASSES
    
    private class PageNavigatorButton extends Button {
        
        private final int targetPage;
        
        public PageNavigatorButton(int targetPage) {
            super(Material.ARROW, String.format("To Page %d", targetPage+1));
            this.targetPage = targetPage;
        }
        
        @Override
        public boolean[] onClick(Player player) {
            ConversionOptionsMenu.this.setPage(targetPage);
            return new boolean[] {true, false};
        }
        
    }
    
}
