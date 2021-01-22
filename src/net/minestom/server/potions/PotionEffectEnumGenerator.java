package net.minestom.server.potions;

import java.io.File;
import java.io.IOException;

import net.minestom.server.BasicEnumGenerator;

public class PotionEffectEnumGenerator extends BasicEnumGenerator {

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Usage: <MC version> [target folder]");
            return;
        }

        String targetPart = DEFAULT_TARGET_PATH;
        if (args.length >= 2) {
            targetPart = args[1];
        }

        File targetFolder = new File(targetPart);
        if (!targetFolder.exists()) {
            targetFolder.mkdirs();
        }

        new PotionEffectEnumGenerator(targetFolder);
    }

    private PotionEffectEnumGenerator(File targetFolder) throws IOException {
        super(targetFolder, true, true);
    }

    @Override
    protected String getCategoryID() {
        return "minecraft:mob_effect";
    }

    @Override
    public String getPackageName() {
        return "net.minestom.server.potion";
    }

    @Override
    public String getClassName() {
        return "PotionEffect";
    }
}