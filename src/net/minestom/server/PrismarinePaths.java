package net.minestom.server;

import java.io.File;

public class PrismarinePaths {

    private String blocks;
    private String biomes;
    private String items;
    public File getBlockFile() {
        return getFile(blocks, "blocks");
    }

    public File getItemsFile() {
        return getFile(items, "items");
    }

    public File getBiomesFile() {
        return getFile(biomes, "biomes");
    }

    public File getFile(String path, String type) {
        return new File("prismarine-minecraft-data/data/" + path + "/" + type + ".json");
    }
}
