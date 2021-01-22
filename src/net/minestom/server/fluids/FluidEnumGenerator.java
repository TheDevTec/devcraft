package net.minestom.server.fluids;

import java.io.File;
import java.io.IOException;

import net.minestom.server.BasicEnumGenerator;

public class FluidEnumGenerator extends BasicEnumGenerator {

    public static void main(String[] args) throws IOException {
        if(args.length < 1) {
            System.err.println("Usage: <MC version> [target folder]");
            return;
        }

        String targetPart = DEFAULT_TARGET_PATH;
        if(args.length >= 2) {
            targetPart = args[1];
        }

        File targetFolder = new File(targetPart);
        if(!targetFolder.exists()) {
            targetFolder.mkdirs();
        }

        new FluidEnumGenerator(targetFolder);
    }

    private FluidEnumGenerator(File targetFolder) throws IOException {
        super(targetFolder);
    }

    @Override
    protected String getCategoryID() {
        return "minecraft:fluid";
    }

    @Override
    public String getPackageName() {
        return "net.minestom.server.fluids";
    }

    @Override
    public String getClassName() {
        return "Fluid";
    }
}
