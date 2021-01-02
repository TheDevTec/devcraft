package me.devtec.fang.world.biome;

import me.devtec.fang.world.NoiseGen;
import net.minestom.server.MinecraftServer;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.biomes.Biome;
import net.minestom.server.world.biomes.BiomeEffects;
import net.minestom.server.world.biomes.BiomeManager;
import org.apache.commons.codec.binary.Hex;

public class BiomeProperties {

    private static BiomeManager biomeMan(){
        return MinecraftServer.getBiomeManager();
    }

    public static void registerAllBiomes(){
        biomeMan().removeBiome(Biome.PLAINS);
        biomeMan().addBiome(SNOWY_TUNDRA);
        biomeMan().addBiome(ICE_SPIKES);
        biomeMan().addBiome(SNOWY_TAIGA);
        biomeMan().addBiome(SNOWY_TAIGA_MOUNTAINS);
        biomeMan().addBiome(FROZEN_RIVER);
        biomeMan().addBiome(SNOWY_BEACH);
        biomeMan().addBiome(MOUNTAINS);
        biomeMan().addBiome(GRAVELLY_MOUNTAINS);
        biomeMan().addBiome(GRAVELLY_MOUNTAINS);
        biomeMan().addBiome(WOODED_MOUNTAINS);
        biomeMan().addBiome(MODIFIED_GRAVELLY_MOUNTAINS);
        biomeMan().addBiome(TAIGA);
        biomeMan().addBiome(TAIGA_MOUNTAINS);
        biomeMan().addBiome(GIANT_TREE_TAIGA);
        biomeMan().addBiome(GIANT_SPRUCE_TAIGA);
        biomeMan().addBiome(PLAINS);
        biomeMan().addBiome(SUNFLOWER_PLAINS);
        biomeMan().addBiome(FOREST);
        biomeMan().addBiome(FLOWER_FOREST);
        biomeMan().addBiome(BIRCH_FOREST);
        biomeMan().addBiome(TALL_BIRCH_FOREST);
        biomeMan().addBiome(DARK_FOREST);
        biomeMan().addBiome(DARK_FOREST_HILLS);
        //biomeMan().addBiome(SWAMP);
        //biomeMan().addBiome(SWAMP_HILLS); WHY DOES IT HATE SWAMPS
        biomeMan().addBiome(JUNGLE);
        biomeMan().addBiome(JUNGLE_EDGE);
        biomeMan().addBiome(BAMBOO_JUNGLE);
        biomeMan().addBiome(MODIFIED_JUNGLE);
        biomeMan().addBiome(MODIFIED_JUNGLE_EDGE);
        biomeMan().addBiome(RIVER);
        biomeMan().addBiome(BEACH);
        biomeMan().addBiome(MUSHROOM_FIELDS);
        biomeMan().addBiome(MUSHROOM_FIELD_SHORE);
        biomeMan().addBiome(DESERT);
        biomeMan().addBiome(DESERT_LAKES);
        biomeMan().addBiome(SAVANNA);
        biomeMan().addBiome(SAVANNA_PLATEAU);
        biomeMan().addBiome(SHATTERED_SAVANNA);
        biomeMan().addBiome(SHATTERED_SAVANNA_PLATEAU);
        biomeMan().addBiome(BADLANDS);
        biomeMan().addBiome(BADLANDS_PLATEAU);
        biomeMan().addBiome(ERODED_BADLANDS);
        biomeMan().addBiome(WOODED_BADLANDS_PLATEAU);
        biomeMan().addBiome(MODIFIED_BADLANDS_PLATEAU);
        biomeMan().addBiome(MODIFIED_WOODED_BADLANDS_PLATEAU);
    }

    public static final Biome SNOWY_TUNDRA = Biome.builder()
            .category(Biome.Category.ICY)
            .name(NamespaceID.from("minecraft:snowy_tundra"))
            .temperature(0.0F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.SNOWY)
            .build();

    public static final Biome ICE_SPIKES = Biome.builder()
            .category(Biome.Category.ICY)
            .name(NamespaceID.from("minecraft:ice_spikes"))
            .temperature(0.0F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.SNOWY)
            .build();

    public static final Biome SNOWY_TAIGA = Biome.builder()
            .category(Biome.Category.ICY)
            .name(NamespaceID.from("minecraft:snowy_taiga"))
            .temperature(-0.5F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.SNOWY)
            .build();

    public static final Biome SNOWY_TAIGA_MOUNTAINS = Biome.builder()
            .category(Biome.Category.ICY)
            .name(NamespaceID.from("minecraft:snowy_taiga_hills"))
            .temperature(-0.5F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.SNOWY)
            .build();

    public static final Biome FROZEN_RIVER = Biome.builder()
            .category(Biome.Category.ICY)
            .name(NamespaceID.from("minecraft:frozen_river"))
            .temperature(0.0F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.SNOWY)
            .build();

    public static final Biome SNOWY_BEACH = Biome.builder()
            .category(Biome.Category.BEACH)
            .name(NamespaceID.from("minecraft:snowy_beach"))
            .temperature(0.05F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.SNOWY_BEACH)
            .build();

    public static final Biome MOUNTAINS = Biome.builder()
            .category(Biome.Category.NONE)
            .name(NamespaceID.from("minecraft:mountains"))
            .temperature(0.2F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.MOUNTAINS)
            .build();

    public static final Biome GRAVELLY_MOUNTAINS = Biome.builder()
            .category(Biome.Category.NONE)
            .name(NamespaceID.from("minecraft:gravelly_mountains"))
            .temperature(0.2F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.MOUNTAINS)
            .build();

    public static final Biome WOODED_MOUNTAINS = Biome.builder()
            .category(Biome.Category.NONE)
            .name(NamespaceID.from("minecraft:wooded_mountains"))
            .temperature(0.2F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.MOUNTAINS)
            .build();

    public static final Biome MODIFIED_GRAVELLY_MOUNTAINS = Biome.builder()
            .category(Biome.Category.NONE)
            .name(NamespaceID.from("minecraft:modified_gravelly_mountains"))
            .temperature(0.2F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.MOUNTAINS)
            .build();

    public static final Biome TAIGA = Biome.builder()
            .category(Biome.Category.TAIGA)
            .name(NamespaceID.from("minecraft:taiga"))
            .temperature(0.25F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.TAIGA)
            .build();

    public static final Biome TAIGA_MOUNTAINS = Biome.builder()
            .category(Biome.Category.TAIGA)
            .name(NamespaceID.from("minecraft:taiga_mountains"))
            .temperature(0.25F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.TAIGA)
            .build();

    public static final Biome GIANT_TREE_TAIGA = Biome.builder()
            .category(Biome.Category.TAIGA)
            .name(NamespaceID.from("minecraft:giant_tree_taiga"))
            .temperature(0.25F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.GIANT_TREE_TAIGA)
            .build();

    public static final Biome GIANT_SPRUCE_TAIGA = Biome.builder()
            .category(Biome.Category.TAIGA)
            .name(NamespaceID.from("minecraft:giant_spruce_taiga"))
            .temperature(0.25F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.TAIGA)
            .build();

    public static final Biome PLAINS = Biome.builder()
            .category(Biome.Category.PLAINS)
            .name(NamespaceID.from("minecraft:plains"))
            .temperature(0.8F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.PLAINS)
            .build();

    public static final Biome SUNFLOWER_PLAINS = Biome.builder()
            .category(Biome.Category.PLAINS)
            .name(NamespaceID.from("minecraft:sunflower_plains"))
            .temperature(0.8F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.PLAINS)
            .build();

    public static final Biome FOREST = Biome.builder()
            .category(Biome.Category.FOREST)
            .name(NamespaceID.from("minecraft:forest"))
            .temperature(0.7F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.FOREST)
            .build();

    public static final Biome FLOWER_FOREST = Biome.builder()
            .category(Biome.Category.FOREST)
            .name(NamespaceID.from("minecraft:flower_forest"))
            .temperature(0.7F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.FOREST)
            .build();

    public static final Biome BIRCH_FOREST = Biome.builder()
            .category(Biome.Category.FOREST)
            .name(NamespaceID.from("minecraft:birch_forest"))
            .temperature(0.6F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.BIRCH_FOREST)
            .build();

    public static final Biome TALL_BIRCH_FOREST = Biome.builder()
            .category(Biome.Category.FOREST)
            .name(NamespaceID.from("minecraft:tall_birch_forest"))
            .temperature(0.7F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.BIRCH_FOREST)
            .build();

    public static final Biome DARK_FOREST = Biome.builder()
            .category(Biome.Category.FOREST)
            .name(NamespaceID.from("minecraft:dark_forest"))
            .temperature(0.7F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.DARK_FOREST)
            .build();

    public static final Biome DARK_FOREST_HILLS = Biome.builder()
            .category(Biome.Category.FOREST)
            .name(NamespaceID.from("minecraft:dark_forest_hills"))
            .temperature(0.7F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.DARK_FOREST)
            .build();

    public static final Biome SWAMP = Biome.builder()
            .category(Biome.Category.SWAMP)
            .name(NamespaceID.from("minecraft:swamp"))
            .temperature(0.8F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.SWAMP)
            .build();

    public static final Biome SWAMP_HILLS = Biome.builder()
            .category(Biome.Category.SWAMP)
            .name(NamespaceID.from("minecraft:swamp_hills"))
            .temperature(0.8F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.SWAMP)
            .build();

    public static final Biome JUNGLE = Biome.builder()
            .category(Biome.Category.JUNGLE)
            .name(NamespaceID.from("minecraft:jungle"))
            .temperature(0.95F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.JUNGLE)
            .build();

    public static final Biome MODIFIED_JUNGLE = Biome.builder()
            .category(Biome.Category.JUNGLE)
            .name(NamespaceID.from("minecraft:modified_jungle"))
            .temperature(0.95F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.JUNGLE)
            .build();

    public static final Biome JUNGLE_EDGE = Biome.builder()
            .category(Biome.Category.JUNGLE)
            .name(NamespaceID.from("minecraft:jungle_egde"))
            .temperature(0.95F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.JUNGLE_EDGE)
            .build();

    public static final Biome MODIFIED_JUNGLE_EDGE = Biome.builder()
            .category(Biome.Category.JUNGLE)
            .name(NamespaceID.from("minecraft:modified_jungle_egde"))
            .temperature(0.95F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.JUNGLE_EDGE)
            .build();

    public static final Biome BAMBOO_JUNGLE = Biome.builder()
            .category(Biome.Category.JUNGLE)
            .name(NamespaceID.from("minecraft:bamboo_jungle"))
            .temperature(0.95F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.JUNGLE)
            .build();

    public static final Biome RIVER = Biome.builder()
            .category(Biome.Category.RIVER)
            .name(NamespaceID.from("minecraft:river"))
            .temperature(0.5F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.PLAINS)
            .build();

    public static final Biome BEACH = Biome.builder()
            .category(Biome.Category.BEACH)
            .name(NamespaceID.from("minecraft:beach"))
            .temperature(0.5F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.PLAINS)
            .build();

    public static final Biome MUSHROOM_FIELDS = Biome.builder()
            .category(Biome.Category.MUSHROOM)
            .name(NamespaceID.from("minecraft:mushroom_fields"))
            .temperature(0.9F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.MUSHROOM_FIELDS)
            .build();

    public static final Biome MUSHROOM_FIELD_SHORE = Biome.builder()
            .category(Biome.Category.MUSHROOM)
            .name(NamespaceID.from("minecraft:mushroom_field_shore"))
            .temperature(0.9F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.MUSHROOM_FIELDS)
            .build();

    public static final Biome DESERT = Biome.builder()
            .category(Biome.Category.DESERT)
            .name(NamespaceID.from("minecraft:desert"))
            .temperature(2.0F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.DESERT)
            .build();

    public static final Biome DESERT_LAKES = Biome.builder()
            .category(Biome.Category.DESERT)
            .name(NamespaceID.from("minecraft:desert_lakes"))
            .temperature(2.0F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.DESERT)
            .build();

    public static final Biome SAVANNA = Biome.builder()
            .category(Biome.Category.SAVANNA)
            .name(NamespaceID.from("minecraft:savanna"))
            .temperature(1.2F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.DESERT)
            .build();

    public static final Biome SHATTERED_SAVANNA = Biome.builder()
            .category(Biome.Category.SAVANNA)
            .name(NamespaceID.from("minecraft:shattered_savanna"))
            .temperature(1.1F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.DESERT)
            .build();

    public static final Biome BADLANDS = Biome.builder()
            .category(Biome.Category.MESA)
            .name(NamespaceID.from("minecraft:badlands"))
            .temperature(2.0F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.BADLANDS)
            .build();

    public static final Biome ERODED_BADLANDS = Biome.builder()
            .category(Biome.Category.MESA)
            .name(NamespaceID.from("minecraft:eroded_badlands"))
            .temperature(2.0F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.BADLANDS)
            .build();

    public static final Biome WOODED_BADLANDS_PLATEAU = Biome.builder()
            .category(Biome.Category.MESA)
            .name(NamespaceID.from("minecraft:wooded_badlands_plateau"))
            .temperature(2.0F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.BADLANDS)
            .build();

    public static final Biome MODIFIED_WOODED_BADLANDS_PLATEAU = Biome.builder()
            .category(Biome.Category.MESA)
            .name(NamespaceID.from("minecraft:modified_wooded_badlands_plateau"))
            .temperature(2.0F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.BADLANDS)
            .build();

    public static final Biome SAVANNA_PLATEAU = Biome.builder()
            .category(Biome.Category.SAVANNA)
            .name(NamespaceID.from("minecraft:savanna_plateau"))
            .temperature(1.0F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.DESERT)
            .build();

    public static final Biome BADLANDS_PLATEAU = Biome.builder()
            .category(Biome.Category.MESA)
            .name(NamespaceID.from("minecraft:badlands_plateau"))
            .temperature(2.0F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.BADLANDS)
            .build();

    public static final Biome SHATTERED_SAVANNA_PLATEAU = Biome.builder()
            .category(Biome.Category.SAVANNA)
            .name(NamespaceID.from("minecraft:shattered_savanna_plateau"))
            .temperature(1.0F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.DESERT)
            .build();

    public static final Biome MODIFIED_BADLANDS_PLATEAU = Biome.builder()
            .category(Biome.Category.MESA)
            .name(NamespaceID.from("minecraft:modified_badlands_plateau"))
            .temperature(2.0F)
            .downfall(0.4F)
            .depth(0.125F)
            .scale(0.05F)
            .effects(BiomeTint.BADLANDS)
            .build();

}
