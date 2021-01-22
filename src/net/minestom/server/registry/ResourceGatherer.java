package net.minestom.server.registry;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Responsible for making sure Minestom has the necessary files to run (notably registry files)
 */
public class ResourceGatherer {
    public static final File DATA_FOLDER = new File("./minecraft_data/");
    private static final Gson GSON = new Gson();
    private static final File TMP_FOLDER = new File("./.minestom_tmp/");

    /**
     * Checks if registry/ folder is present
     * If it is not, download the minecraft server jar, run the data generator and extract the wanted files
     * If it is already present, directly return
     */
    public static void ensureResourcesArePresent(String version) throws IOException {
        if (DATA_FOLDER.exists()) {
            return;
        }
        if (!TMP_FOLDER.exists() && !TMP_FOLDER.mkdirs()) {
            throw new IOException("Failed to create tmp folder.");
        }
        File serverJar = downloadServerJar(version);

        runDataGenerator(serverJar);

        moveAndCleanup(version);
    }

    private static void moveAndCleanup(String version) throws IOException {
        Path dataFolderPath = DATA_FOLDER.toPath();
        Path tmpFolderPath = TMP_FOLDER.toPath();
        Path generatedFolder = tmpFolderPath.resolve("generated");
        Files.delete(tmpFolderPath.resolve("server_" + version + ".jar"));
        Files.walkFileTree(tmpFolderPath, new SimpFileVisit() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path relativePath = generatedFolder.relativize(dir);
                if (dir.startsWith(generatedFolder)) { // don't copy logs
                    Path resolvedPath = dataFolderPath.resolve(relativePath);
                    Files.createDirectories(resolvedPath);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path relativePath = generatedFolder.relativize(file);
                if (file.startsWith(generatedFolder)) { // don't copy logs
                    Path resolvedPath = dataFolderPath.resolve(relativePath);
                    Files.move(file, resolvedPath);
                } else {
                    Files.delete(file);
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static void runDataGenerator(File serverJar) throws IOException {
        ProcessBuilder dataGenerator = new ProcessBuilder("java", "-cp", serverJar.getName(), "net.minecraft.data.Main", "--all", "--server", "--dev");
        dataGenerator.directory(TMP_FOLDER);
        Process dataGeneratorProcess = dataGenerator.start();

        try {
            int resultCode = dataGeneratorProcess.waitFor();
            if (resultCode != 0) {
                throw new IOException("Data generator finished with non-zero return code " + resultCode + " verify that you have 'java' cli");
            }
        } catch (InterruptedException e) {
            throw new IOException("Data generator was interrupted.", e);
        }
    }

    private static File downloadServerJar(String version) throws IOException {
        // Mojang's version manifest is located at https://launchermeta.mojang.com/mc/game/version_manifest.json
        // If we query this (it's a json object), we can then search for the id we want.
        InputStream versionManifestStream = new URL("https://launchermeta.mojang.com/mc/game/version_manifest.json").openStream();
       
        JsonObject versionManifestJson = GSON.fromJson(new InputStreamReader(versionManifestStream), JsonObject.class);
        
        JsonArray versionArray = versionManifestJson.getAsJsonArray("versions");
        
        JsonObject versionEntry = null;
        for (JsonElement element : versionArray) {
            if (element.isJsonObject()) {
                JsonObject entry = element.getAsJsonObject();
                if (entry.get("id").getAsString().equals(version)) {
                    versionEntry = entry;
                    break;
                }
            }
        }
        if (versionEntry == null) {
            throw new IOException("Could not find " + version + " in Mojang's official list of minecraft versions.");
        }
        // We now have the entry we want and it gives us access to the json file containing the downloads.
        String versionUrl = versionEntry.get("url").getAsString();
        InputStream versionStream = new URL(versionUrl).openStream();
        
        JsonObject versionJson = GSON.fromJson(new InputStreamReader(versionStream), JsonObject.class);
        
        // Now we need to navigate to "downloads.client.url" and "downloads.server.url" }
        JsonObject downloadsJson = versionJson.getAsJsonObject("downloads");

        // Designated spot if we ever need the client.

        // Server
        {
            JsonObject serverJson = downloadsJson.getAsJsonObject("server");
            final String jarURL = serverJson.get("url").getAsString();
            final String sha1 = serverJson.get("sha1").getAsString();
            return download(version, jarURL, sha1);
        }
    }

    private static File download(String version, String url, String sha1Source) throws IOException {
        File target = new File(TMP_FOLDER, "server_" + version + ".jar");
        // Download
        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream())) {
            Files.copy(in, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Failed to download Minecraft server jar.", e);
        }
        return target;
    }
}
