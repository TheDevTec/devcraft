package me.devtec.fang;

import me.devtec.fang.commands.Stop;
import me.devtec.fang.data.Data;
import me.devtec.fang.data.DataType;
import me.devtec.fang.data.Ref;
import me.devtec.fang.data.collections.UnsortedList;
import me.devtec.fang.world.World;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.extras.bungee.BungeeCordProxy;
import net.minestom.server.extras.optifine.OptifineSupport;
import net.minestom.server.extras.velocity.VelocityProxy;
import net.minestom.server.instance.Instance;
import net.minestom.server.storage.systems.FileStorageSystem;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.utils.Position;
import net.minestom.server.world.Difficulty;
import net.minestom.server.world.DimensionType;

import java.text.SimpleDateFormat;
import java.util.*;

public class Loader {
    static List<World> w = new UnsortedList<>();

    public static void log(String text) {
        System.out.println("[Fang] [" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + text);
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        Data c = new Data("server-properties.yml");
        c.addDefault("view-distance.chunk", 6, Arrays.asList("# View distance of chunks (Defaulty 6)"));
        c.addDefault("view-distance.entity", 8, Arrays.asList("# View distance of entities for players (Defaulty 8)"));
        c.addDefault("server.ip", "localhost", Arrays.asList("# Server IP (Defaulty localhost)"));
        c.addDefault("server.port", 25565, Arrays.asList("# Server port (Defaulty 25565)"));
        c.addDefault("server.online-mode", true, Arrays.asList("# Protect the server with online-mode and prevent connections from the warez players"));
        c.addDefault("server.rate", 300, Arrays.asList("# Rate limit of server (Defaulty 300)"));
        c.addDefault("server.netty-threads", 4, Arrays.asList("# Count of Netty threads (Defaulty 4)"));
        c.addDefault("server.netty-compressions-threshold", 512, Arrays.asList("# Compression of threshold (Defaulty 512, For BungeeCord use value -1)"));
        c.addDefault("server.name", "Fang", Arrays.asList("# Brand server name"));
        c.addDefault("server.packet-maxSize", 30000, Arrays.asList("# Packet limit of server (Defaulty 30,000)"));
        c.addDefault("server.difficulty", "EASY", Arrays.asList("# Default difficulty of server"));
        c.addDefault("server.level", "world", Arrays.asList("# Name of server world"));

        c.addDefault("server.type", "SERVER", Arrays.asList("# Types: SERVER, BUNGEECORD, VELOCITY"));
        c.addDefault("server.secret", "NONE", Arrays.asList("# Server key for Velocity proxy"));
        c.save(DataType.YAML);

        //LOAD CONFIGS

        if (c.getBoolean("server.online-mode")) {
            net.minestom.server.extras.MojangAuth.init();
        }
        MinecraftServer minecraftServer = MinecraftServer.init();
        MinecraftServer.getStorageManager().defineDefaultStorageSystem(FileStorageSystem::new);

        MinecraftServer.setRateLimit(c.getInt("server.rate"));
        MinecraftServer.setChunkViewDistance(c.getInt("view-distance.chunk"));
        MinecraftServer.setEntityViewDistance(c.getInt("view-distance.entity"));
        MinecraftServer.setNettyThreadCount(c.getInt("server.netty-threads"));
        MinecraftServer.setCompressionThreshold(c.getInt("server.netty-compressions-threshold"));
        MinecraftServer.setBrandName(c.getString("server.name"));
        MinecraftServer.setMaxPacketSize(c.getInt("server.packet-maxSize"));
        MinecraftServer.setDifficulty(Difficulty.valueOf(c.getString("server.difficulty").toUpperCase()));
        w.add(new World(c.getString("server.level"), DimensionType.OVERWORLD, new Random().nextLong()));
        log("Loaded world '" + c.getString("server.level") + "'");
        w.add(new World(c.getString("server.level") + "_nether", DimensionType.builder(NamespaceID.from("minecraft:nether"))
                .ultrawarm(false)
                .natural(true)
                .piglinSafe(true)
                .respawnAnchorSafe(true)
                .bedSafe(false)
                .raidCapable(false)
                .skylightEnabled(true)
                .ceilingEnabled(false)
                .fixedTime(Optional.empty())
                .ambientLight(0.0f)
                .logicalHeight(256)
                .build(), new Random().nextLong()));
        log("Loaded world '" + c.getString("server.level") + "_nether'");
        w.add(new World(c.getString("server.level") + "_the_end", DimensionType.builder(NamespaceID.from("minecraft:the_end"))
                .ultrawarm(false)
                .natural(true)
                .piglinSafe(false)
                .respawnAnchorSafe(false)
                .bedSafe(false)
                .raidCapable(false)
                .skylightEnabled(false)
                .ceilingEnabled(false)
                .fixedTime(Optional.empty())
                .ambientLight(0.0f)
                .logicalHeight(256)
                .build(), new Random().nextLong()));
        log("Loaded world '" + c.getString("server.level") + "_the_end'");

        //LOAD COMMANDS
        MinecraftServer.getCommandManager().register(new Stop());
        log("Registered commands.");

        //HOOK LOGGER
        GlobalEventHandler events = MinecraftServer.getGlobalEventHandler();
        events.addEventCallback(PlayerLoginEvent.class, event -> {
            final Player player = event.getPlayer();
            log(player.getUsername() + " joined the game.");
            event.setSpawningInstance((Instance) Ref.get(getWorlds().get(0), "world"));
            player.setRespawnPoint(new Position(0, 64, 0));
        });
        events.addEventCallback(PlayerDisconnectEvent.class, event -> {
            log(event.getPlayer().getUsername() + " disconnected from the game.");
        });
        events.addEventCallback(PlayerChatEvent.class, event -> {
            log(event.getSender().getUsername() + ": " + event.getMessage());
        });
        log("Registered events.");

        OptifineSupport.enable();
        switch (c.getString("server.type").toUpperCase()) {
            case "BUNGEE":
            case "BUNGEECORD":
                BungeeCordProxy.enable();
                break;
            case "VELOCITY":
                VelocityProxy.enable(c.getString("server.secret"));
                break;
            default:
                break;
        }

        minecraftServer.start(c.getString("server.ip"), c.getInt("server.port"));
        log("Server loaded in " + (System.currentTimeMillis() - start) + "ms");
    }

    public static List<World> getWorlds() {
        return w;
    }
}
