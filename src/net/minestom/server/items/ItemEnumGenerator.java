package net.minestom.server.items;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeSet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import net.minestom.server.EnumGenerator;
import net.minestom.server.MinestomEnumGenerator;
import net.minestom.server.PrismarinePaths;
import net.minestom.server.instance.block.Block;
import net.minestom.server.registry.Registries;
import net.minestom.server.utils.NamespaceID;

/**
 * Generates a Material enum containing all data about items
 * <p>
 * Assumes that Block is available
 */
public class ItemEnumGenerator extends MinestomEnumGenerator<ItemContainer> {

    private final String targetVersion;
    public static void main(String[] args) throws IOException {
        String targetVersion;
        if (args.length < 1) {
            System.err.println("Usage: <MC version> [target folder]");
            return;
        }

        targetVersion = args[0];

        String targetPart = DEFAULT_TARGET_PATH;
        if (args.length >= 2) {
            targetPart = args[1];
        }

        File targetFolder = new File(targetPart);
        if (!targetFolder.exists()) {
            targetFolder.mkdirs();
        }

        new ItemEnumGenerator(targetVersion, targetFolder);
    }

    private ItemEnumGenerator(String targetVersion, File targetFolder) throws IOException {
        this.targetVersion = targetVersion;
        generateTo(targetFolder);
    }

    /**
     * Extract item information from Burger (submodule of Minestom)
     *
     * @param gson
     * @param url
     * @return
     * @throws IOException
     */
    private List<BurgerItem> parseItemsFromBurger(Gson gson, String url) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new URL(url).openStream()))) {
            JsonObject dictionary = gson.fromJson(bufferedReader, JsonArray.class).get(0).getAsJsonObject();
            JsonObject itemMap = dictionary.getAsJsonObject("items").getAsJsonObject("item");
            List<BurgerItem> items = new LinkedList<>();
            for (Entry<String, JsonElement> entry : itemMap.entrySet()) {
                BurgerItem item = gson.fromJson(entry.getValue(), BurgerItem.class);
                items.add(item);
            }
            return items;
        } catch (IOException e) {
            throw e;
        }
    }

    @Override
    public String getPackageName() {
        return "net.minestom.server.item";
    }

    @Override
    public String getClassName() {
        return "Material";
    }

    @Override
    protected Collection<ItemContainer> compile() throws IOException {
        Gson gson = new Gson();
        JsonObject dataPaths = gson.fromJson(new BufferedReader(new FileReader(PRISMARINE_JS_DATA_PATHS)), JsonObject.class);
        JsonObject pathsJson = dataPaths.getAsJsonObject("pc").getAsJsonObject(targetVersion);

        gson.fromJson(pathsJson, PrismarinePaths.class);
        List<BurgerItem> burgerItems = parseItemsFromBurger(gson, BURGER_URL_BASE_URL + targetVersion + ".json");

        TreeSet<ItemContainer> items = new TreeSet<>(ItemContainer::compareTo);
        for (BurgerItem burgerItem : burgerItems) {
            items.add(new ItemContainer(burgerItem.numeric_id, NamespaceID.from("minecraft:" + burgerItem.text_id), burgerItem.max_stack_size, getBlock(burgerItem.text_id.toUpperCase())));
        }
        return items;
    }

    /**
     * Returns a block with the given name. Returns null if none
     *
     * @param itemName
     * @return
     */
    private Block getBlock(String itemName) {
        // special cases
        if (itemName.equals("REDSTONE"))
            return Block.REDSTONE_WIRE;
        // end of special cases

        try {
            return Block.valueOf(itemName);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    protected void prepare(EnumGenerator generator) {
        ClassName className = ClassName.get(getPackageName(), getClassName());
        generator.addClassAnnotation(AnnotationSpec.builder(SuppressWarnings.class).addMember("value", "{$S}", "deprecation").build());
        generator.setParams(ParameterSpec.builder(String.class, "namespaceID").addAnnotation(NotNull.class).build(), ParameterSpec.builder(TypeName.INT, "maxDefaultStackSize").build(),
                ParameterSpec.builder(Block.class, "correspondingBlock").addAnnotation(Nullable.class).build());
        generator.appendToConstructor(code -> {
            code.addStatement("$T.$N.put($T.from(namespaceID), this)", Registries.class, "materials", NamespaceID.class);
        });

        generator.addMethod("getId", new ParameterSpec[0], TypeName.SHORT, code -> { code.addStatement("return (short)ordinal()");});
        generator.addMethod("getName", new ParameterSpec[0], ClassName.get(String.class), code -> { code.addStatement("return namespaceID");});
        generator.addMethod("getMaxDefaultStackSize", new ParameterSpec[0], TypeName.INT, code -> { code.addStatement("return maxDefaultStackSize");});
        generator.addMethod("isBlock", new ParameterSpec[0], TypeName.BOOLEAN, code -> { code.addStatement("return correspondingBlock != null && this != AIR");});
        generator.addMethod("getBlock", new ParameterSpec[0], ClassName.get(Block.class), code -> { code.addStatement("return correspondingBlock");});

        generator.addStaticMethod("fromId", new ParameterSpec[]{ParameterSpec.builder(TypeName.SHORT, "id").build()}, className, code -> {
            code.beginControlFlow("if(id >= 0 && id < values().length)")
                    .addStatement("return values()[id]")
                .endControlFlow()
                .addStatement("return AIR");
        });

        // hard coded methods
        generator.addMethod("isHelmet", new ParameterSpec[0], TypeName.BOOLEAN, code -> { code.addStatement("return toString().endsWith(\"HELMET\")");});
        generator.addMethod("isChestplate", new ParameterSpec[0], TypeName.BOOLEAN, code -> { code.addStatement("return toString().endsWith(\"CHESTPLATE\")");});
        generator.addMethod("isLeggings", new ParameterSpec[0], TypeName.BOOLEAN, code -> { code.addStatement("return toString().endsWith(\"LEGGINGS\")");});
        generator.addMethod("isBoots", new ParameterSpec[0], TypeName.BOOLEAN, code -> { code.addStatement("return toString().endsWith(\"BOOTS\")");});
        generator.addMethod("isArmor", new ParameterSpec[0], TypeName.BOOLEAN, code -> { code.addStatement("return isChestplate() || isHelmet() || isLeggings() || isBoots()");});
        generator.addMethod("isFood", new ParameterSpec[0], TypeName.BOOLEAN, code -> {
            code.beginControlFlow("switch(this)")
                    .add("case APPLE:\n")
                    .add("case MUSHROOM_STEW:\n")
                    .add("case BREAD:\n")
                    .add("case PORKCHOP:\n")
                    .add("case COOKED_PORKCHOP:\n")
                    .add("case GOLDEN_APPLE:\n")
                    .add("case ENCHANTED_GOLDEN_APPLE:\n")
                    .add("case COD:\n")
                    .add("case SALMON:\n")
                    .add("case TROPICAL_FISH:\n")
                    .add("case PUFFERFISH:\n")
                    .add("case COOKED_COD:\n")
                    .add("case COOKED_SALMON:\n")
                    .add("case CAKE:\n")
                    .add("case COOKIE:\n")
                    .add("case MELON_SLICE:\n")
                    .add("case DRIED_KELP:\n")
                    .add("case BEEF:\n")
                    .add("case COOKED_BEEF:\n")
                    .add("case CHICKEN:\n")
                    .add("case COOKED_CHICKEN:\n")
                    .add("case ROTTEN_FLESH:\n")
                    .add("case SPIDER_EYE:\n")
                    .add("case CARROT:\n")
                    .add("case POTATO:\n")
                    .add("case BAKED_POTATO:\n")
                    .add("case POISONOUS_POTATO:\n")
                    .add("case PUMPKIN_PIE:\n")
                    .add("case RABBIT:\n")
                    .add("case COOKED_RABBIT:\n")
                    .add("case RABBIT_STEW:\n")
                    .add("case MUTTON:\n")
                    .add("case COOKED_MUTTON:\n")
                    .add("case BEETROOT:\n")
                    .add("case BEETROOT_SOUP:\n")
                    .add("case SWEET_BERRIES:\n")
                    .add("case HONEY_BOTTLE:\n")
                    .add("case CHORUS_FRUIT:\n")
                    .addStatement("return true")
                .endControlFlow()
                .addStatement("return false");
        });
        generator.addMethod("hasState", new ParameterSpec[0], TypeName.BOOLEAN, code -> {
            code.beginControlFlow("switch(this)")
                    .add("case BOW:\n")
                    .add("case TRIDENT:\n")
                    .add("case CROSSBOW:\n")
                    .add("case SHIELD:\n")
                    .addStatement("return true")
                .endControlFlow()
                .addStatement("return isFood()");
        });
    }

    @Override
    protected void writeSingle(EnumGenerator generator, ItemContainer item) {
        String instanceName = item.getName().getPath().toUpperCase();
        generator.addInstance(instanceName,
                "\"" + item.getName().toString() + "\"",
                item.getStackSize(),
                item.getBlock() == null ? "null" : ("Block." + item.getBlock().name())
        );
    }

    @Override
    protected List<JavaFile> postGeneration(Collection<ItemContainer> items) throws IOException {
        return Collections.emptyList();
    }

    @Override
    protected void postWrite(EnumGenerator generator) {
    }
}
