public class BiomeTable {
    
    public static void q() {
        
        biome(0, "ocean", new BiomeOcean(
            new BiomeData("Ocean").c(-1.0F).d(0.1F)));
        
        biome(1, "plains", new BiomePlains(false,
            new BiomeData("Plains").c(0.125F).d(0.05F).temp(0.8F).humid(0.4F)));
        
        biome(2, "desert", new BiomeDesert(
            new BiomeData("Desert").c(0.125F).d(0.05F).temp(2.0F).humid(0.0F).customColorsOrSomething()));
        
        biome(3, "extreme_hills", new BiomeBigHills(BiomeBigHills.Type.NORMAL,
            new BiomeData("Extreme Hills").c(1.0F).d(0.5F).temp(0.2F).humid(0.3F)));
        
        biome(4, "forest", new BiomeForest(BiomeForest.Type.NORMAL,
            new BiomeData("Forest").temp(0.7F).humid(0.8F)));
        
        biome(5, "taiga", new BiomeTaiga(BiomeTaiga.Type.NORMAL,
            new BiomeData("Taiga").c(0.2F).d(0.2F).temp(0.25F).humid(0.8F)));
        
        biome(6, "swampland", new BiomeSwamp(
            new BiomeData("Swampland").c(-0.2F).d(0.1F).temp(0.8F).humid(0.9F).color(14745518)));
        
        biome(7, "river", new BiomeRiver(
            new BiomeData("River").c(-0.5F).d(0.0F)));
        
        biome(8, "hell", new BiomeHell(
            new BiomeData("Hell").temp(2.0F).humid(0.0F).customColorsOrSomething()));
        
        biome(9, "sky", new BiomeTheEnd(
            new BiomeData("The End").customColorsOrSomething()));
        
        biome(10, "frozen_ocean", new BiomeOcean(
            new BiomeData("FrozenOcean").c(-1.0F).d(0.1F).temp(0.0F).humid(0.5F).b()));
        
        biome(11, "frozen_river", new BiomeRiver(
            new BiomeData("FrozenRiver").c(-0.5F).d(0.0F).temp(0.0F).humid(0.5F).b()));
        
        biome(12, "ice_flats", new BiomeIcePlains(false,
            new BiomeData("Ice Plains").c(0.125F).d(0.05F).temp(0.0F).humid(0.5F).b()));
        
        biome(13, "ice_mountains", new BiomeIcePlains(false,
            new BiomeData("Ice Mountains").c(0.45F).d(0.3F).temp(0.0F).humid(0.5F).b()));
        
        biome(14, "mushroom_island", new BiomeMushrooms(
            new BiomeData("MushroomIsland").c(0.2F).d(0.3F).temp(0.9F).humid(1.0F)));
        
        biome(15, "mushroom_island_shore", new BiomeMushrooms(
            new BiomeData("MushroomIslandShore").c(0.0F).d(0.025F).temp(0.9F).humid(1.0F)));
        
        biome(16, "beaches", new BiomeBeach(
            new BiomeData("Beach").c(0.0F).d(0.025F).temp(0.8F).humid(0.4F)));
        
        biome(17, "desert_hills", new BiomeDesert(
            new BiomeData("DesertHills").c(0.45F).d(0.3F).temp(2.0F).humid(0.0F).customColorsOrSomething()));
        
        biome(18, "forest_hills", new BiomeForest(BiomeForest.Type.NORMAL,
            new BiomeData("ForestHills").c(0.45F).d(0.3F).temp(0.7F).humid(0.8F)));
        
        biome(19, "taiga_hills", new BiomeTaiga(BiomeTaiga.Type.NORMAL,
            new BiomeData("TaigaHills").temp(0.25F).humid(0.8F).c(0.45F).d(0.3F)));
        
        biome(20, "smaller_extreme_hills", new BiomeBigHills(BiomeBigHills.Type.EXTRA_TREES,
            new BiomeData("Extreme Hills Edge").c(0.8F).d(0.3F).temp(0.2F).humid(0.3F)));
        
        biome(21, "jungle", new BiomeJungle(false,
            new BiomeData("Jungle").temp(0.95F).humid(0.9F)));
        
        biome(22, "jungle_hills", new BiomeJungle(false,
            new BiomeData("JungleHills").c(0.45F).d(0.3F).temp(0.95F).humid(0.9F)));
        
        biome(23, "jungle_edge", new BiomeJungle(true,
            new BiomeData("JungleEdge").temp(0.95F).humid(0.8F)));
        
        biome(24, "deep_ocean", new BiomeOcean(
            new BiomeData("Deep Ocean").c(-1.8F).d(0.1F)));
        
        biome(25, "stone_beach", new BiomeStoneBeach(
            new BiomeData("Stone Beach").c(0.1F).d(0.8F).temp(0.2F).humid(0.3F)));
        
        biome(26, "cold_beach", new BiomeBeach(
            new BiomeData("Cold Beach").c(0.0F).d(0.025F).temp(0.05F).humid(0.3F).b()));
        
        biome(27, "birch_forest", new BiomeForest(BiomeForest.Type.BIRCH,
            new BiomeData("Birch Forest").temp(0.6F).humid(0.6F)));
        
        biome(28, "birch_forest_hills", new BiomeForest(BiomeForest.Type.BIRCH,
            new BiomeData("Birch Forest Hills").c(0.45F).d(0.3F).temp(0.6F).humid(0.6F)));
        
        biome(29, "roofed_forest", new BiomeForest(BiomeForest.Type.ROOFED,
            new BiomeData("Roofed Forest").temp(0.7F).humid(0.8F)));
        
        biome(30, "taiga_cold", new BiomeTaiga(BiomeTaiga.Type.NORMAL,
            new BiomeData("Cold Taiga").c(0.2F).d(0.2F).temp(-0.5F).humid(0.4F).b()));
        
        biome(31, "taiga_cold_hills", new BiomeTaiga(BiomeTaiga.Type.NORMAL,
            new BiomeData("Cold Taiga Hills").c(0.45F).d(0.3F).temp(-0.5F).humid(0.4F).b()));
        
        biome(32, "redwood_taiga", new BiomeTaiga(BiomeTaiga.Type.MEGA,
            new BiomeData("Mega Taiga").temp(0.3F).humid(0.8F).c(0.2F).d(0.2F)));
        
        biome(33, "redwood_taiga_hills", new BiomeTaiga(BiomeTaiga.Type.MEGA,
            new BiomeData("Mega Taiga Hills").c(0.45F).d(0.3F).temp(0.3F).humid(0.8F)));
        
        biome(34, "extreme_hills_with_trees", new BiomeBigHills(BiomeBigHills.Type.EXTRA_TREES,
            new BiomeData("Extreme Hills+").c(1.0F).d(0.5F).temp(0.2F).humid(0.3F)));
        
        biome(35, "savanna", new BiomeSavanna(
            new BiomeData("Savanna").c(0.125F).d(0.05F).temp(1.2F).humid(0.0F).customColorsOrSomething()));
        
        biome(36, "savanna_rock", new BiomeSavanna(
            new BiomeData("Savanna Plateau").c(1.5F).d(0.025F).temp(1.0F).humid(0.0F).customColorsOrSomething()));
        
        biome(37, "mesa", new BiomeMesa(false, false,
            new BiomeData("Mesa").temp(2.0F).humid(0.0F).customColorsOrSomething()));
        
        biome(38, "mesa_rock", new BiomeMesa(false, true,
            new BiomeData("Mesa Plateau F").c(1.5F).d(0.025F).temp(2.0F).humid(0.0F).customColorsOrSomething()));
        
        biome(39, "mesa_clear_rock", new BiomeMesa(false, false,
            new BiomeData("Mesa Plateau").c(1.5F).d(0.025F).temp(2.0F).humid(0.0F).customColorsOrSomething()));
        
        biome(127, "void", new BiomeVoid(
            new BiomeData("The Void").customColorsOrSomething()));
        
        biome(129, "mutated_plains", new BiomePlains(true,
            new BiomeData("Sunflower Plains").parent("plains").c(0.125F).d(0.05F).temp(0.8F).humid(0.4F)));
        
        biome(130, "mutated_desert", new BiomeDesert(
            new BiomeData("Desert M").parent("desert").c(0.225F).d(0.25F).temp(2.0F).humid(0.0F).customColorsOrSomething()));
        
        biome(131, "mutated_extreme_hills", new BiomeBigHills(BiomeBigHills.Type.MUTATED,
            new BiomeData("Extreme Hills M").parent("extreme_hills").c(1.0F).d(0.5F).temp(0.2F).humid(0.3F)));
        
        biome(132, "mutated_forest", new BiomeForest(BiomeForest.Type.FLOWER,
            new BiomeData("Flower Forest").parent("forest").d(0.4F).temp(0.7F).humid(0.8F)));
        
        biome(133, "mutated_taiga", new BiomeTaiga(BiomeTaiga.Type.NORMAL,
            new BiomeData("Taiga M").parent("taiga").c(0.3F).d(0.4F).temp(0.25F).humid(0.8F)));
        
        biome(134, "mutated_swampland", new BiomeSwamp(
            new BiomeData("Swampland M").parent("swampland").c(-0.1F).d(0.3F).temp(0.8F).humid(0.9F).temp(14745518)));
        
        biome(140, "mutated_ice_flats", new BiomeIcePlains(true,
            new BiomeData("Ice Plains Spikes").parent("ice_flats").c(0.425F).d(0.45000002F).temp(0.0F).humid(0.5F).humid()));
        
        biome(149, "mutated_jungle", new BiomeJungle(false,
            new BiomeData("Jungle M").parent("jungle").c(0.2F).d(0.4F).temp(0.95F).humid(0.9F)));
        
        biome(151, "mutated_jungle_edge", new BiomeJungle(true,
            new BiomeData("JungleEdge M").parent("jungle_edge").c(0.2F).d(0.4F).temp(0.95F).humid(0.8F)));
        
        biome(155, "mutated_birch_forest", new BiomeForestMutated(
            new BiomeData("Birch Forest M").parent("birch_forest").c(0.2F).d(0.4F).temp(0.6F).humid(0.6F)));
        
        biome(156, "mutated_birch_forest_hills", new BiomeForestMutated(
            new BiomeData("Birch Forest Hills M").parent("birch_forest_hills").c(0.55F).d(0.5F).temp(0.6F).humid(0.6F)));
        
        biome(157, "mutated_roofed_forest", new BiomeForest(BiomeForest.Type.ROOFED,
            new BiomeData("Roofed Forest M").parent("roofed_forest").c(0.2F).d(0.4F).temp(0.7F).humid(0.8F)));
        
        biome(158, "mutated_taiga_cold", new BiomeTaiga(BiomeTaiga.Type.NORMAL,
            new BiomeData("Cold Taiga M").parent("taiga_cold").c(0.3F).d(0.4F).temp(-0.5F).humid(0.4F).humid()));
        
        biome(160, "mutated_redwood_taiga", new BiomeTaiga(BiomeTaiga.Type.MEGA_SPRUCE,
            new BiomeData("Mega Spruce Taiga").parent("redwood_taiga").c(0.2F).d(0.2F).temp(0.25F).humid(0.8F)));
        
        biome(161, "mutated_redwood_taiga_hills", new BiomeTaiga(BiomeTaiga.Type.MEGA_SPRUCE,
            new BiomeData("Redwood Taiga Hills M").parent("redwood_taiga_hills").c(0.2F).d(0.2F).temp(0.25F).humid(0.8F)));
        
        biome(162, "mutated_extreme_hills_with_trees", new BiomeBigHills(BiomeBigHills.Type.MUTATED,
            new BiomeData("Extreme Hills+ M").parent("extreme_hills_with_trees").c(1.0F).d(0.5F).temp(0.2F).humid(0.3F)));
        
        biome(163, "mutated_savanna", new BiomeSavannaMutated(
            new BiomeData("Savanna M").parent("savanna").c(0.3625F).d(1.225F).temp(1.1F).humid(0.0F).customColorsOrSomething()));
        
        biome(164, "mutated_savanna_rock", new BiomeSavannaMutated(
            new BiomeData("Savanna Plateau M").parent("savanna_rock").c(1.05F).d(1.2125001F).temp(1.0F).humid(0.0F).customColorsOrSomething()));
        
        biome(165, "mutated_mesa", new BiomeMesa(true, false,
            new BiomeData("Mesa (Bryce)").parent("mesa").temp(2.0F).humid(0.0F).customColorsOrSomething()));
        
        biome(166, "mutated_mesa_rock", new BiomeMesa(false, true,
            new BiomeData("Mesa Plateau F M").parent("mesa_rock").c(0.45F).d(0.3F).temp(2.0F).humid(0.0F).customColorsOrSomething()));
        
        biome(167, "mutated_mesa_clear_rock", new BiomeMesa(false, false,
            new BiomeData("Mesa Plateau M").parent("mesa_clear_rock").c(0.45F).d(0.3F).temp(2.0F).humid(0.0F).customColorsOrSomething()));
    }
    
    private static void biome(int id, String name, BiomeBase biome) {
        REGISTRY_ID.a(id, new MinecraftKey(name), biome);
        if (biome.b()) {
            i.a(biome, biome((BiomeBase) REGISTRY_ID.get(new MinecraftKey(biome.G))));
        }
    }
    
    public static class BiomeData {
        private final String name;
        private float b = 0.1F;
        private float c = 0.2F;
        private float temperature = 0.5F;
        private float humidity = 0.5F;
        private int color = 0xFFFFFF;
        private boolean g;
        private boolean h = true;
        @Nullable
        private String parent;
        
        public BiomeData(String name) {
            this.name = name;
        }
        
        protected BiomeData temp(float temperature) {
            if ((temperature > 0.1F) && (temperature < 0.2F)) {
                throw new IllegalArgumentException("Please avoid temperatures in the range 0.1 - 0.2 because of snow");
            }
            this.temperature = temperature;
            return this;
        }
        
        protected BiomeData humid(float humidity) {
            this.humidity = humidity;
            return this;
        }
        
        protected BiomeData c(float paramFloat) {
            this.b = paramFloat;
            return this;
        }
        
        protected BiomeData d(float paramFloat) {
            this.c = paramFloat;
            return this;
        }
        
        protected BiomeData customColorsOrSomething() {
            this.h = false;
            return this;
        }
        
        protected BiomeData b() {
            this.g = true;
            return this;
        }
        
        protected BiomeData color(int rgb) {
            this.color = rgb;
            return this;
        }
        
        protected BiomeData parent(String name) {
            this.parent = name;
            return this;
        }
    }
    
}
