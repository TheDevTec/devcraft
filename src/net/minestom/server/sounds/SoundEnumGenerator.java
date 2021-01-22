package net.minestom.server.sounds;

import java.io.File;
import java.io.IOException;

import net.minestom.server.BasicEnumGenerator;
import net.minestom.server.registry.ResourceGatherer;

public class SoundEnumGenerator extends BasicEnumGenerator {

    public static void main(String[] args) throws IOException {
        String targetVersion;
        if(args.length < 1) {
            System.err.println("Usage: <MC version> [target folder]");
            return;
        }

        targetVersion = args[0];

        try {
            ResourceGatherer.ensureResourcesArePresent(targetVersion); // TODO
        } catch (IOException e) {
            e.printStackTrace();
        }

        String targetPart = DEFAULT_TARGET_PATH;
        if(args.length >= 2) {
            targetPart = args[1];
        }

        File targetFolder = new File(targetPart);
        if(!targetFolder.exists()) {
            targetFolder.mkdirs();
        }

        new SoundEnumGenerator(targetFolder);
    }

    private SoundEnumGenerator(File targetFolder) throws IOException {
        super(targetFolder);
    }

    @Override
    protected String getCategoryID() {
        return "minecraft:sound_event";
    }

    @Override
    public String getPackageName() {
        return "net.minestom.server.sound";
    }

    @Override
    public String getClassName() {
        return "Sound";
    }
}
