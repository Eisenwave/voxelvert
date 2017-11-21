package org.eisenwave.vv.bukkit.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public final class ItemInitUtil {
    
    private ItemInitUtil() {}
    
    @NotNull
    public static ItemStack item(Material material, String name) {
        return item(material, 1, (short) 0, name, (List) null);
    }
    
    @NotNull
    public static ItemStack item(Material material, String name, List<String> lore) {
        return item(material, 1, (short) 0, name, lore);
    }
    
    @NotNull
    public static ItemStack item(Material material, String name, String lore) {
        return item(material, 1, (short) 0, name, lore);
    }
    
    @NotNull
    public static ItemStack item(Material material, int count, String name) {
        return item(material, count, (short) 0, name, (List) null);
    }
    
    @NotNull
    public static ItemStack item(Material material, short damage) {
        return item(material, 1, damage, null, ((List) null));
    }
    
    @NotNull
    public static ItemStack item(Material material, short damage, String name) {
        return item(material, 1, damage, name, (List) null);
    }
    
    @NotNull
    public static ItemStack item(Material material, int count, short damage, @Nullable String name) {
        return item(material, count, damage, name, (List) null);
    }
    
    @NotNull
    public static ItemStack item(Material material, int count, short damage,
                                 @Nullable String name,
                                 @Nullable String lore) {
        List<String> loreList = lore == null || lore.equals("")? null :
            Arrays.stream(lore.split("\n")).collect(Collectors.toList());
        return item(material, count, damage, name, loreList);
    }
    
    @NotNull
    public static ItemStack item(Material material, int count, short damage,
                                 @Nullable String name,
                                 @Nullable List<String> lore) {
        ItemStack item = new ItemStack(material, count, damage);
        ItemMeta meta = item.getItemMeta();
        if (name != null)
            meta.setDisplayName(name);
        if (lore != null) {
            lore = lore.stream()
                .map(str -> ChatColor.translateAlternateColorCodes('&', str))
                .collect(Collectors.toList());
            meta.setLore(lore);
        }
        
        item.setItemMeta(meta);
        return item;
    }
    
    public static ItemStack setLoreLines(ItemStack stack, String... lore) {
        ItemMeta meta = stack.getItemMeta();
        meta.setLore(Arrays.asList(lore));
        stack.setItemMeta(meta);
        return stack;
    }
    
    public static ItemStack setLoreLines(ItemStack stack, String lore) {
        ItemMeta meta = stack.getItemMeta();
        meta.setLore(Collections.singletonList(lore));
        stack.setItemMeta(meta);
        return stack;
    }
    
    public static ItemStack withLoreLines(ItemStack stack, String... lore) {
        return setLoreLines(stack.clone(), lore);
    }
    
    public static ItemStack withLoreLines(ItemStack stack, String lore) {
        return setLoreLines(stack.clone(), lore);
    }
    
    public static ItemStack setName(ItemStack stack, String name) {
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        stack.setItemMeta(meta);
        return stack;
    }
    
    public static ItemStack withName(ItemStack stack, String name) {
        return setName(stack.clone(), name);
    }
    
}
