package me.devtec.fang;

import me.devtec.fang.commands.DeopCommand;
import me.devtec.fang.commands.GamemodeCommand;
import me.devtec.fang.commands.OpCommand;
import me.devtec.fang.commands.StopCommand;
import me.devtec.fang.configs.ServerProperties;
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

        ServerProperties p = new ServerProperties();
        p.Setup();
        //LOAD CONFIGS

        if (p.get().getBoolean("server.online-mode")) {
            net.minestom.server.extras.MojangAuth.init();
        }
        MinecraftServer minecraftServer = MinecraftServer.init();
        MinecraftServer.getStorageManager().defineDefaultStorageSystem(FileStorageSystem::new);

        MinecraftServer.setRateLimit(p.get().getInt("server.rate"));
        MinecraftServer.setChunkViewDistance(p.get().getInt("view-distance.chunk"));
        MinecraftServer.setEntityViewDistance(p.get().getInt("view-distance.entity"));
        MinecraftServer.setNettyThreadCount(p.get().getInt("server.netty-threads"));
        MinecraftServer.setCompressionThreshold(p.get().getInt("server.netty-compressions-threshold"));
        MinecraftServer.setBrandName(p.get().getString("server.name"));
        MinecraftServer.setMaxPacketSize(p.get().getInt("server.packet-maxSize"));
        MinecraftServer.setDifficulty(Difficulty.valueOf(p.get().getString("server.difficulty").toUpperCase()));
        w.add(new World(p.get().getString("server.level"), DimensionType.OVERWORLD, new Random().nextLong()));
        log("Loaded world '" + p.get().getString("server.level") + "'");
        w.add(new World(p.get().getString("server.level") + "_nether", DimensionType.builder(NamespaceID.from("minecraft:nether"))
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
        log("Loaded world '" + p.get().getString("server.level") + "_nether'");
        w.add(new World(p.get().getString("server.level") + "_the_end", DimensionType.builder(NamespaceID.from("minecraft:the_end"))
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
        log("Loaded world '" + p.get().getString("server.level") + "_the_end'");

        //LOAD COMMANDS
        MinecraftServer.getCommandManager().register(new StopCommand());
        MinecraftServer.getCommandManager().register(new OpCommand());
        MinecraftServer.getCommandManager().register(new DeopCommand());
        MinecraftServer.getCommandManager().register(new GamemodeCommand());
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
        switch (p.get().getString("server.type").toUpperCase()) {
            case "BUNGEE":
            case "BUNGEECORD":
                BungeeCordProxy.enable();
                break;
            case "VELOCITY":
                VelocityProxy.enable(p.get().getString("server.secret"));
                break;
            default:
                break;
        }

        minecraftServer.start(p.get().getString("server.ip"), p.get().getInt("server.port"));
        log("Server loaded in " + (System.currentTimeMillis() - start) + "ms");
    }

    public static List<World> getWorlds() {
        return w;
    }
}
