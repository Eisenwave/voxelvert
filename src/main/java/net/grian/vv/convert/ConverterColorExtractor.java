package net.grian.vv.convert;

import net.grian.vv.cache.ColorMap;
import net.grian.vv.core.BlockColor;
import net.grian.vv.core.BlockKey;
import net.grian.vv.util.Colors;
import org.bukkit.Material;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ConverterColorExtractor implements Converter<ZipFile, ColorMap> {

    public final static List<ExtractableColor> colorList = new ArrayList<>();

    private static class ExtractableColor {

        private final BlockKey block;
        private final Extractor extractor;
        private final float occupation;
        private final boolean tint;

        private ExtractableColor(BlockKey block, Extractor extractor, float occupation, boolean tint) {
            this.block = block;
            this.extractor = extractor;
            this.occupation = occupation;
            this.tint = tint;
        }

    }

    private static void add(BlockKey key, int color, float occupation, boolean tint) {
        colorList.add(new ExtractableColor(key, Extractors.constantColor(color), occupation, tint));
    }

    private static void add(BlockKey key, String texturePath, float occupation, boolean tint) {
        colorList.add(new ExtractableColor(key, Extractors.textureColor(texturePath), occupation, tint));
    }

    private static void add(Material material, int data, String texturePath, float occupation, boolean tint) {
        add(new BlockKey(material, data), texturePath, occupation, tint);
    }

    private static void add(int id, int data, String texturePath, float occupation, boolean tint) {
        add(new BlockKey(id, data), texturePath, occupation, tint);
    }

    static {
        add(BlockKey.AIR, Colors.INVISIBLE_WHITE, 0F, false);
        add(BlockKey.STONE, "blocks/stone.png", 1F, false);
        add(new BlockKey(Material.STONE, 1), "blocks/stone_granite.png", 1F, false);
        add(new BlockKey(Material.STONE, 2), "blocks/stone_granite_polished.png", 1F, false);
        add(new BlockKey(Material.STONE, 3), "blocks/stone_diorite.png", 1F, false);
        add(new BlockKey(Material.STONE, 4), "blocks/stone_diorite_polished.png", 1F, false);
        add(new BlockKey(Material.STONE, 5), "blocks/stone_andesite.png", 1F, false);
        add(new BlockKey(Material.STONE, 6), "blocks/stone_andesite_polished.png", 1F, false);
        add(new BlockKey(Material.GRASS, 0), "blocks/grass_top.png", 1F, true);
        add(new BlockKey(Material.DIRT, 0), "blocks/dirt.png", 1F, false);
        add(new BlockKey(Material.DIRT, 1), "blocks/coarse_dirt.png", 1F, false);
        add(new BlockKey(Material.DIRT, 2), "blocks/dirt_podzol_top.png", 1F, false);
        add(new BlockKey(Material.COBBLESTONE, 0), "blocks/cobblestone.png", 1F, false);
        add(new BlockKey(5, 0), "blocks/planks_oak.png", 1F, false);
        add(new BlockKey(5, 1), "blocks/planks_spruce.png", 1F, false);
        add(new BlockKey(5, 2), "blocks/planks_birch.png", 1F, false);
        add(new BlockKey(5, 3), "blocks/planks_jungle.png", 1F, false);
        add(new BlockKey(5, 4), "blocks/planks_acacia.png", 1F, false);
        add(new BlockKey(5, 5), "blocks/planks_big_oak.png", 1F, false);
        add(new BlockKey(Material.SAPLING, 0), "blocks/sapling_oak.png", 0.536376953F, false);
        add(new BlockKey(Material.SAPLING, 1), "blocks/sapling_spruce.png", 0.536376953F, false);
        add(new BlockKey(Material.SAPLING, 2), "blocks/sapling_birch.png", 0.536376953F, false);
        add(new BlockKey(Material.SAPLING, 3), "blocks/sapling_jungle.png", 0.536376953F, false);
        add(new BlockKey(Material.SAPLING, 4), "blocks/sapling_acacia.png", 0.536376953F, false);
        add(new BlockKey(Material.SAPLING, 5), "blocks/sapling_oak.png", 0.536376953F, false);
        add(new BlockKey(Material.BEDROCK, 0), "blocks/bedrock.png", 1F, false);
        add(new BlockKey(Material.STATIONARY_WATER, 0), "blocks/water_still.png", 14F/16F, false);
        add(new BlockKey(Material.WATER, 0), "blocks/water_flow.png", 14F/16F, false);
        add(new BlockKey(Material.STATIONARY_LAVA, 0), "blocks/lava_still.png", 14F/16F, false);
        add(new BlockKey(Material.LAVA, 0), "blocks/lava_flow.png", 14F/16F, false);
        add(new BlockKey(Material.SAND, 0), "blocks/sand.png", 1F, false);
        add(new BlockKey(Material.SAND, 1), "blocks/red_sand.png", 1F, false);
        add(new BlockKey(Material.GRAVEL, 0), "blocks/gravel.png", 1F, false); //13:0
        add(new BlockKey(Material.GOLD_ORE, 0), "blocks/gold_ore.png", 1F, false);
        add(new BlockKey(Material.IRON_ORE, 0), "blocks/iron_ore.png", 1F, false);
        add(new BlockKey(Material.COAL_ORE, 0), "blocks/coal_ore.png", 1F, false);
        add(new BlockKey(Material.LOG, 0), "blocks/log_oak.png", 1F, false);
        add(new BlockKey(Material.LOG, 1), "blocks/log_spruce.png", 1F, false);
        add(new BlockKey(Material.LOG, 2), "blocks/log_birch.png", 1F, false);
        add(new BlockKey(Material.LOG, 3), "blocks/log_jungle.png", 1F, false);
        add(new BlockKey(Material.LOG, 4), "blocks/log_oak.png", 1F, false);
        add(new BlockKey(Material.LOG, 5), "blocks/log_spruce.png", 1F, false);
        add(new BlockKey(Material.LOG, 6), "blocks/log_birch.png", 1F, false);
        add(new BlockKey(Material.LOG, 7), "blocks/log_jungle.png", 1F, false);
        add(new BlockKey(Material.LOG, 8), "blocks/log_oak.png", 1F, false);
        add(new BlockKey(Material.LOG, 9), "blocks/log_spruce.png", 1F, false);
        add(new BlockKey(Material.LOG, 10), "blocks/log_birch.png", 1F, false);
        add(new BlockKey(Material.LOG, 11), "blocks/log_jungle.png", 1F, false);
        add(new BlockKey(Material.LOG, 12), "blocks/log_oak.png", 1F, false);
        add(new BlockKey(Material.LOG, 13), "blocks/log_spruce.png", 1F, false);
        add(new BlockKey(Material.LOG, 14), "blocks/log_birch.png", 1F, false);
        add(new BlockKey(Material.LOG, 15), "blocks/log_jungle.png", 1F, false);
        add(new BlockKey(Material.LEAVES, 0), "blocks/leaves_oak.png", 1F, false);
        add(new BlockKey(Material.LEAVES, 1), "blocks/leaves_spruce.png", 1F, false);
        add(new BlockKey(Material.LEAVES, 2), "blocks/leaves_birch.png", 1F, false);
        add(new BlockKey(Material.LEAVES, 3), "blocks/leaves_jungle.png", 1F, false);
        add(new BlockKey(Material.LEAVES, 4), "blocks/leaves_oak.png", 1F, false);
        add(new BlockKey(Material.LEAVES, 5), "blocks/leaves_spruce.png", 1F, false);
        add(new BlockKey(Material.LEAVES, 6), "blocks/leaves_birch.png", 1F, false);
        add(new BlockKey(Material.LEAVES, 7), "blocks/leaves_jungle.png", 1F, false);
        add(new BlockKey(Material.LEAVES, 8), "blocks/leaves_oak.png", 1F, false);
        add(new BlockKey(Material.LEAVES, 9), "blocks/leaves_spruce.png", 1F, false);
        add(new BlockKey(Material.LEAVES, 10), "blocks/leaves_birch.png", 1F, false);
        add(new BlockKey(Material.LEAVES, 11), "blocks/leaves_jungle.png", 1F, false);
        add(new BlockKey(Material.LEAVES, 12), "blocks/leaves_oak.png", 1F, false);
        add(new BlockKey(Material.LEAVES, 13), "blocks/leaves_spruce.png", 1F, false);
        add(new BlockKey(Material.LEAVES, 14), "blocks/leaves_birch.png", 1F, false);
        add(new BlockKey(Material.LEAVES, 15), "blocks/leaves_jungle.png", 1F, false);
        add(new BlockKey(Material.SPONGE, 0), "blocks/sponge.png", 1F, false);
        add(new BlockKey(Material.SPONGE, 1), "blocks/sponge_wet.png", 1F, false);
        //add transparency
        add(new BlockKey(Material.LAPIS_ORE, 0), "blocks/lapis_ore.png", 1F, false);
        add(new BlockKey(Material.LAPIS_BLOCK, 0), "blocks/lapis_block.png", 1F, false);
        add(new BlockKey(Material.DISPENSER, 0), "blocks/dispenser_front_horizontal.png", 1F, false);
        add(new BlockKey(Material.SANDSTONE, 0), "blocks/sandstone_normal.png", 1F, false);
        add(new BlockKey(Material.SANDSTONE, 1), "blocks/sandstone_carved.png", 1F, false);
        add(new BlockKey(Material.SANDSTONE, 2), "blocks/sandstone_smooth.png", 1F, false);
        add(new BlockKey(Material.NOTE_BLOCK, 0), "blocks/noteblock.png", 1F, false);
        //add bed details
        add(new BlockKey(Material.BED, 0), "blocks/bed_head_top.png", 1F, false);
        add(new BlockKey(Material.POWERED_RAIL, 0), "blocks/rail_detector_powered.png", 2F/16F, false);
        add(new BlockKey(Material.PISTON_STICKY_BASE, 0), "blocks/piston_side.png", 1F, false);
        add(new BlockKey(Material.WEB, 0), "blocks/web.png", 1F, false);
        add(new BlockKey(31, 0), "blocks/deadbush.png", 0.25F, false); //tall grass
        add(new BlockKey(31, 1), "blocks/tallgrass.png", 0.25F, true);
        add(new BlockKey(31, 2), "blocks/fern.png", 0.25F, true);
        add(new BlockKey(Material.DEAD_BUSH, 0), "blocks/deadbush.png", 0.25F, true);
        add(new BlockKey(Material.PISTON_BASE, 0), "blocks/piston_side.png", 1F, false);
        //piston head
        add(new BlockKey(Material.WOOL, 0), "blocks/wool_colored_white.png", 1F, false); //35:0
        add(new BlockKey(Material.WOOL, 1), "blocks/wool_colored_orange.png", 1F, false);
        add(new BlockKey(Material.WOOL, 2), "blocks/wool_colored_magenta.png", 1F, false);
        add(new BlockKey(Material.WOOL, 3), "blocks/wool_colored_light_blue.png", 1F, false);
        add(new BlockKey(Material.WOOL, 4), "blocks/wool_colored_yellow.png", 1F, false);
        add(new BlockKey(Material.WOOL, 5), "blocks/wool_colored_lime.png", 1F, false);
        add(new BlockKey(Material.WOOL, 6), "blocks/wool_colored_pink.png", 1F, false);
        add(new BlockKey(Material.WOOL, 7), "blocks/wool_colored_gray.png", 1F, false);
        add(new BlockKey(Material.WOOL, 8), "blocks/wool_colored_silver.png", 1F, false);
        add(new BlockKey(Material.WOOL, 9), "blocks/wool_colored_cyan.png", 1F, false);
        add(new BlockKey(Material.WOOL, 10), "blocks/wool_colored_purple.png", 1F, false);
        add(new BlockKey(Material.WOOL, 11), "blocks/wool_colored_blue.png", 1F, false);
        add(new BlockKey(Material.WOOL, 12), "blocks/wool_colored_brown.png", 1F, false);
        add(new BlockKey(Material.WOOL, 13), "blocks/wool_colored_green.png", 1F, false);
        add(new BlockKey(Material.WOOL, 14), "blocks/wool_colored_red.png", 1F, false);
        add(new BlockKey(Material.WOOL, 15), "blocks/wool_colored_black.png", 1F, false);
        add(new BlockKey(Material.YELLOW_FLOWER, 0), "blocks/flower_dandelion.png", 1F, false);
        add(new BlockKey(Material.RED_ROSE, 0), "blocks/flower_rose.png", 1F, false);
        add(new BlockKey(Material.RED_ROSE, 1), "blocks/flower_blue_orchid.png", 1F, false);
        add(new BlockKey(Material.RED_ROSE, 2), "blocks/flower_allium.png", 1F, false);
        add(new BlockKey(Material.RED_ROSE, 3), "blocks/flower_houstonia.png", 1F, false);
        add(new BlockKey(Material.RED_ROSE, 4), "blocks/flower_tulip_red.png", 1F, false);
        add(new BlockKey(Material.RED_ROSE, 5), "blocks/flower_tulip_orange.png", 1F, false);
        add(new BlockKey(Material.RED_ROSE, 6), "blocks/flower_tulip_white.png", 1F, false);
        add(new BlockKey(Material.RED_ROSE, 7), "blocks/flower_tulip_pink.png", 1F, false);
        add(new BlockKey(Material.RED_ROSE, 8), "blocks/flower_oxeye_daisy.png", 1F, false);
        add(new BlockKey(Material.BROWN_MUSHROOM, 0), "blocks/mushroom_brown.png", 1F, false);
        add(new BlockKey(Material.RED_MUSHROOM, 0), "blocks/mushroom_red.png", 1F, false);
        add(new BlockKey(Material.GOLD_BLOCK, 0), "blocks/gold_block.png", 1F, false);
        add(new BlockKey(Material.IRON_BLOCK, 0), "blocks/iron_block.png", 1F, false);
        //double stone slab
        add(new BlockKey(44, 0), "blocks/stone.png", 1F, false); //stone slab
        add(new BlockKey(44, 1), "blocks/sandstone_top.png", 1F, false);
        add(new BlockKey(44, 2), "blocks/planks_oak.png", 1F, false);
        add(new BlockKey(44, 3), "blocks/cobblestone.png", 1F, false);
        add(new BlockKey(44, 4), "blocks/brick.png", 1F, false);
        add(new BlockKey(44, 5), "blocks/stonebrick.png", 1F, false);
        add(new BlockKey(44, 6), "blocks/nether_brick.png", 1F, false);
        add(new BlockKey(44, 7), "blocks/quartz_block_top.png", 1F, false);
        add(new BlockKey(45, 0), "blocks/brick.png", 1F, false); //brick block
        add(new BlockKey(Material.TNT, 0), "blocks/tnt_side.png", 1F, false);
        add(new BlockKey(Material.BOOKSHELF, 0), "blocks/bookshelf.png", 1F, false);
        add(new BlockKey(Material.MOSSY_COBBLESTONE, 0), "blocks/cobblestone_mossy.png", 1F, false);
        add(new BlockKey(Material.OBSIDIAN, 0), "blocks/obsidian.png", 1F, false);
        add(new BlockKey(Material.TORCH, 0), "blocks/torch_on.png", 1F, false);
        add(new BlockKey(Material.FIRE, 0), "blocks/fire_layer_0.png", 1F, false);
        add(new BlockKey(Material.MOB_SPAWNER, 0), "blocks/mob_spawner.png", 1F, false);
        add(new BlockKey(Material.WOOD_STAIRS, 0), "blocks/planks_oak.png", 1F, false);
        //wood stairs data values?
        //chest
        //redstone wire
        add(new BlockKey(Material.DIAMOND_ORE, 0), "blocks/diamond_ore.png", 1F, false);
        add(new BlockKey(Material.DIAMOND_BLOCK, 0), "blocks/diamond_block.png", 1F, false);
        add(new BlockKey(Material.WORKBENCH, 0), "blocks/crafting_table_front.png", 1F, false);
        //wheat crops
        add(new BlockKey(Material.FURNACE, 0), "blocks/furnace_front_off.png", 1F, false);
        add(new BlockKey(Material.BURNING_FURNACE, 0), "blocks/furnace_front_on.png", 1F, false);
        add(new BlockKey(63, 0), "entity/sign.png", 1F, false); //standing sign
        //oak door
        add(new BlockKey(Material.LADDER, 0), "blocks/ladder.png", 1F, false);
        add(new BlockKey(Material.RAILS, 0), "blocks/rail_normal.png", 1F, false);
        add(new BlockKey(Material.COBBLESTONE_STAIRS, 0), "blocks/cobblestone.png", 1F, false);
        add(new BlockKey(68, 0), "entity/sign.png", 1F, false); //sign on wall
        add(new BlockKey(Material.LEVER, 0), "blocks/lever.png", 1F, false);
        add(new BlockKey(Material.STONE_PLATE, 0), "blocks/stone.png", 1F, false);
        //iron door
        add(new BlockKey(Material.WOOD_PLATE, 0), "blocks/planks_oak.png", 1F, false);
        add(new BlockKey(Material.REDSTONE_ORE, 0), "blocks/redstone_ore.png", 1F, false);
        add(new BlockKey(Material.GLOWING_REDSTONE_ORE, 0), "blocks/redstone_ore.png", 1F, false);
        add(new BlockKey(Material.REDSTONE_TORCH_OFF, 0), "blocks/redstone_torch_off.png", 1F, false);
        add(new BlockKey(Material.REDSTONE_TORCH_ON, 0), "blocks/redstone_torch_on.png", 1F, false);
        add(new BlockKey(Material.STONE_BUTTON, 0), "blocks/stone.png", 0.01171875F, false);
        add(new BlockKey(Material.SNOW, 0), "blocks/snow.png", 1F, false); //snow layer
        add(new BlockKey(Material.ICE, 0), "blocks/ice.png", 1F, false);
        add(new BlockKey(Material.SNOW_BLOCK, 0), "blocks/snow.png", 1F, false); //snow block
        add(new BlockKey(Material.CACTUS, 0), "blocks/cactus_side.png", 1F, false);
        add(new BlockKey(Material.CLAY, 0), "blocks/clay.png", 1F, false);
        add(new BlockKey(Material.SUGAR_CANE_BLOCK, 0), "blocks/reeds.png", 1F, false);
        add(new BlockKey(Material.JUKEBOX, 0), "blocks/jukebox_top.png", 1F, false);
        add(new BlockKey(Material.FENCE, 0), "blocks/planks_oak.png", 1F, false);
        add(new BlockKey(Material.PUMPKIN, 0), "blocks/pumpkin_side.png", 1F, false);
        add(new BlockKey(Material.NETHERRACK, 0), "blocks/netherrack.png", 1F, false);
        add(new BlockKey(Material.SOUL_SAND, 0), "blocks/soul_sand.png", 1F, false);
        add(new BlockKey(Material.GLOWSTONE, 0), "blocks/glowstone.png", 1F, false);
        add(new BlockKey(Material.PORTAL, 0), "blocks/portal.png", 1F, false);
        add(new BlockKey(Material.JACK_O_LANTERN, 0), "blocks/pumpkin_face_on.png", 1F, false);
        add(new BlockKey(Material.CAKE_BLOCK, 0), "blocks/cake_side.png", 1F, false);
        add(new BlockKey(93, 0), "blocks/repeater_off.png", 1/8F, false);
        add(new BlockKey(94, 0), "blocks/repeater_on.png", 1/8F, false);
        add(new BlockKey(Material.STAINED_GLASS, 0), "blocks/glass_white.png", 1F, false); //95:0
        add(new BlockKey(Material.STAINED_GLASS, 1), "blocks/glass_orange.png", 1F, false);
        add(new BlockKey(Material.STAINED_GLASS, 2), "blocks/glass_magenta.png", 1F, false);
        add(new BlockKey(Material.STAINED_GLASS, 3), "blocks/glass_light_blue.png", 1F, false);
        add(new BlockKey(Material.STAINED_GLASS, 4), "blocks/glass_yellow.png", 1F, false);
        add(new BlockKey(Material.STAINED_GLASS, 5), "blocks/glass_lime.png", 1F, false);
        add(new BlockKey(Material.STAINED_GLASS, 6), "blocks/glass_pink.png", 1F, false);
        add(new BlockKey(Material.STAINED_GLASS, 7), "blocks/glass_gray.png", 1F, false);
        add(new BlockKey(Material.STAINED_GLASS, 8), "blocks/glass_silver.png", 1F, false);
        add(new BlockKey(Material.STAINED_GLASS, 9), "blocks/glass_cyan.png", 1F, false);
        add(new BlockKey(Material.STAINED_GLASS, 10), "blocks/glass_purple.png", 1F, false);
        add(new BlockKey(Material.STAINED_GLASS, 11), "blocks/glass_blue.png", 1F, false);
        add(new BlockKey(Material.STAINED_GLASS, 12), "blocks/glass_brown.png", 1F, false);
        add(new BlockKey(Material.STAINED_GLASS, 13), "blocks/glass_green.png", 1F, false);
        add(new BlockKey(Material.STAINED_GLASS, 14), "blocks/glass_red.png", 1F, false);
        add(new BlockKey(Material.STAINED_GLASS, 15), "blocks/glass_black.png", 1F, false);
        add(new BlockKey(Material.TRAP_DOOR, 0), "blocks/trapdoor.png", 1/4F, false);
        add(new BlockKey(97, 0), "blocks/stone.png", 1F, false); //monster stone egg
        add(new BlockKey(97, 1), "blocks/cobblestone.png", 1F, false);
        add(new BlockKey(97, 2), "blocks/stonebrick.png", 1F, false);
        add(new BlockKey(97, 3), "blocks/stonebrick_mossy.png", 1F, false);
        add(new BlockKey(97, 4), "blocks/stonebrick_cracked.png", 1F, false);
        add(new BlockKey(97, 5), "blocks/stonebrick_carved.png", 1F, false);
        add(new BlockKey(98, 0), "blocks/stonebrick.png", 1F, false); //stone brick
        add(new BlockKey(98, 1), "blocks/stonebrick_mossy.png", 1F, false);
        add(new BlockKey(98, 2), "blocks/stonebrick_cracked.png", 1F, false);
        add(new BlockKey(98, 3), "blocks/stonebrick_carved.png", 1F, false);
        add(new BlockKey(Material.HUGE_MUSHROOM_1, 0), "blocks/mushroom_block_inside.png", 1F, false); //99:0
        add(new BlockKey(Material.HUGE_MUSHROOM_1, 1), "blocks/mushroom_block_skin_brown.png", 1F, false);
        add(new BlockKey(Material.HUGE_MUSHROOM_1, 2), "blocks/mushroom_block_skin_brown.png", 1F, false);
        add(new BlockKey(Material.HUGE_MUSHROOM_1, 3), "blocks/mushroom_block_skin_brown.png", 1F, false);
        add(new BlockKey(Material.HUGE_MUSHROOM_1, 4), "blocks/mushroom_block_skin_brown.png", 1F, false);
        add(new BlockKey(Material.HUGE_MUSHROOM_1, 5), "blocks/mushroom_block_skin_brown.png", 1F, false);
        add(new BlockKey(Material.HUGE_MUSHROOM_1, 6), "blocks/mushroom_block_skin_brown.png", 1F, false);
        add(new BlockKey(Material.HUGE_MUSHROOM_1, 7), "blocks/mushroom_block_skin_brown.png", 1F, false);
        add(new BlockKey(Material.HUGE_MUSHROOM_1, 8), "blocks/mushroom_block_skin_brown.png", 1F, false);
        add(new BlockKey(Material.HUGE_MUSHROOM_1, 9), "blocks/mushroom_block_skin_brown.png", 1F, false);
        add(new BlockKey(Material.HUGE_MUSHROOM_1, 10), "blocks/mushroom_block_skin_stem.png", 1F, false);
        add(new BlockKey(Material.HUGE_MUSHROOM_1, 11), "blocks/mushroom_block_inside.png", 1F, false);
        add(new BlockKey(Material.HUGE_MUSHROOM_1, 12), "blocks/mushroom_block_inside.png", 1F, false);
        add(new BlockKey(Material.HUGE_MUSHROOM_1, 13), "blocks/mushroom_block_inside.png", 1F, false);
        add(new BlockKey(Material.HUGE_MUSHROOM_1, 14), "blocks/mushroom_block_skin_brown.png", 1F, false);
        add(new BlockKey(Material.HUGE_MUSHROOM_1, 15), "blocks/mushroom_block_skin_stem.png", 1F, false);
        add(new BlockKey(Material.HUGE_MUSHROOM_2, 0), "blocks/mushroom_block_inside.png", 1F, false); //100:0
        add(new BlockKey(Material.HUGE_MUSHROOM_2, 1), "blocks/mushroom_block_skin_red.png", 1F, false);
        add(new BlockKey(Material.HUGE_MUSHROOM_2, 2), "blocks/mushroom_block_skin_red.png", 1F, false);
        add(new BlockKey(Material.HUGE_MUSHROOM_2, 3), "blocks/mushroom_block_skin_red.png", 1F, false);
        add(new BlockKey(Material.HUGE_MUSHROOM_2, 4), "blocks/mushroom_block_skin_red.png", 1F, false);
        add(new BlockKey(Material.HUGE_MUSHROOM_2, 5), "blocks/mushroom_block_skin_red.png", 1F, false);
        add(new BlockKey(Material.HUGE_MUSHROOM_2, 6), "blocks/mushroom_block_skin_red.png", 1F, false);
        add(new BlockKey(Material.HUGE_MUSHROOM_2, 7), "blocks/mushroom_block_skin_red.png", 1F, false);
        add(new BlockKey(Material.HUGE_MUSHROOM_2, 8), "blocks/mushroom_block_skin_red.png", 1F, false);
        add(new BlockKey(Material.HUGE_MUSHROOM_2, 9), "blocks/mushroom_block_skin_red.png", 1F, false);
        add(new BlockKey(Material.HUGE_MUSHROOM_2, 10), "blocks/mushroom_block_skin_stem.png", 1F, false);
        add(new BlockKey(Material.HUGE_MUSHROOM_2, 11), "blocks/mushroom_block_inside.png", 1F, false);
        add(new BlockKey(Material.HUGE_MUSHROOM_2, 12), "blocks/mushroom_block_inside.png", 1F, false);
        add(new BlockKey(Material.HUGE_MUSHROOM_2, 13), "blocks/mushroom_block_inside.png", 1F, false);
        add(new BlockKey(Material.HUGE_MUSHROOM_2, 14), "blocks/mushroom_block_skin_red.png", 1F, false);
        add(new BlockKey(Material.HUGE_MUSHROOM_2, 15), "blocks/mushroom_block_skin_stem.png", 1F, false);
        add(new BlockKey(Material.IRON_FENCE, 0), "blocks/iron_bars.png", 1F, false); //101:0
        add(new BlockKey(Material.THIN_GLASS, 0), "blocks/glass.png", 1F, false);
        add(new BlockKey(Material.MELON_BLOCK, 0), "blocks/melon_side.png", 1F, false);
        //add(new BlockKey(Material.VINE, 0), "blocks/vine.png", 1F, false);
        add(new BlockKey(Material.FENCE_GATE, 0), "blocks/planks_oak.png", 1F, false);
        add(new BlockKey(Material.BRICK_STAIRS, 0), "blocks/brick.png", 1F, false);
        add(new BlockKey(Material.SMOOTH_STAIRS, 0), "blocks/stonebrick.png", 1F, false);
        add(new BlockKey(Material.MYCEL, 0), "blocks/mycelium_top.png", 1F, false);
        add(new BlockKey(Material.NETHER_BRICK, 0), "blocks/nether_brick.png", 1F, false);
        add(new BlockKey(Material.NETHER_FENCE, 0), "blocks/nether_brick.png", 1F, false);
        add(new BlockKey(Material.NETHER_BRICK_STAIRS, 0), "blocks/nether_brick.png", 1F, false);
        add(new BlockKey(Material.NETHER_WARTS, 0), "blocks/nether_wart_stage_0.png", 1F, false);
        add(new BlockKey(Material.NETHER_WARTS, 1), "blocks/nether_wart_stage_1.png", 1F, false);
        add(new BlockKey(Material.NETHER_WARTS, 2), "blocks/nether_wart_stage_1.png", 1F, false);
        add(new BlockKey(Material.NETHER_WARTS, 3), "blocks/nether_wart_stage_2.png", 1F, false);
        add(new BlockKey(Material.ENCHANTMENT_TABLE, 0), "blocks/enchanting_table_side.png", 1F, false);
        add(new BlockKey(Material.BREWING_STAND, 0), "blocks/brewing_stand.png", 1F, false);
        add(new BlockKey(Material.CAULDRON, 0), "blocks/cauldron_side.png", 1F, false);
        add(new BlockKey(Material.ENDER_PORTAL, 0), "entity/end_portal.png", 1F, false);
        add(new BlockKey(Material.ENDER_PORTAL_FRAME, 0), "blocks/endframe_side.png", 1F, false);
        add(new BlockKey(Material.ENDER_STONE, 0), "blocks/end_stone.png", 1F, false);
        add(new BlockKey(Material.DRAGON_EGG, 0), "blocks/dragon_egg.png", 1F, false);
        add(new BlockKey(Material.REDSTONE_LAMP_OFF, 0), "blocks/redstone_lamp_off.png", 1F, false);
        add(new BlockKey(Material.REDSTONE_LAMP_ON, 0), "blocks/redstone_lamp_on.png", 1F, false); //124:0
        add(new BlockKey(Material.WOOD_DOUBLE_STEP, 0), "blocks/planks_oak.png", 1F, false);
        add(new BlockKey(Material.WOOD_DOUBLE_STEP, 1), "blocks/planks_spruce.png", 1F, false);
        add(new BlockKey(Material.WOOD_DOUBLE_STEP, 2), "blocks/planks_birch.png", 1F, false);
        add(new BlockKey(Material.WOOD_DOUBLE_STEP, 3), "blocks/planks_jungle.png", 1F, false);
        add(new BlockKey(Material.WOOD_DOUBLE_STEP, 4), "blocks/planks_acacia.png", 1F, false);
        add(new BlockKey(Material.WOOD_DOUBLE_STEP, 5), "blocks/planks_big_oak.png", 1F, false);
        add(new BlockKey(Material.WOOD_STEP, 0), "blocks/planks_oak.png", 1F, false);
        add(new BlockKey(Material.WOOD_STEP, 1), "blocks/planks_spruce.png", 1F, false);
        add(new BlockKey(Material.WOOD_STEP, 2), "blocks/planks_birch.png", 1F, false);
        add(new BlockKey(Material.WOOD_STEP, 3), "blocks/planks_jungle.png", 1F, false);
        add(new BlockKey(Material.WOOD_STEP, 4), "blocks/planks_acacia.png", 1F, false);
        add(new BlockKey(Material.WOOD_STEP, 5), "blocks/planks_big_oak.png", 1F, false);
        add(new BlockKey(Material.WOOD_STEP, 8), "blocks/planks_oak.png", 1F, false);
        add(new BlockKey(Material.WOOD_STEP, 9), "blocks/planks_spruce.png", 1F, false);
        add(new BlockKey(Material.WOOD_STEP, 10), "blocks/planks_birch.png", 1F, false);
        add(new BlockKey(Material.WOOD_STEP, 11), "blocks/planks_jungle.png", 1F, false);
        add(new BlockKey(Material.WOOD_STEP, 12), "blocks/planks_acacia.png", 1F, false);
        add(new BlockKey(Material.WOOD_STEP, 13), "blocks/planks_big_oak.png", 1F, false);
        add(new BlockKey(Material.COCOA, 0), "blocks/cocoa_stage_0.png", 0.015625F, false);
        add(new BlockKey(Material.COCOA, 1), "blocks/cocoa_stage_0.png", 0.015625F, false);
        add(new BlockKey(Material.COCOA, 2), "blocks/cocoa_stage_0.png", 0.015625F, false);
        add(new BlockKey(Material.COCOA, 3), "blocks/cocoa_stage_0.png", 0.015625F, false);
        add(new BlockKey(Material.COCOA, 4), "blocks/cocoa_stage_1.png", 0.052734375F, false);
        add(new BlockKey(Material.COCOA, 5), "blocks/cocoa_stage_1.png", 0.052734375F, false);
        add(new BlockKey(Material.COCOA, 6), "blocks/cocoa_stage_1.png", 0.052734375F, false);
        add(new BlockKey(Material.COCOA, 7), "blocks/cocoa_stage_1.png", 0.052734375F, false);
        add(new BlockKey(Material.COCOA, 8), "blocks/cocoa_stage_2.png", 1/8F, false);
        add(new BlockKey(Material.COCOA, 9), "blocks/cocoa_stage_2.png", 1/8F, false);
        add(new BlockKey(Material.COCOA, 10), "blocks/cocoa_stage_2.png", 1/8F, false);
        add(new BlockKey(Material.COCOA, 11), "blocks/cocoa_stage_2.png", 1/8F, false);
        add(new BlockKey(Material.SANDSTONE_STAIRS, 0), "blocks/sandstone_normal.png", 3/4F, false);
        add(new BlockKey(Material.EMERALD_ORE, 0), "blocks/emerald_ore.png", 1F, false);
        add(new BlockKey(Material.ENDER_CHEST, 0), "entity/ender.png", 0.669921875F, false);
        //tripwire hook
        //tripwire
        add(new BlockKey(Material.EMERALD_BLOCK, 0), "blocks/emerald_block.png", 1F, false);
        add(new BlockKey(Material.SPRUCE_WOOD_STAIRS, 0), "blocks/planks_spruce.png", 3/4F, false);
        add(new BlockKey(Material.BIRCH_WOOD_STAIRS, 0), "blocks/planks_birch.png", 3/4F, false);
        add(new BlockKey(Material.JUNGLE_WOOD_STAIRS, 0), "blocks/planks_jungle.png", 3/4F, false);
        add(new BlockKey(Material.COMMAND, 0), "blocks/command_block_front.png", 1F, false);
        add(new BlockKey(Material.BEACON, 0), "blocks/beacon.png", 1F, false);
        add(new BlockKey(Material.COBBLE_WALL, 0), "blocks/cobblestone.png", 0.5F, false);
        add(new BlockKey(Material.COBBLE_WALL, 1), "blocks/cobblestone_mossy.png", 0.5F, false);
        add(new BlockKey(Material.FLOWER_POT, 0), "blocks/flower_pot.png", 0.052734375F, false);
        //carrots
        //potatoes
        add(new BlockKey(Material.WOOD_BUTTON, 0), "blocks/planks_oak.png", 0.01171875F, false);
        //skull
        add(new BlockKey(Material.ANVIL, 0), "blocks/anvil_base.png", 0.5F, false);
        add(new BlockKey(Material.TRAPPED_CHEST, 0), "entity/chest/trapped.png", 0.669921875F, false);
        add(new BlockKey(Material.GOLD_PLATE, 0), "blocks/gold_block.png", 1/16F, false);
        add(new BlockKey(Material.IRON_PLATE, 0), "blocks/iron_block.png", 1/16F, false);
        //comparators
        add(new BlockKey(Material.DAYLIGHT_DETECTOR, 0), "blocks/daylight_detector_top.png", 6/16F, false);
        add(new BlockKey(Material.REDSTONE_BLOCK, 0), "blocks/redstone_block.png", 1F, false);
        add(new BlockKey(Material.QUARTZ_ORE, 0), "blocks/quartz_ore.png", 1F, false);
        add(new BlockKey(Material.HOPPER, 0), "blocks/hopper_outside.png", 1F, false);
        add(new BlockKey(Material.QUARTZ_BLOCK, 0), "blocks/quartz_block_side.png", 1F, false);
        add(new BlockKey(Material.QUARTZ_BLOCK, 1), "blocks/quartz_block_chiseled.png", 1F, false);
        add(new BlockKey(Material.QUARTZ_BLOCK, 2), "blocks/quartz_block_lines.png", 1F, false);
        add(new BlockKey(Material.QUARTZ_BLOCK, 3), "blocks/quartz_block_lines.png", 1F, false);
        add(new BlockKey(Material.QUARTZ_BLOCK, 4), "blocks/quartz_block_lines.png", 1F, false);
        add(new BlockKey(Material.QUARTZ_STAIRS, 0), "blocks/quartz_block_side.png", 3/4F, false);
        //activator rail
        //dropper
        add(new BlockKey(Material.STAINED_CLAY, 0), "blocks/hardened_clay_stained_white.png", 1F, false); //159:0
        add(new BlockKey(Material.STAINED_CLAY, 1), "blocks/hardened_clay_stained_orange.png", 1F, false);
        add(new BlockKey(Material.STAINED_CLAY, 2), "blocks/hardened_clay_stained_magenta.png", 1F, false);
        add(new BlockKey(Material.STAINED_CLAY, 3), "blocks/hardened_clay_stained_light_blue.png", 1F, false);
        add(new BlockKey(Material.STAINED_CLAY, 4), "blocks/hardened_clay_stained_yellow.png", 1F, false);
        add(new BlockKey(Material.STAINED_CLAY, 5), "blocks/hardened_clay_stained_lime.png", 1F, false);
        add(new BlockKey(Material.STAINED_CLAY, 6), "blocks/hardened_clay_stained_pink.png", 1F, false);
        add(new BlockKey(Material.STAINED_CLAY, 7), "blocks/hardened_clay_stained_gray.png", 1F, false);
        add(new BlockKey(Material.STAINED_CLAY, 8), "blocks/hardened_clay_stained_silver.png", 1F, false);
        add(new BlockKey(Material.STAINED_CLAY, 9), "blocks/hardened_clay_stained_cyan.png", 1F, false);
        add(new BlockKey(Material.STAINED_CLAY, 10), "blocks/hardened_clay_stained_purple.png", 1F, false);
        add(new BlockKey(Material.STAINED_CLAY, 11), "blocks/hardened_clay_stained_blue.png", 1F, false);
        add(new BlockKey(Material.STAINED_CLAY, 12), "blocks/hardened_clay_stained_brown.png", 1F, false);
        add(new BlockKey(Material.STAINED_CLAY, 13), "blocks/hardened_clay_stained_green.png", 1F, false);
        add(new BlockKey(Material.STAINED_CLAY, 14), "blocks/hardened_clay_stained_red.png", 1F, false);
        add(new BlockKey(Material.STAINED_CLAY, 15), "blocks/hardened_clay_stained_black.png", 1F, false);
        add(new BlockKey(Material.STAINED_GLASS_PANE, 0), "blocks/glass_white.png", 1/8F, false); //95:0
        add(new BlockKey(Material.STAINED_GLASS_PANE, 1), "blocks/glass_orange.png", 1/8F, false);
        add(new BlockKey(Material.STAINED_GLASS_PANE, 2), "blocks/glass_magenta.png", 1/8F, false);
        add(new BlockKey(Material.STAINED_GLASS_PANE, 3), "blocks/glass_light_blue.png", 1/8F, false);
        add(new BlockKey(Material.STAINED_GLASS_PANE, 4), "blocks/glass_yellow.png", 1/8F, false);
        add(new BlockKey(Material.STAINED_GLASS_PANE, 5), "blocks/glass_lime.png", 1/8F, false);
        add(new BlockKey(Material.STAINED_GLASS_PANE, 6), "blocks/glass_pink.png", 1/8F, false);
        add(new BlockKey(Material.STAINED_GLASS_PANE, 7), "blocks/glass_gray.png", 1/8F, false);
        add(new BlockKey(Material.STAINED_GLASS_PANE, 8), "blocks/glass_silver.png", 1/8F, false);
        add(new BlockKey(Material.STAINED_GLASS_PANE, 9), "blocks/glass_cyan.png", 1/8F, false);
        add(new BlockKey(Material.STAINED_GLASS_PANE, 10), "blocks/glass_purple.png", 1/8F, false);
        add(new BlockKey(Material.STAINED_GLASS_PANE, 11), "blocks/glass_blue.png", 1/8F, false);
        add(new BlockKey(Material.STAINED_GLASS_PANE, 12), "blocks/glass_brown.png", 1/8F, false);
        add(new BlockKey(Material.STAINED_GLASS_PANE, 13), "blocks/glass_green.png", 1/8F, false);
        add(new BlockKey(Material.STAINED_GLASS_PANE, 14), "blocks/glass_red.png", 1/8F, false);
        add(new BlockKey(Material.STAINED_GLASS_PANE, 15), "blocks/glass_black.png", 1/8F, false);
        add(new BlockKey(Material.LEAVES_2, 0), "blocks/leaves_acacia.png", 1F, true);
        add(new BlockKey(Material.LEAVES_2, 1), "blocks/leaves_big_oak.png", 1F, true);
        add(new BlockKey(Material.LEAVES_2, 4), "blocks/leaves_acacia.png", 1F, true);
        add(new BlockKey(Material.LEAVES_2, 5), "blocks/leaves_big_oak.png", 1F, true);
        add(new BlockKey(Material.LEAVES_2, 8), "blocks/leaves_acacia.png", 1F, true);
        add(new BlockKey(Material.LEAVES_2, 9), "blocks/leaves_big_oak.png", 1F, true);
        add(new BlockKey(Material.LEAVES_2, 12), "blocks/leaves_acacia.png", 1F, true);
        add(new BlockKey(Material.LEAVES_2, 13), "blocks/leaves_big_oak.png", 1F, true);
        //fix logs
        add(new BlockKey(Material.LOG_2, 0), "blocks/log_acacia.png", 1F, false);
        add(new BlockKey(Material.LOG_2, 1), "blocks/log_big_oak.png", 1F, false);
        add(new BlockKey(Material.LOG_2, 2), "blocks/log_acacia.png", 1F, false);
        add(new BlockKey(Material.LOG_2, 3), "blocks/log_big_oak.png", 1F, false);
        add(new BlockKey(Material.LOG_2, 4), "blocks/log_acacia.png", 1F, false);
        add(new BlockKey(Material.LOG_2, 5), "blocks/log_big_oak.png", 1F, false);
        add(new BlockKey(Material.LOG_2, 6), "blocks/log_acacia.png", 1F, false);
        add(new BlockKey(Material.LOG_2, 7), "blocks/log_big_oak.png", 1F, false);
        add(new BlockKey(Material.LOG_2, 8), "blocks/log_acacia.png", 1F, false);
        add(new BlockKey(Material.LOG_2, 9), "blocks/log_big_oak.png", 1F, false);
        add(new BlockKey(Material.LOG_2, 10), "blocks/log_acacia.png", 1F, false);
        add(new BlockKey(Material.LOG_2, 11), "blocks/log_big_oak.png", 1F, false);
        add(new BlockKey(Material.LOG_2, 12), "blocks/log_acacia.png", 1F, false);
        add(new BlockKey(Material.LOG_2, 13), "blocks/log_big_oak.png", 1F, false);
        add(new BlockKey(Material.ACACIA_STAIRS, 0), "blocks/planks_acacia.png", 3/4F, false);
        add(new BlockKey(Material.DARK_OAK_STAIRS, 0), "blocks/planks_big_oak.png", 3/4F, false);
        add(new BlockKey(Material.SLIME_BLOCK, 0), "blocks/slime.png", 1F, false);
        add(new BlockKey(Material.BARRIER, 0), Colors.INVISIBLE_WHITE, 1F, false);
        add(new BlockKey(Material.IRON_TRAPDOOR, 0), "blocks/iron_block.png", 1/4F, false);
        add(new BlockKey(Material.PRISMARINE, 0), "blocks/prismarine_rough.png", 1F, false);
        add(new BlockKey(Material.PRISMARINE, 1), "blocks/prismarine_bricks.png", 1F, false);
        add(new BlockKey(Material.PRISMARINE, 2), "blocks/prismarine_dark.png", 1F, false);
        add(new BlockKey(Material.SEA_LANTERN, 0), "blocks/sea_lantern.png", 1F, false);
        add(new BlockKey(Material.HAY_BLOCK, 0), "blocks/hay_block_side.png", 1F, false);
        add(new BlockKey(Material.CARPET, 0), "blocks/wool_colored_white.png", 1/16F, false); //171:0
        add(new BlockKey(Material.CARPET, 1), "blocks/wool_colored_orange.png", 1/16F, false);
        add(new BlockKey(Material.CARPET, 2), "blocks/wool_colored_magenta.png", 1/16F, false);
        add(new BlockKey(Material.CARPET, 3), "blocks/wool_colored_light_blue.png", 1/16F, false);
        add(new BlockKey(Material.CARPET, 4), "blocks/wool_colored_yellow.png", 1/16F, false);
        add(new BlockKey(Material.CARPET, 5), "blocks/wool_colored_lime.png", 1/16F, false);
        add(new BlockKey(Material.CARPET, 6), "blocks/wool_colored_pink.png", 1/16F, false);
        add(new BlockKey(Material.CARPET, 7), "blocks/wool_colored_gray.png", 1/16F, false);
        add(new BlockKey(Material.CARPET, 8), "blocks/wool_colored_silver.png", 1/16F, false);
        add(new BlockKey(Material.CARPET, 9), "blocks/wool_colored_cyan.png", 1/16F, false);
        add(new BlockKey(Material.CARPET, 10), "blocks/wool_colored_purple.png", 1/16F, false);
        add(new BlockKey(Material.CARPET, 11), "blocks/wool_colored_blue.png", 1/16F, false);
        add(new BlockKey(Material.CARPET, 12), "blocks/wool_colored_brown.png", 1/16F, false);
        add(new BlockKey(Material.CARPET, 13), "blocks/wool_colored_green.png", 1/16F, false);
        add(new BlockKey(Material.CARPET, 14), "blocks/wool_colored_red.png", 1/16F, false);
        add(new BlockKey(Material.CARPET, 15), "blocks/wool_colored_black.png", 1/16F, false);
        add(new BlockKey(Material.HARD_CLAY, 15), "blocks/hardened_clay.png", 1F, false);
        add(new BlockKey(Material.COAL_BLOCK, 0), "blocks/coal_block.png", 1F, false);
        add(new BlockKey(Material.PACKED_ICE, 0), "blocks/ice_packed.png", 1F, false);
        //add double plants
        add(new BlockKey(Material.DOUBLE_PLANT, 0), "blocks/double_plant_sunflower_front.png", 1F, false);
        //free standing banner
        //wall mounted banner
        add(new BlockKey(Material.DAYLIGHT_DETECTOR_INVERTED, 0), "blocks/daylight_detector_inverted_top.png", 6/16F, false);
        add(new BlockKey(Material.RED_SANDSTONE, 0), "blocks/red_sandstone_normal.png", 1F, false);
        add(new BlockKey(Material.RED_SANDSTONE, 1), "blocks/red_sandstone_carved.png", 1F, false);
        add(new BlockKey(Material.RED_SANDSTONE, 2), "blocks/red_sandstone_smooth.png", 1F, false);
        add(new BlockKey(Material.RED_SANDSTONE_STAIRS, 0), "blocks/red_sandstone_smooth.png", 3/4F, false);
        add(new BlockKey(182, 0), "blocks/red_sandstone_top.png", 1F, false); //red sandstone slab
        add(new BlockKey(Material.SPRUCE_FENCE_GATE, 0), "blocks/planks_oak.png", 1/4F, false);
        add(new BlockKey(Material.BIRCH_FENCE_GATE, 0), "blocks/planks_birch.png", 1/4F, false);
        add(new BlockKey(Material.JUNGLE_FENCE_GATE, 0), "blocks/planks_jungle.png", 1/4F, false);
        add(new BlockKey(Material.DARK_OAK_FENCE_GATE, 0), "blocks/planks_big_oak.png", 1/4F, false);
        add(new BlockKey(Material.ACACIA_FENCE, 0), "blocks/planks_acacia.png", 1/4F, false);
        //wooden doors
        add(new BlockKey(Material.END_ROD, 0), "blocks/end_rod.png", 1/16F, false);
        add(new BlockKey(Material.CHORUS_PLANT, 0), "blocks/chorus_plant.png", 100/256F, false);
        add(new BlockKey(Material.CHORUS_FLOWER, 0), "blocks/chorus_flower.png", 1F, false);
        add(new BlockKey(Material.PURPUR_BLOCK, 0), "blocks/purpur_block.png", 1F, false);
        add(new BlockKey(Material.PURPUR_PILLAR, 0), "blocks/purpur_pillar.png", 1F, false);
        add(new BlockKey(Material.PURPUR_STAIRS, 0), "blocks/purpur_block.png", 3/4F, false);
        add(new BlockKey(Material.PURPUR_SLAB, 0), "blocks/purpur_block.png", 0.5F, false);
        add(new BlockKey(Material.END_BRICKS, 0), "blocks/end_bricks.png", 0.5F, false);
        //beetrot block
        add(new BlockKey(Material.GRASS_PATH, 0), "blocks/grass_path_top.png", 15/16F, false);
        add(new BlockKey(Material.END_GATEWAY, 0), Colors.SOLID_BLACK, 1F, false);
        add(new BlockKey(Material.COMMAND_REPEATING, 0), "blocks/repeating_command_block_front.png", 1F, false);
        add(new BlockKey(Material.COMMAND_CHAIN, 0), "blocks/chain_command_block_front.png", 1F, false);
        add(new BlockKey(Material.FROSTED_ICE, 0), "blocks/frosted_ice_0.png", 1F, false);
        add(new BlockKey(Material.FROSTED_ICE, 1), "blocks/frosted_ice_1.png", 1F, false);
        add(new BlockKey(Material.FROSTED_ICE, 2), "blocks/frosted_ice_2.png", 1F, false);
        add(new BlockKey(Material.FROSTED_ICE, 3), "blocks/frosted_ice_3.png", 1F, false);
        add(new BlockKey(Material.MAGMA, 0), "blocks/magma", 1F, false);
        add(new BlockKey(Material.NETHER_WART_BLOCK, 0), "blocks/nether_wart_block.png", 1F, false);
        add(new BlockKey(Material.RED_NETHER_BRICK, 0), "blocks/red_nether_brick.png", 1F, false);
        add(new BlockKey(Material.BONE_BLOCK, 0), "blocks/bone_block_side.png", 1F, false);
        add(new BlockKey(Material.STRUCTURE_VOID, 0), Colors.INVISIBLE_WHITE, 0F, false);
        add(new BlockKey(Material.STRUCTURE_BLOCK, 0), "blocks/structure_block", 0F, false);
    }

    @Override
    public Class<ZipFile> getFrom() {
        return ZipFile.class;
    }

    @Override
    public Class<ColorMap> getTo() {
        return ColorMap.class;
    }

    private final Logger logger = Logger.getGlobal();

    @Override
    public ColorMap invoke(ZipFile zip, Object... args) {
        return invoke(zip);
    }

    public ColorMap invoke(ZipFile zip) {
        String name = zip.getName();
        name = name.substring(0, name.lastIndexOf('.'));
        ColorMap result = new ColorMap(name);

        for (ExtractableColor color : colorList) {
            int rgb;
            try {
                rgb = color.extractor.extract(zip);
            } catch (IOException ex) {
                continue;
            }

            result.put(color.block, new BlockColor(rgb, color.occupation, color.tint));
        }

        return result;
    }

    private static BufferedImage readImage(ZipFile zip, ZipEntry entry) throws IOException {
        InputStream stream = zip.getInputStream(entry);
        BufferedImage image = ImageIO.read(stream);
        stream.close();
        return image;
    }

    public final static class Extractors {

        private Extractors() {}

        public static Extractor constantColor(int rgb) {
            return new ConstantExtractor(rgb);
        }

        public static Extractor textureColor(String name) {
            return new TextureColorExtractor(name);
        }

    }

    @FunctionalInterface
    public static interface Extractor {

        public int extract(ZipFile zip) throws IOException;

    }

    /**
     * An extractor which returns the same rgb value every time.
     */
    private static class ConstantExtractor implements Extractor {

        private final int rgb;

        private ConstantExtractor(int rgb) {
            this.rgb = rgb;
        }

        @Override
        public int extract(ZipFile zip) throws IOException {
            return rgb;
        }
    }

    /**
     * A color extractor which measures the average color of a texture of choice inside the resource pack.
     */
    private static class TextureColorExtractor implements Extractor {

        private final String name;

        private TextureColorExtractor(String name) {
            this.name = name;
        }

        @Override
        public int extract(ZipFile zip) throws IOException {
            ZipEntry entry = zip.getEntry("assets/minecraft/textures/"+name);
            if (entry == null) throw new IOException("entry not found: "+name);

            BufferedImage image = readImage(zip, entry);
            int[] sum = new int[4];
            int count = 0;

            final int width = image.getWidth(), height = image.getHeight();
            for (int u = 0; u<width; u++) for (int v = 0; v<height; v++) {
                int rgb = image.getRGB(u, v);
                if (Colors.isInvisible(rgb)) continue;
                int[] color = Colors.split(rgb);

                for (int i = 0; i<color.length; i++)
                    sum[i] += color[i];

                count++;
            }

            return count==0?
                    Colors.INVISIBLE_WHITE :
                    Colors.fromRGB(sum[1]/count, sum[2]/count, sum[3]/count, sum[0]/count);
        }

    }

}
