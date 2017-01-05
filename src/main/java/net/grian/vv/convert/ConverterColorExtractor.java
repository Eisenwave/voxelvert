package net.grian.vv.convert;

import net.grian.vv.cache.ColorMap;
import net.grian.vv.core.BlockKey;
import net.grian.vv.util.Colors;
import org.bukkit.Material;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ConverterColorExtractor implements Converter<ZipFile, ColorMap> {

    public final static Map<BlockKey, Extractor> map = new HashMap<>();

    static {
        map.put(BlockKey.AIR, Extractors.constantColor(Colors.INVISIBLE_WHITE));
        map.put(BlockKey.STONE, Extractors.textureColor("blocks/stone.png"));
        map.put(new BlockKey(Material.STONE, 1), Extractors.textureColor("blocks/stone_granite.png"));
        map.put(new BlockKey(Material.STONE, 2), Extractors.textureColor("blocks/stone_granite_polished.png"));
        map.put(new BlockKey(Material.STONE, 3), Extractors.textureColor("blocks/stone_diorite.png"));
        map.put(new BlockKey(Material.STONE, 4), Extractors.textureColor("blocks/stone_diorite_polished.png"));
        map.put(new BlockKey(Material.STONE, 5), Extractors.textureColor("blocks/stone_andesite.png"));
        map.put(new BlockKey(Material.STONE, 6), Extractors.textureColor("blocks/stone_andesite_polished.png"));
        //grass
        map.put(new BlockKey(Material.DIRT, 0), Extractors.textureColor("blocks/dirt.png"));
        map.put(new BlockKey(Material.DIRT, 1), Extractors.textureColor("blocks/coarse_dirt.png"));
        map.put(new BlockKey(Material.DIRT, 2), Extractors.textureColor("blocks/dirt_podzol_top.png"));
        map.put(new BlockKey(Material.COBBLESTONE, 0), Extractors.textureColor("blocks/cobblestone.png"));
        map.put(new BlockKey(5, 0), Extractors.textureColor("blocks/planks_oak.png"));
        map.put(new BlockKey(5, 1), Extractors.textureColor("blocks/planks_spruce.png"));
        map.put(new BlockKey(5, 2), Extractors.textureColor("blocks/planks_birch.png"));
        map.put(new BlockKey(5, 3), Extractors.textureColor("blocks/planks_jungle.png"));
        map.put(new BlockKey(5, 4), Extractors.textureColor("blocks/planks_acacia.png"));
        map.put(new BlockKey(5, 5), Extractors.textureColor("blocks/planks_big_oak.png"));
        map.put(new BlockKey(Material.SAPLING, 0), Extractors.textureColor("blocks/sapling_oak.png"));
        map.put(new BlockKey(Material.SAPLING, 1), Extractors.textureColor("blocks/sapling_spruce.png"));
        map.put(new BlockKey(Material.SAPLING, 2), Extractors.textureColor("blocks/sapling_birch.png"));
        map.put(new BlockKey(Material.SAPLING, 3), Extractors.textureColor("blocks/sapling_jungle.png"));
        map.put(new BlockKey(Material.SAPLING, 4), Extractors.textureColor("blocks/sapling_acacia.png"));
        map.put(new BlockKey(Material.SAPLING, 5), Extractors.textureColor("blocks/roofed_oak.png"));
        map.put(new BlockKey(Material.BEDROCK, 0), Extractors.textureColor("blocks/bedrock.png"));
        map.put(new BlockKey(Material.STATIONARY_WATER, 0), Extractors.textureColor("blocks/water_still.png"));
        map.put(new BlockKey(Material.WATER, 0), Extractors.textureColor("blocks/water_flow.png"));
        map.put(new BlockKey(Material.STATIONARY_LAVA, 0), Extractors.textureColor("blocks/lava_still.png"));
        map.put(new BlockKey(Material.LAVA, 0), Extractors.textureColor("blocks/lava_flow.png"));
        map.put(new BlockKey(Material.SAND, 0), Extractors.textureColor("blocks/sand.png"));
        map.put(new BlockKey(Material.SAND, 1), Extractors.textureColor("blocks/red_sand.png"));
        map.put(new BlockKey(Material.GRAVEL, 0), Extractors.textureColor("blocks/gravel.png")); //13:0
        map.put(new BlockKey(Material.GOLD_ORE, 0), Extractors.textureColor("blocks/gold_ore.png"));
        map.put(new BlockKey(Material.IRON_ORE, 0), Extractors.textureColor("blocks/iron_ore.png"));
        map.put(new BlockKey(Material.COAL_ORE, 0), Extractors.textureColor("blocks/coal_ore.png"));
        map.put(new BlockKey(Material.LOG, 0), Extractors.textureColor("blocks/log_oak.png"));
        map.put(new BlockKey(Material.LOG, 1), Extractors.textureColor("blocks/log_spruce.png"));
        map.put(new BlockKey(Material.LOG, 2), Extractors.textureColor("blocks/log_birch.png"));
        map.put(new BlockKey(Material.LOG, 3), Extractors.textureColor("blocks/log_jungle.png"));
        map.put(new BlockKey(Material.LOG, 4), Extractors.textureColor("blocks/log_oak.png"));
        map.put(new BlockKey(Material.LOG, 5), Extractors.textureColor("blocks/log_spruce.png"));
        map.put(new BlockKey(Material.LOG, 6), Extractors.textureColor("blocks/log_birch.png"));
        map.put(new BlockKey(Material.LOG, 7), Extractors.textureColor("blocks/log_jungle.png"));
        map.put(new BlockKey(Material.LOG, 8), Extractors.textureColor("blocks/log_oak.png"));
        map.put(new BlockKey(Material.LOG, 9), Extractors.textureColor("blocks/log_spruce.png"));
        map.put(new BlockKey(Material.LOG, 10), Extractors.textureColor("blocks/log_birch.png"));
        map.put(new BlockKey(Material.LOG, 11), Extractors.textureColor("blocks/log_jungle.png"));
        map.put(new BlockKey(Material.LOG, 12), Extractors.textureColor("blocks/log_oak.png"));
        map.put(new BlockKey(Material.LOG, 13), Extractors.textureColor("blocks/log_spruce.png"));
        map.put(new BlockKey(Material.LOG, 14), Extractors.textureColor("blocks/log_birch.png"));
        map.put(new BlockKey(Material.LOG, 15), Extractors.textureColor("blocks/log_jungle.png"));
        //leaves
        map.put(new BlockKey(Material.SPONGE, 0), Extractors.textureColor("blocks/sponge.png"));
        map.put(new BlockKey(Material.SPONGE, 1), Extractors.textureColor("blocks/sponge_wet.png"));
        //add transparency
        map.put(new BlockKey(Material.LAPIS_ORE, 0), Extractors.textureColor("blocks/lapis_ore.png"));
        map.put(new BlockKey(Material.LAPIS_BLOCK, 0), Extractors.textureColor("blocks/lapis_block.png"));
        map.put(new BlockKey(Material.DISPENSER, 0), Extractors.textureColor("blocks/dispenser_front_horizontal.png"));
        map.put(new BlockKey(Material.SANDSTONE, 0), Extractors.textureColor("blocks/sandstone_normal.png"));
        map.put(new BlockKey(Material.SANDSTONE, 1), Extractors.textureColor("blocks/sandstone_carved.png"));
        map.put(new BlockKey(Material.SANDSTONE, 2), Extractors.textureColor("blocks/sandstone_smooth.png"));
        map.put(new BlockKey(Material.NOTE_BLOCK, 0), Extractors.textureColor("blocks/noteblock.png"));
        map.put(new BlockKey(Material.BED, 0), Extractors.textureColor("blocks/bed_head_top.png"));
        map.put(new BlockKey(Material.POWERED_RAIL, 0), Extractors.textureColor("blocks/rail_detector_powered.png"));
        map.put(new BlockKey(Material.PISTON_STICKY_BASE, 0), Extractors.textureColor("blocks/piston_side.png"));
        map.put(new BlockKey(Material.WEB, 0), Extractors.textureColor("blocks/web.png"));
        //dead shrub
        //grass
        //fern
        //dead bush
        map.put(new BlockKey(Material.PISTON_BASE, 0), Extractors.textureColor("blocks/piston_side.png"));
        //piston head
        map.put(new BlockKey(Material.WOOL, 0), Extractors.textureColor("blocks/wool_colored_white.png")); //35:0
        map.put(new BlockKey(Material.WOOL, 1), Extractors.textureColor("blocks/wool_colored_orange.png"));
        map.put(new BlockKey(Material.WOOL, 2), Extractors.textureColor("blocks/wool_colored_magenta.png"));
        map.put(new BlockKey(Material.WOOL, 3), Extractors.textureColor("blocks/wool_colored_light_blue.png"));
        map.put(new BlockKey(Material.WOOL, 4), Extractors.textureColor("blocks/wool_colored_yellow.png"));
        map.put(new BlockKey(Material.WOOL, 5), Extractors.textureColor("blocks/wool_colored_lime.png"));
        map.put(new BlockKey(Material.WOOL, 6), Extractors.textureColor("blocks/wool_colored_pink.png"));
        map.put(new BlockKey(Material.WOOL, 7), Extractors.textureColor("blocks/wool_colored_gray.png"));
        map.put(new BlockKey(Material.WOOL, 8), Extractors.textureColor("blocks/wool_colored_silver.png"));
        map.put(new BlockKey(Material.WOOL, 9), Extractors.textureColor("blocks/wool_colored_cyan.png"));
        map.put(new BlockKey(Material.WOOL, 10), Extractors.textureColor("blocks/wool_colored_purple.png"));
        map.put(new BlockKey(Material.WOOL, 11), Extractors.textureColor("blocks/wool_colored_blue.png"));
        map.put(new BlockKey(Material.WOOL, 12), Extractors.textureColor("blocks/wool_colored_brown.png"));
        map.put(new BlockKey(Material.WOOL, 13), Extractors.textureColor("blocks/wool_colored_green.png"));
        map.put(new BlockKey(Material.WOOL, 14), Extractors.textureColor("blocks/wool_colored_red.png"));
        map.put(new BlockKey(Material.WOOL, 15), Extractors.textureColor("blocks/wool_colored_black.png"));
        map.put(new BlockKey(Material.YELLOW_FLOWER, 0), Extractors.textureColor("blocks/flower_dandelion.png"));
        map.put(new BlockKey(Material.RED_ROSE, 0), Extractors.textureColor("blocks/flower_rose.png"));
        map.put(new BlockKey(Material.RED_ROSE, 1), Extractors.textureColor("blocks/flower_blue_orchid.png"));
        map.put(new BlockKey(Material.RED_ROSE, 2), Extractors.textureColor("blocks/flower_allium.png"));
        map.put(new BlockKey(Material.RED_ROSE, 3), Extractors.textureColor("blocks/flower_houstonia.png"));
        map.put(new BlockKey(Material.RED_ROSE, 4), Extractors.textureColor("blocks/flower_tulip_red.png"));
        map.put(new BlockKey(Material.RED_ROSE, 5), Extractors.textureColor("blocks/flower_tulip_orange.png"));
        map.put(new BlockKey(Material.RED_ROSE, 6), Extractors.textureColor("blocks/flower_tulip_white.png"));
        map.put(new BlockKey(Material.RED_ROSE, 7), Extractors.textureColor("blocks/flower_tulip_pink.png"));
        map.put(new BlockKey(Material.RED_ROSE, 8), Extractors.textureColor("blocks/flower_oxeye_daisy.png"));
        map.put(new BlockKey(Material.BROWN_MUSHROOM, 0), Extractors.textureColor("blocks/mushroom_brown.png"));
        map.put(new BlockKey(Material.RED_MUSHROOM, 0), Extractors.textureColor("blocks/mushroom_red.png"));
        map.put(new BlockKey(Material.GOLD_BLOCK, 0), Extractors.textureColor("blocks/gold_block.png"));
        map.put(new BlockKey(Material.IRON_BLOCK, 0), Extractors.textureColor("blocks/iron_block.png"));
        //double stone slab
        map.put(new BlockKey(44, 0), Extractors.textureColor("blocks/stone.png")); //stone slab
        map.put(new BlockKey(44, 1), Extractors.textureColor("blocks/sandstone_top.png"));
        map.put(new BlockKey(44, 2), Extractors.textureColor("blocks/planks_oak.png"));
        map.put(new BlockKey(44, 3), Extractors.textureColor("blocks/cobblestone.png"));
        map.put(new BlockKey(44, 4), Extractors.textureColor("blocks/brick.png"));
        map.put(new BlockKey(44, 5), Extractors.textureColor("blocks/stonebrick.png"));
        map.put(new BlockKey(44, 6), Extractors.textureColor("blocks/nether_brick.png"));
        map.put(new BlockKey(44, 7), Extractors.textureColor("blocks/quartz_block_top.png"));
        map.put(new BlockKey(45, 0), Extractors.textureColor("blocks/brick.png")); //brick block
        map.put(new BlockKey(Material.TNT, 0), Extractors.textureColor("blocks/tnt_side.png"));
        map.put(new BlockKey(Material.BOOKSHELF, 0), Extractors.textureColor("blocks/bookshelf.png"));
        map.put(new BlockKey(Material.MOSSY_COBBLESTONE, 0), Extractors.textureColor("blocks/cobblestone_mossy.png"));
        map.put(new BlockKey(Material.OBSIDIAN, 0), Extractors.textureColor("blocks/obsidian.png"));
        map.put(new BlockKey(Material.TORCH, 0), Extractors.textureColor("blocks/torch_on.png"));
        map.put(new BlockKey(Material.FIRE, 0), Extractors.textureColor("blocks/fire_layer_0.png"));
        map.put(new BlockKey(Material.MOB_SPAWNER, 0), Extractors.textureColor("blocks/mob_spawner.png"));
        map.put(new BlockKey(Material.WOOD_STAIRS, 0), Extractors.textureColor("blocks/planks_oak.png"));
        //wood stairs data values?
        //chest
        //redstone wire
        map.put(new BlockKey(Material.DIAMOND_ORE, 0), Extractors.textureColor("blocks/diamond_ore.png"));
        map.put(new BlockKey(Material.DIAMOND_BLOCK, 0), Extractors.textureColor("blocks/diamond_block.png"));
        map.put(new BlockKey(Material.WORKBENCH, 0), Extractors.textureColor("blocks/crafting_table_front.png"));
        //wheat crops
        map.put(new BlockKey(Material.FURNACE, 0), Extractors.textureColor("blocks/furnace_front_off.png"));
        map.put(new BlockKey(Material.BURNING_FURNACE, 0), Extractors.textureColor("blocks/furnace_front_on.png"));
        map.put(new BlockKey(63, 0), Extractors.textureColor("entity/sign.png")); //standing sign
        //oak door
        map.put(new BlockKey(Material.LADDER, 0), Extractors.textureColor("blocks/ladder.png"));
        map.put(new BlockKey(Material.RAILS, 0), Extractors.textureColor("blocks/rail_normal.png"));
        map.put(new BlockKey(Material.COBBLESTONE_STAIRS, 0), Extractors.textureColor("blocks/cobblestone.png"));
        map.put(new BlockKey(68, 0), Extractors.textureColor("entity/sign.png")); //sign on wall
        map.put(new BlockKey(Material.LEVER, 0), Extractors.textureColor("blocks/lever.png"));
        map.put(new BlockKey(Material.STONE_PLATE, 0), Extractors.textureColor("blocks/stone.png"));
        //iron door
        map.put(new BlockKey(Material.WOOD_PLATE, 0), Extractors.textureColor("blocks/planks_oak.png"));
        map.put(new BlockKey(Material.REDSTONE_ORE, 0), Extractors.textureColor("blocks/redstone_ore.png"));
        map.put(new BlockKey(Material.GLOWING_REDSTONE_ORE, 0), Extractors.textureColor("blocks/redstone_ore.png"));
        map.put(new BlockKey(Material.REDSTONE_TORCH_OFF, 0), Extractors.textureColor("blocks/redstone_torch_off.png"));
        map.put(new BlockKey(Material.REDSTONE_TORCH_ON, 0), Extractors.textureColor("blocks/redstone_torch_on.png"));
        //button
        map.put(new BlockKey(Material.SNOW, 0), Extractors.textureColor("blocks/snow.png")); //snow layer
        map.put(new BlockKey(Material.ICE, 0), Extractors.textureColor("blocks/ice.png"));
        map.put(new BlockKey(Material.SNOW_BLOCK, 0), Extractors.textureColor("blocks/snow.png")); //snow block
        map.put(new BlockKey(Material.CACTUS, 0), Extractors.textureColor("blocks/cactus_side.png"));
        map.put(new BlockKey(Material.CLAY, 0), Extractors.textureColor("blocks/clay.png"));
        map.put(new BlockKey(Material.SUGAR_CANE_BLOCK, 0), Extractors.textureColor("blocks/reeds.png"));
        map.put(new BlockKey(Material.JUKEBOX, 0), Extractors.textureColor("blocks/jukebox_top.png"));
        map.put(new BlockKey(Material.FENCE, 0), Extractors.textureColor("blocks/planks_oak.png"));
        //other fences?
        map.put(new BlockKey(Material.PUMPKIN, 0), Extractors.textureColor("blocks/pumpkin_side.png"));
        map.put(new BlockKey(Material.NETHERRACK, 0), Extractors.textureColor("blocks/netherrack.png"));
        map.put(new BlockKey(Material.SOUL_SAND, 0), Extractors.textureColor("blocks/soul_sand.png"));
        map.put(new BlockKey(Material.GLOWSTONE, 0), Extractors.textureColor("blocks/glowstone.png"));
        map.put(new BlockKey(Material.PORTAL, 0), Extractors.textureColor("blocks/portal.png"));
        map.put(new BlockKey(Material.JACK_O_LANTERN, 0), Extractors.textureColor("blocks/pumpkin_face_on.png"));
        map.put(new BlockKey(Material.CAKE_BLOCK, 0), Extractors.textureColor("blocks/cake_side.png"));
        map.put(new BlockKey(Material.CAKE_BLOCK, 0), Extractors.textureColor("blocks/cake_side.png"));

        map.put(new BlockKey(Material.STAINED_GLASS, 0), Extractors.textureColor("blocks/glass_white.png")); //95:0
        map.put(new BlockKey(Material.STAINED_GLASS, 1), Extractors.textureColor("blocks/glass_orange.png"));
        map.put(new BlockKey(Material.STAINED_GLASS, 2), Extractors.textureColor("blocks/glass_magenta.png"));
        map.put(new BlockKey(Material.STAINED_GLASS, 3), Extractors.textureColor("blocks/glass_light_blue.png"));
        map.put(new BlockKey(Material.STAINED_GLASS, 4), Extractors.textureColor("blocks/glass_yellow.png"));
        map.put(new BlockKey(Material.STAINED_GLASS, 5), Extractors.textureColor("blocks/glass_lime.png"));
        map.put(new BlockKey(Material.STAINED_GLASS, 6), Extractors.textureColor("blocks/glass_pink.png"));
        map.put(new BlockKey(Material.STAINED_GLASS, 7), Extractors.textureColor("blocks/glass_gray.png"));
        map.put(new BlockKey(Material.STAINED_GLASS, 8), Extractors.textureColor("blocks/glass_silver.png"));
        map.put(new BlockKey(Material.STAINED_GLASS, 9), Extractors.textureColor("blocks/glass_cyan.png"));
        map.put(new BlockKey(Material.STAINED_GLASS, 10), Extractors.textureColor("blocks/glass_purple.png"));
        map.put(new BlockKey(Material.STAINED_GLASS, 11), Extractors.textureColor("blocks/glass_blue.png"));
        map.put(new BlockKey(Material.STAINED_GLASS, 12), Extractors.textureColor("blocks/glass_brown.png"));
        map.put(new BlockKey(Material.STAINED_GLASS, 13), Extractors.textureColor("blocks/glass_green.png"));
        map.put(new BlockKey(Material.STAINED_GLASS, 14), Extractors.textureColor("blocks/glass_red.png"));
        map.put(new BlockKey(Material.STAINED_GLASS, 15), Extractors.textureColor("blocks/glass_black.png"));

        map.put(new BlockKey(Material.HUGE_MUSHROOM_1, 0), Extractors.textureColor("blocks/mushroom_block_inside.png")); //99:0
        map.put(new BlockKey(Material.HUGE_MUSHROOM_1, 1), Extractors.textureColor("blocks/mushroom_block_skin_brown.png"));
        map.put(new BlockKey(Material.HUGE_MUSHROOM_1, 2), Extractors.textureColor("blocks/mushroom_block_skin_brown.png"));
        map.put(new BlockKey(Material.HUGE_MUSHROOM_1, 3), Extractors.textureColor("blocks/mushroom_block_skin_brown.png"));
        map.put(new BlockKey(Material.HUGE_MUSHROOM_1, 4), Extractors.textureColor("blocks/mushroom_block_skin_brown.png"));
        map.put(new BlockKey(Material.HUGE_MUSHROOM_1, 5), Extractors.textureColor("blocks/mushroom_block_skin_brown.png"));
        map.put(new BlockKey(Material.HUGE_MUSHROOM_1, 6), Extractors.textureColor("blocks/mushroom_block_skin_brown.png"));
        map.put(new BlockKey(Material.HUGE_MUSHROOM_1, 7), Extractors.textureColor("blocks/mushroom_block_skin_brown.png"));
        map.put(new BlockKey(Material.HUGE_MUSHROOM_1, 8), Extractors.textureColor("blocks/mushroom_block_skin_brown.png"));
        map.put(new BlockKey(Material.HUGE_MUSHROOM_1, 9), Extractors.textureColor("blocks/mushroom_block_skin_brown.png"));
        map.put(new BlockKey(Material.HUGE_MUSHROOM_1, 10), Extractors.textureColor("blocks/mushroom_block_skin_stem.png"));
        map.put(new BlockKey(Material.HUGE_MUSHROOM_1, 11), Extractors.textureColor("blocks/mushroom_block_inside.png"));
        map.put(new BlockKey(Material.HUGE_MUSHROOM_1, 12), Extractors.textureColor("blocks/mushroom_block_inside.png"));
        map.put(new BlockKey(Material.HUGE_MUSHROOM_1, 13), Extractors.textureColor("blocks/mushroom_block_inside.png"));
        map.put(new BlockKey(Material.HUGE_MUSHROOM_1, 14), Extractors.textureColor("blocks/mushroom_block_skin_brown.png"));
        map.put(new BlockKey(Material.HUGE_MUSHROOM_1, 15), Extractors.textureColor("blocks/mushroom_block_skin_stem.png"));

        map.put(new BlockKey(Material.HUGE_MUSHROOM_2, 0), Extractors.textureColor("blocks/mushroom_block_inside.png")); //100:0
        map.put(new BlockKey(Material.HUGE_MUSHROOM_2, 1), Extractors.textureColor("blocks/mushroom_block_skin_red.png"));
        map.put(new BlockKey(Material.HUGE_MUSHROOM_2, 2), Extractors.textureColor("blocks/mushroom_block_skin_red.png"));
        map.put(new BlockKey(Material.HUGE_MUSHROOM_2, 3), Extractors.textureColor("blocks/mushroom_block_skin_red.png"));
        map.put(new BlockKey(Material.HUGE_MUSHROOM_2, 4), Extractors.textureColor("blocks/mushroom_block_skin_red.png"));
        map.put(new BlockKey(Material.HUGE_MUSHROOM_2, 5), Extractors.textureColor("blocks/mushroom_block_skin_red.png"));
        map.put(new BlockKey(Material.HUGE_MUSHROOM_2, 6), Extractors.textureColor("blocks/mushroom_block_skin_red.png"));
        map.put(new BlockKey(Material.HUGE_MUSHROOM_2, 7), Extractors.textureColor("blocks/mushroom_block_skin_red.png"));
        map.put(new BlockKey(Material.HUGE_MUSHROOM_2, 8), Extractors.textureColor("blocks/mushroom_block_skin_red.png"));
        map.put(new BlockKey(Material.HUGE_MUSHROOM_2, 9), Extractors.textureColor("blocks/mushroom_block_skin_red.png"));
        map.put(new BlockKey(Material.HUGE_MUSHROOM_2, 10), Extractors.textureColor("blocks/mushroom_block_skin_stem.png"));
        map.put(new BlockKey(Material.HUGE_MUSHROOM_2, 11), Extractors.textureColor("blocks/mushroom_block_inside.png"));
        map.put(new BlockKey(Material.HUGE_MUSHROOM_2, 12), Extractors.textureColor("blocks/mushroom_block_inside.png"));
        map.put(new BlockKey(Material.HUGE_MUSHROOM_2, 13), Extractors.textureColor("blocks/mushroom_block_inside.png"));
        map.put(new BlockKey(Material.HUGE_MUSHROOM_2, 14), Extractors.textureColor("blocks/mushroom_block_skin_red.png"));
        map.put(new BlockKey(Material.HUGE_MUSHROOM_2, 15), Extractors.textureColor("blocks/mushroom_block_skin_stem.png"));

        map.put(new BlockKey(Material.IRON_FENCE, 0), Extractors.textureColor("blocks/iron_bars.png")); //101:0
        map.put(new BlockKey(Material.THIN_GLASS, 0), Extractors.textureColor("blocks/glass.png"));
        map.put(new BlockKey(Material.MELON_BLOCK, 0), Extractors.textureColor("blocks/melon_side.png"));
        //map.put(new BlockKey(Material.VINE, 0), Extractors.textureColor("blocks/vine.png"));
        map.put(new BlockKey(Material.FENCE_GATE, 0), Extractors.textureColor("blocks/planks_oak.png"));
        map.put(new BlockKey(Material.BRICK_STAIRS, 0), Extractors.textureColor("blocks/brick.png"));
        map.put(new BlockKey(Material.SMOOTH_STAIRS, 0), Extractors.textureColor("blocks/stonebrick.png"));
        map.put(new BlockKey(Material.MYCEL, 0), Extractors.textureColor("blocks/mycelium_top.png"));
        map.put(new BlockKey(Material.NETHER_BRICK, 0), Extractors.textureColor("blocks/nether_brick.png"));
        map.put(new BlockKey(Material.NETHER_FENCE, 0), Extractors.textureColor("blocks/nether_brick.png"));
        map.put(new BlockKey(Material.NETHER_BRICK_STAIRS, 0), Extractors.textureColor("blocks/nether_brick.png"));
        map.put(new BlockKey(Material.NETHER_WARTS, 0), Extractors.textureColor("blocks/nether_wart_stage_0.png"));
        map.put(new BlockKey(Material.NETHER_WARTS, 1), Extractors.textureColor("blocks/nether_wart_stage_1.png"));
        map.put(new BlockKey(Material.NETHER_WARTS, 2), Extractors.textureColor("blocks/nether_wart_stage_1.png"));
        map.put(new BlockKey(Material.NETHER_WARTS, 3), Extractors.textureColor("blocks/nether_wart_stage_2.png"));
        map.put(new BlockKey(Material.ENCHANTMENT_TABLE, 0), Extractors.textureColor("blocks/enchanting_table_side.png"));
        map.put(new BlockKey(Material.BREWING_STAND, 0), Extractors.textureColor("blocks/brewing_stand.png"));
        map.put(new BlockKey(Material.CAULDRON, 0), Extractors.textureColor("blocks/cauldron_side.png"));
        map.put(new BlockKey(Material.ENDER_PORTAL, 0), Extractors.textureColor("entity/end_portal.png"));
        map.put(new BlockKey(Material.ENDER_PORTAL_FRAME, 0), Extractors.textureColor("blocks/endframe_side.png"));
        map.put(new BlockKey(Material.ENDER_STONE, 0), Extractors.textureColor("blocks/end_stone.png"));
        map.put(new BlockKey(Material.DRAGON_EGG, 0), Extractors.textureColor("blocks/dragon_egg.png"));
        map.put(new BlockKey(Material.REDSTONE_LAMP_OFF, 0), Extractors.textureColor("blocks/redstone_lamp_off.png"));
        map.put(new BlockKey(Material.REDSTONE_LAMP_ON, 0), Extractors.textureColor("blocks/redstone_lamp_on.png"));

        map.put(new BlockKey(Material.STAINED_CLAY, 0), Extractors.textureColor("blocks/hardened_clay_stained_white.png")); //159:0
        map.put(new BlockKey(Material.STAINED_CLAY, 1), Extractors.textureColor("blocks/hardened_clay_stained_orange.png"));
        map.put(new BlockKey(Material.STAINED_CLAY, 2), Extractors.textureColor("blocks/hardened_clay_stained_magenta.png"));
        map.put(new BlockKey(Material.STAINED_CLAY, 3), Extractors.textureColor("blocks/hardened_clay_stained_light_blue.png"));
        map.put(new BlockKey(Material.STAINED_CLAY, 4), Extractors.textureColor("blocks/hardened_clay_stained_yellow.png"));
        map.put(new BlockKey(Material.STAINED_CLAY, 5), Extractors.textureColor("blocks/hardened_clay_stained_lime.png"));
        map.put(new BlockKey(Material.STAINED_CLAY, 6), Extractors.textureColor("blocks/hardened_clay_stained_pink.png"));
        map.put(new BlockKey(Material.STAINED_CLAY, 7), Extractors.textureColor("blocks/hardened_clay_stained_gray.png"));
        map.put(new BlockKey(Material.STAINED_CLAY, 8), Extractors.textureColor("blocks/hardened_clay_stained_silver.png"));
        map.put(new BlockKey(Material.STAINED_CLAY, 9), Extractors.textureColor("blocks/hardened_clay_stained_cyan.png"));
        map.put(new BlockKey(Material.STAINED_CLAY, 10), Extractors.textureColor("blocks/hardened_clay_stained_purple.png"));
        map.put(new BlockKey(Material.STAINED_CLAY, 11), Extractors.textureColor("blocks/hardened_clay_stained_blue.png"));
        map.put(new BlockKey(Material.STAINED_CLAY, 12), Extractors.textureColor("blocks/hardened_clay_stained_brown.png"));
        map.put(new BlockKey(Material.STAINED_CLAY, 13), Extractors.textureColor("blocks/hardened_clay_stained_green.png"));
        map.put(new BlockKey(Material.STAINED_CLAY, 14), Extractors.textureColor("blocks/hardened_clay_stained_red.png"));
        map.put(new BlockKey(Material.STAINED_CLAY, 15), Extractors.textureColor("blocks/hardened_clay_stained_black.png"));

        map.put(new BlockKey(Material.CARPET, 0), Extractors.textureColor("blocks/wool_colored_white.png")); //171:0
        map.put(new BlockKey(Material.CARPET, 1), Extractors.textureColor("blocks/wool_colored_orange.png"));
        map.put(new BlockKey(Material.CARPET, 2), Extractors.textureColor("blocks/wool_colored_magenta.png"));
        map.put(new BlockKey(Material.CARPET, 3), Extractors.textureColor("blocks/wool_colored_light_blue.png"));
        map.put(new BlockKey(Material.CARPET, 4), Extractors.textureColor("blocks/wool_colored_yellow.png"));
        map.put(new BlockKey(Material.CARPET, 5), Extractors.textureColor("blocks/wool_colored_lime.png"));
        map.put(new BlockKey(Material.CARPET, 6), Extractors.textureColor("blocks/wool_colored_pink.png"));
        map.put(new BlockKey(Material.CARPET, 7), Extractors.textureColor("blocks/wool_colored_gray.png"));
        map.put(new BlockKey(Material.CARPET, 8), Extractors.textureColor("blocks/wool_colored_silver.png"));
        map.put(new BlockKey(Material.CARPET, 9), Extractors.textureColor("blocks/wool_colored_cyan.png"));
        map.put(new BlockKey(Material.CARPET, 10), Extractors.textureColor("blocks/wool_colored_purple.png"));
        map.put(new BlockKey(Material.CARPET, 11), Extractors.textureColor("blocks/wool_colored_blue.png"));
        map.put(new BlockKey(Material.CARPET, 12), Extractors.textureColor("blocks/wool_colored_brown.png"));
        map.put(new BlockKey(Material.CARPET, 13), Extractors.textureColor("blocks/wool_colored_green.png"));
        map.put(new BlockKey(Material.CARPET, 14), Extractors.textureColor("blocks/wool_colored_red.png"));
        map.put(new BlockKey(Material.CARPET, 15), Extractors.textureColor("blocks/wool_colored_black.png"));
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
        String name = zip.getName();
        name = name.substring(0, name.lastIndexOf('.'));
        ColorMap result = new ColorMap(name);

        Enumeration<? extends ZipEntry> entries = zip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            System.out.println(entry.getName());
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
                if (!Colors.isVisible(rgb)) continue;
                int[] color = Colors.split(rgb);

                for (int i = 0; i<color.length; i++)
                    sum[i] += color[i];

                count++;
            }

            return count==0?
                    Colors.INVISIBLE_WHITE :
                    Colors.fromRGB(sum[0]/count, sum[1]/count, sum[2]/count, sum[3]/count);
        }

    }

}
