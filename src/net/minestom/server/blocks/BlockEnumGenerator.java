package net.minestom.server.blocks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import javax.lang.model.element.Modifier;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import net.minestom.server.EnumGenerator;
import net.minestom.server.MinestomEnumGenerator;
import net.minestom.server.PrismarinePaths;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockAlternative;
import net.minestom.server.registry.Registries;
import net.minestom.server.utils.NamespaceID;

/**
 * Generates a Block enum containing all data about blocks
 */
public class BlockEnumGenerator extends MinestomEnumGenerator<BlockContainer> {

    public static final String MC_DATA_BLOCKS_PATH = "minecraft_data/reports/blocks.json";

    private final String targetVersion;

    private CodeBlock.Builder staticBlock = CodeBlock.builder();


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

        new BlockEnumGenerator(targetVersion, targetFolder);
    }

    private BlockEnumGenerator(String targetVersion, File targetFolder) throws IOException {
        this.targetVersion = targetVersion;
        generateTo(targetFolder);
    }

    /**
     * Compiles all block information in a single location
     *
     * @param dataBlocks
     * @param prismarineJSBlocks
     * @param burgerBlocks
     */
    private Collection<BlockContainer> compile(List<DataReportBlock> dataBlocks, List<PrismarineJSBlock> prismarineJSBlocks, List<BurgerBlock> burgerBlocks) {
        TreeSet<BlockContainer> blocks = new TreeSet<>(BlockContainer::compareTo);
        // ensure the 3 list have the same length and order
        dataBlocks.sort(Comparator.comparing(block -> block.name.toString()));
        prismarineJSBlocks.sort(Comparator.comparing(block -> NamespaceID.from(block.name).toString()));
        burgerBlocks.sort(Comparator.comparing(block -> NamespaceID.from(block.text_id).toString()));

        // if one of these tests fail, you probably forgot to clear the minecraft_data cache before launching this program
        if (dataBlocks.size() != prismarineJSBlocks.size()) {
            throw new Error("minecraft_data block count is different from PrismarineJS count! Try clearing the minecraft_data cache");
        }
        if (prismarineJSBlocks.size() != burgerBlocks.size()) {
            throw new Error("Burger's block count is different from PrismarineJS count! Try clearing the minecraft_data cache");
        }

        for (int i = 0; i < dataBlocks.size(); i++) {
            DataReportBlock data = dataBlocks.get(i);
            PrismarineJSBlock prismarine = prismarineJSBlocks.get(i);
            BurgerBlock burger = burgerBlocks.get(i);

            assert data.name.getPath().equals(prismarine.name) && prismarine.name.equalsIgnoreCase(burger.text_id);

            List<BlockContainer.BlockState> states = new LinkedList<>();
            for (DataReportBlock.BlockState s : data.states) {
                states.add(new BlockContainer.BlockState(s.id, s.properties));
            }

            BlockContainer.BlockState defaultState = new BlockContainer.BlockState(data.defaultState.id, data.defaultState.properties);

            BlockContainer block = new BlockContainer(prismarine.id, data.name, prismarine.hardness, burger.resistance, burger.blockEntity == null ? null : NamespaceID.from(burger.blockEntity.name), defaultState, states);
            if (!"empty".equals(prismarine.boundingBox)) {
                block.setSolid();
            }
            if (data.name.equals(NamespaceID.from("minecraft:water")) || data.name.equals(NamespaceID.from("minecraft:lava"))) {
                block.setLiquid();
            }
            boolean isAir = data.name.equals(NamespaceID.from("minecraft:air")) || data.name.getPath().endsWith("_air");
            if (isAir) {
                block.setAir();
            }

            blocks.add(block);
        }

        return blocks;
    }

    /**
     * Extracts block information from Burger
     *
     * @param gson
     * @param url
     * @return
     * @throws IOException
     */
    private List<BurgerBlock> parseBlocksFromBurger(Gson gson, String url) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new URL(url).openStream()))) {
            JsonObject dictionary = gson.fromJson(bufferedReader, JsonArray.class).get(0).getAsJsonObject();
            JsonObject tileEntityMap = dictionary.getAsJsonObject("tileentity").getAsJsonObject("tileentities");

            Map<String, BurgerTileEntity> block2entityMap = new HashMap<>();
            for (Entry<String, JsonElement> entry : tileEntityMap.entrySet()) {
                BurgerTileEntity te = gson.fromJson(entry.getValue(), BurgerTileEntity.class);
                if (te.blocks != null) {
                    for (String block : te.blocks) {
                        block2entityMap.put(block, te);
                    }
                }
            }

            JsonObject blockMap = dictionary.getAsJsonObject("blocks").getAsJsonObject("block");

            List<BurgerBlock> blocks = new LinkedList<>();
            for (Entry<String, JsonElement> entry : blockMap.entrySet()) {
                BurgerBlock block = gson.fromJson(entry.getValue(), BurgerBlock.class);
                block.blockEntity = block2entityMap.get(block.text_id);
                blocks.add(block);
            }

            return blocks;
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * Extract block information from PrismarineJS (submodule of Minestom)
     *
     * @param gson
     * @param blockFile
     * @return
     * @throws IOException
     */
    private List<PrismarineJSBlock> parseBlocksFromPrismarineJS(Gson gson, File blockFile) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(blockFile))) {
            PrismarineJSBlock[] blocks = gson.fromJson(bufferedReader, PrismarineJSBlock[].class);
            return Arrays.asList(blocks);
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * Extract block information from reports generated by the data extractor present in minecraft_server.jar
     *
     * @param gson
     * @param path
     * @return
     */
    private List<DataReportBlock> parseBlocksFromMCData(Gson gson, String path) {
        List<DataReportBlock> blocks = new LinkedList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));

            JsonObject obj = gson.fromJson(bufferedReader, JsonObject.class);
            for (Entry<String, JsonElement> entry : obj.entrySet()) {
                NamespaceID id = NamespaceID.from(entry.getKey());
                JsonElement blockInfo = entry.getValue();
                DataReportBlock block = gson.fromJson(blockInfo, DataReportBlock.class);
                block.bindDefaultState();
                block.name = id;

                blocks.add(block);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return blocks;
    }

    @Override
    public String getPackageName() {
        return "net.minestom.server.instance.block";
    }

    @Override
    public String getClassName() {
        return "Block";
    }

    @Override
    protected Collection<BlockContainer> compile() throws IOException {
        Gson gson = new Gson();

        // load report blocks and block states
        List<DataReportBlock> dataBlocks = parseBlocksFromMCData(gson, MC_DATA_BLOCKS_PATH);
        // load properties from Prismarine
        JsonObject dataPaths = gson.fromJson(new BufferedReader(new FileReader(PRISMARINE_JS_DATA_PATHS)), JsonObject.class);
        JsonObject pathsJson = dataPaths.getAsJsonObject("pc").getAsJsonObject(targetVersion);

        PrismarinePaths paths = gson.fromJson(pathsJson, PrismarinePaths.class);
        List<PrismarineJSBlock> prismarineJSBlocks = parseBlocksFromPrismarineJS(gson, paths.getBlockFile());

        List<BurgerBlock> burgerBlocks = parseBlocksFromBurger(gson, BURGER_URL_BASE_URL + targetVersion + ".json");

        return compile(dataBlocks, prismarineJSBlocks, burgerBlocks);
    }

    @Override
    protected void prepare(EnumGenerator generator) {
        ClassName className = ClassName.get(getPackageName(), getClassName());
        generator.addClassAnnotation(AnnotationSpec.builder(SuppressWarnings.class).addMember("value", "{$S}", "deprecation").build());

        generator.setParams(
                ParameterSpec.builder(String.class, "namespaceID").build(),
                ParameterSpec.builder(TypeName.SHORT, "defaultID").build(),
                ParameterSpec.builder(TypeName.DOUBLE, "hardness").build(),
                ParameterSpec.builder(TypeName.DOUBLE, "resistance").build(),
                ParameterSpec.builder(TypeName.BOOLEAN, "isAir").build(),
                ParameterSpec.builder(TypeName.BOOLEAN, "isSolid").build(),
                ParameterSpec.builder(NamespaceID.class, "blockEntity").build(),
                ParameterSpec.builder(TypeName.BOOLEAN, "singleState").build()
        );

        generator.addHardcodedField(ParameterizedTypeName.get(List.class, BlockAlternative.class), "alternatives", "new java.util.ArrayList<>()");

        generator.addMethod("getBlockId", new ParameterSpec[0], TypeName.SHORT, code -> code.addStatement("return defaultID"));
        generator.addMethod("getName", new ParameterSpec[0], ClassName.get(String.class), code -> code.addStatement("return namespaceID"));
        generator.addMethod("isAir", new ParameterSpec[0], TypeName.BOOLEAN, code -> code.addStatement("return isAir"));
        generator.addMethod("hasBlockEntity", new ParameterSpec[0], TypeName.BOOLEAN, code -> code.addStatement("return blockEntity != null"));
        generator.addMethod("getBlockEntityName", new ParameterSpec[0], ClassName.get(NamespaceID.class), code -> code.addStatement("return blockEntity"));
        generator.addMethod("isSolid", new ParameterSpec[0], TypeName.BOOLEAN, code -> code.addStatement("return isSolid"));
        generator.addMethod("isLiquid", new ParameterSpec[0], TypeName.BOOLEAN, code -> code.addStatement("return this == WATER || this == LAVA"));
        generator.addMethod("getHardness", new ParameterSpec[0], TypeName.DOUBLE, code -> code.addStatement("return hardness"));
        generator.addMethod("getResistance", new ParameterSpec[0], TypeName.DOUBLE, code -> code.addStatement("return resistance"));
        generator.addMethod("breaksInstantaneously", new ParameterSpec[0], TypeName.BOOLEAN, code -> code.addStatement("return hardness == 0"));
        generator.addMethod("addBlockAlternative", new ParameterSpec[]{ParameterSpec.builder(BlockAlternative.class, "alternative").build()}, TypeName.VOID, code -> {
            code.addStatement("alternatives.add(alternative)")
                .addStatement("$T.blocks[alternative.getId()] = this", ClassName.get("net.minestom.server.instance.block", "BlockArray"));
        });

        generator.addMethod("getAlternative", new ParameterSpec[]{ParameterSpec.builder(TypeName.SHORT, "blockId").build()}, ClassName.get(BlockAlternative.class), code -> {
            code.beginControlFlow("for($T alt : alternatives)", BlockAlternative.class)
                    .beginControlFlow("if(alt.getId() == blockId)")
                        .addStatement("return alt")
                    .endControlFlow()
                .endControlFlow()
                .addStatement("return null");
        });
        generator.addMethod("getAlternatives", new ParameterSpec[0], ParameterizedTypeName.get(List.class, BlockAlternative.class), code -> code.addStatement("return alternatives"));
        generator.addVarargMethod("withProperties", new ParameterSpec[]{ParameterSpec.builder(String[].class, "properties").build()}, TypeName.SHORT, code -> {
            code.beginControlFlow("for($T alt : alternatives)", BlockAlternative.class)
                    .beginControlFlow("if($T.equals(alt.getProperties(), properties))", Arrays.class)
                        .addStatement("return alt.getId()")
                    .endControlFlow()
                .endControlFlow()
                .addStatement("return defaultID");
        });
        generator.addStaticMethod("fromStateId", new ParameterSpec[]{ParameterSpec.builder(TypeName.SHORT, "blockStateId").build()}, className, code -> code.addStatement("return $T.blocks[blockStateId]", ClassName.get("net.minestom.server.instance.block", "BlockArray")));
        generator.appendToConstructor(code -> {
            code.beginControlFlow("if(singleState)")
                    .addStatement("addBlockAlternative(new BlockAlternative(defaultID))")
                .endControlFlow()
                .addStatement("$T.blocks.put($T.from(namespaceID), this)", Registries.class, NamespaceID.class);
        });
    }

    @Override
    protected void writeSingle(EnumGenerator generator, BlockContainer block) {
        String instanceName = block.getId().getPath().toUpperCase();
        generator.addInstance(instanceName,
                "\"" + block.getId().toString() + "\"",
                "(short) " + block.getDefaultState().getId(),
                block.getHardness(),
                block.getResistance(),
                block.isAir(),
                block.isSolid(),
                block.getBlockEntityName() != null ? "NamespaceID.from(\"" + block.getBlockEntityName() + "\")" : "null",
                block.getStates().size() == 1 // used to avoid duplicates inside the 'alternatives' field due to both constructor addition and subclasses initStates()
        );

        if (block.getStates().size() > 1) {
            String blockName = snakeCaseToCapitalizedCamelCase(block.getId().getPath());
            blockName = blockName.replace("_", "");
            staticBlock.addStatement("$T.initStates()", ClassName.get(getPackageName()+".states", blockName));
        }
    }

    @Override
    protected List<JavaFile> postGeneration(Collection<BlockContainer> items) throws IOException {
        List<JavaFile> additionalFiles = new LinkedList<>();

        TypeSpec blockArrayClass = TypeSpec.classBuilder("BlockArray")
                .addModifiers(Modifier.FINAL)
                .addField(FieldSpec.builder(Block[].class, "blocks").initializer("new Block[Short.MAX_VALUE]").addModifiers(Modifier.STATIC, Modifier.FINAL).build())
                .build();
        additionalFiles.add(JavaFile.builder(getPackageName(), blockArrayClass).indent("    ").skipJavaLangImports(true).build());


        final String warningComment = "Completely internal. DO NOT USE. IF YOU ARE A USER AND FACE A PROBLEM WHILE USING THIS CODE, THAT'S ON YOU.";
        final AnnotationSpec internalUseAnnotation = AnnotationSpec.builder(Deprecated.class).addMember("since", "$S", "forever").addMember("forRemoval", "$L", false).build();
        for(BlockContainer block : items) {
            // do not add alternative for default states. This will be added by default inside the constructor
            if (block.getStates().size() > 1) {
                String blockName = snakeCaseToCapitalizedCamelCase(block.getId().getPath());
                blockName = blockName.replace("_", "");
                TypeSpec.Builder subclass = TypeSpec.classBuilder(blockName)
                        .addAnnotation(internalUseAnnotation)
                        .addJavadoc(warningComment)
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

                MethodSpec.Builder initStatesMethod = MethodSpec.methodBuilder("initStates")
                        .returns(TypeName.VOID)
                        .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                        .addAnnotation(internalUseAnnotation)
                        .addJavadoc(warningComment);

                for (BlockContainer.BlockState state : block.getStates()) {
                    if (state == block.getDefaultState())
                        continue;
                    // generate BlockAlternative instance that will be used to lookup block alternatives
                    StringBuilder propertyList = new StringBuilder();
                    // add block state properties if applicable
                    if (state.getProperties() != null) {
                        for (Entry<String, String> property : state.getProperties().entrySet()) {
                            propertyList.append(", ");
                            propertyList.append("\"").append(property.getKey()).append("=").append(property.getValue()).append("\"");
                        }
                    }
                    initStatesMethod.addStatement("$T.$N.addBlockAlternative(new $T((short) $L"+propertyList+"))", Block.class, block.getId().getPath().toUpperCase(), BlockAlternative.class, state.getId());
                }
                subclass.addMethod(initStatesMethod.build());
                staticBlock.addStatement("$T.initStates()", ClassName.get(getPackageName()+".states", blockName));

                additionalFiles.add(JavaFile.builder(getPackageName()+".states", subclass.build())
                        .indent("    ")
                        .skipJavaLangImports(true)
                        .build());
            }
        }

        return additionalFiles;
    }

    @Override
    protected void postWrite(EnumGenerator generator) {
        generator.setStaticInitBlock(staticBlock.build());
    }
}
