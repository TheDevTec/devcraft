package me.devtec.fang.world.biome;

import net.minestom.server.world.biomes.Biome;
import net.minestom.server.world.biomes.BiomeEffects;

public class BiomeTint {

    private enum GrassTint {
        BADLANDS(0x90814D), DESERT(0xBFB755), JUNGLE(0x59C93C), JUNGLE_EDGE(0x59C93C),
        FOREST(0x79C05A), BIRCH_FOREST(0x88BB67), DARK_FOREST(0x507A32), SWAMP(0x6A7039),
        SWAMPNOISE(0x4C763C), PLAINS(0x91BD59), OCEAN(0x8EB971), MUSHROOM_FIELDS(0x55C93F),
        MOUNTAINS(0x8AB689), SNOWY_BEACH(0x83B593), GIANT_TREE_TAIGA(0x86B87F), TAIGA(0x86B783), SNOWY(0x80B497);

        int type;

        GrassTint(int type) {
            this.type = type;
        }

        private int getType() {
            return type;
        }
    }

    private enum FoliageTint {
        BADLANDS(0x9E814D), DESERT(0xAEA42A), JUNGLE(0x30BB0B), JUNGLE_EDGE(0x3EB80F),
        FOREST(0x59AE30), BIRCH_FOREST(0x6BA941), DARK_FOREST(0x59AE30), SWAMP(0x6A7039),
        SWAMPNOISE(0x6A7039), PLAINS(0x77AB2F), OCEAN(0x71A74D), MUSHROOM_FIELDS(0x2BBB0F),
        MOUNTAINS(0x6DA36B), SNOWY_BEACH(0x64A278), GIANT_TREE_TAIGA(0x68A55F), TAIGA(0x68A464), SNOWY(0x60A17B);

        int type;

        FoliageTint(int type) {
            this.type = type;
        }

        private int getType() {
            return type;
        }
    }

    private enum WaterTint {
        DEFAULT(0x3F76E4), COLD_OCEAN(0x3D57D6), FROZEN_OCEAN(0x3938C9),
        LUKEWARM_OCEAN(0x45ADF2), SWAMP(0x617B64), WARM_OCEAN(0x43D5EE);

        int type;

        WaterTint(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }

    private enum SkyTint {
        WARM(0x87B8FF), LUSH(0x93B5FF), COLD(0x96B1FF), SNOWY(0x99AFFF);

        int type;

        SkyTint(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }

    private enum FogTint {
        WARM(0xE6EFF4), LUSH(0xBAD3FF), COLD(0xBAD3FF), SNOWY(0xBBD2FF);

        int type;

        FogTint(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }

    public static final BiomeEffects DEFAULT_EFFECTS = BiomeEffects.builder()
            .fogColor(0xC0D8FF)
            .skyColor(0x78A7FF)
            .waterColor(0x3F76E4)
            .waterFogColor(0x50533)
            .build();

    public static final BiomeEffects BADLANDS = BiomeEffects.builder()
            .skyColor(SkyTint.WARM.type)
            .fogColor(FogTint.WARM.type)
            .waterColor(WaterTint.DEFAULT.type)
            .grassColor(GrassTint.BADLANDS.type)
            .foliageColor(FoliageTint.BADLANDS.type)
            .build();

    public static final BiomeEffects DESERT = BiomeEffects.builder()
            .skyColor(SkyTint.WARM.type)
            .fogColor(FogTint.WARM.type)
            .waterColor(WaterTint.DEFAULT.type)
            .grassColor(GrassTint.DESERT.type)
            .foliageColor(FoliageTint.DESERT.type)
            .build();

    public static final BiomeEffects JUNGLE = BiomeEffects.builder()
            .skyColor(SkyTint.LUSH.type)
            .fogColor(FogTint.LUSH.type)
            .waterColor(WaterTint.DEFAULT.type)
            .grassColor(GrassTint.JUNGLE.type)
            .foliageColor(FoliageTint.JUNGLE.type)
            .build();

    public static final BiomeEffects JUNGLE_EDGE = BiomeEffects.builder()
            .skyColor(SkyTint.LUSH.type)
            .fogColor(FogTint.LUSH.type)
            .waterColor(WaterTint.DEFAULT.type)
            .grassColor(GrassTint.JUNGLE_EDGE.type)
            .foliageColor(FoliageTint.JUNGLE_EDGE.type)
            .build();

    public static final BiomeEffects FOREST = BiomeEffects.builder()
            .skyColor(SkyTint.LUSH.type)
            .fogColor(FogTint.LUSH.type)
            .waterColor(WaterTint.DEFAULT.type)
            .grassColor(GrassTint.FOREST.type)
            .foliageColor(FoliageTint.FOREST.type)
            .build();

    public static final BiomeEffects BIRCH_FOREST = BiomeEffects.builder()
            .skyColor(SkyTint.LUSH.type)
            .fogColor(FogTint.LUSH.type)
            .waterColor(WaterTint.DEFAULT.type)
            .grassColor(GrassTint.BIRCH_FOREST.type)
            .foliageColor(FoliageTint.BIRCH_FOREST.type)
            .build();

    public static final BiomeEffects DARK_FOREST = BiomeEffects.builder()
            .skyColor(SkyTint.LUSH.type)
            .fogColor(FogTint.LUSH.type)
            .waterColor(WaterTint.DEFAULT.type)
            .grassColor(GrassTint.DARK_FOREST.type)
            .foliageColor(FoliageTint.DARK_FOREST.type)
            .build();

    public static final BiomeEffects SWAMP = BiomeEffects.builder()
            .skyColor(SkyTint.LUSH.type)
            .fogColor(FogTint.LUSH.type)
            .waterColor(WaterTint.SWAMP.type)
            .grassColor(GrassTint.SWAMP.type)
            .foliageColor(FoliageTint.SWAMP.type)
            .build();

    public static final BiomeEffects SWAMPNOISE = BiomeEffects.builder()
            .skyColor(SkyTint.LUSH.type)
            .fogColor(FogTint.LUSH.type)
            .waterColor(WaterTint.SWAMP.type)
            .grassColor(GrassTint.SWAMPNOISE.type)
            .foliageColor(FoliageTint.SWAMPNOISE.type)
            .build();

    public static final BiomeEffects PLAINS = BiomeEffects.builder()
            .skyColor(SkyTint.LUSH.type)
            .fogColor(FogTint.LUSH.type)
            .waterColor(WaterTint.DEFAULT.type)
            .grassColor(GrassTint.PLAINS.type)
            .foliageColor(FoliageTint.PLAINS.type)
            .build();

    public static final BiomeEffects MUSHROOM_FIELDS = BiomeEffects.builder()
            .skyColor(SkyTint.LUSH.type)
            .fogColor(FogTint.LUSH.type)
            .waterColor(WaterTint.DEFAULT.type)
            .grassColor(GrassTint.MUSHROOM_FIELDS.type)
            .foliageColor(FoliageTint.MUSHROOM_FIELDS.type)
            .build();

    public static final BiomeEffects MOUNTAINS = BiomeEffects.builder()
            .skyColor(SkyTint.COLD.type)
            .fogColor(FogTint.COLD.type)
            .waterColor(WaterTint.DEFAULT.type)
            .grassColor(GrassTint.MOUNTAINS.type)
            .foliageColor(FoliageTint.MOUNTAINS.type)
            .build();

    public static final BiomeEffects SNOWY_BEACH = BiomeEffects.builder()
            .skyColor(SkyTint.COLD.type)
            .fogColor(FogTint.COLD.type)
            .waterColor(WaterTint.DEFAULT.type)
            .grassColor(GrassTint.SNOWY_BEACH.type)
            .foliageColor(FoliageTint.SNOWY_BEACH.type)
            .build();

    public static final BiomeEffects GIANT_TREE_TAIGA = BiomeEffects.builder()
            .skyColor(SkyTint.COLD.type)
            .fogColor(FogTint.COLD.type)
            .waterColor(WaterTint.DEFAULT.type)
            .grassColor(GrassTint.GIANT_TREE_TAIGA.type)
            .foliageColor(FoliageTint.GIANT_TREE_TAIGA.type)
            .build();

    public static final BiomeEffects TAIGA = BiomeEffects.builder()
            .skyColor(SkyTint.COLD.type)
            .fogColor(FogTint.COLD.type)
            .waterColor(WaterTint.DEFAULT.type)
            .grassColor(GrassTint.TAIGA.type)
            .foliageColor(FoliageTint.TAIGA.type)
            .build();

    public static final BiomeEffects SNOWY = BiomeEffects.builder()
            .skyColor(SkyTint.SNOWY.type)
            .fogColor(FogTint.SNOWY.type)
            .waterColor(WaterTint.DEFAULT.type)
            .grassColor(GrassTint.SNOWY.type)
            .foliageColor(FoliageTint.SNOWY.type)
            .build();
}
