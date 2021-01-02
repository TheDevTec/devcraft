package me.devtec.fang;

import com.google.common.base.Charsets;
import me.devtec.fang.commands.PluginCommand;
import me.devtec.fang.commands.defaults.*;
import me.devtec.fang.configs.ServerProperties;
import me.devtec.fang.data.Data;
import me.devtec.fang.data.DataType;
import me.devtec.fang.data.Ref;
import me.devtec.fang.world.biome.BiomeProperties;
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
import net.minestom.server.utils.Position;
import net.minestom.server.world.Difficulty;
import net.minestom.server.world.DimensionType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class Loader {
    public static Data opConfig = new Data("configs/op.json");
    public static ServerProperties p = new ServerProperties();

    public static void log(String text) {
        System.out.println("[Fang] [" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + text);
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

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
        Fang.createWorld(p.get().getString("server.level"), DimensionType.OVERWORLD, new Random().nextLong());
        BiomeProperties.registerAllBiomes();
        /*
        Fang.createWorld(p.get().getString("server.level") + "_nether", DimensionType.builder(NamespaceID.from("minecraft:nether"))
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
                .build(), new Random().nextLong());
        Fang.createWorld(p.get().getString("server.level") + "_the_end", DimensionType.builder(NamespaceID.from("minecraft:the_end"))
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
                .build(), new Random().nextLong());
        */

        //HOOK LOGGER
        GlobalEventHandler events = MinecraftServer.getGlobalEventHandler();
        events.addEventCallback(PlayerLoginEvent.class, event -> {
            final Player player = event.getPlayer();
            log(player.getUsername() + " joined the game.");
            event.setSpawningInstance((Instance) Ref.get(Fang.getWorld("world"), "world"));
            setOfflineUUID(player.getUsername(), player.getUuid());
            player.setRespawnPoint(new Position(0, 100, 0));
        });
        events.addEventCallback(PlayerDisconnectEvent.class, event -> {
            log(event.getPlayer().getUsername() + " disconnected from the game.");
        });


        events.addEventCallback(PlayerChatEvent.class, event -> {
            log(event.getPlayer().getUsername() + ": " + event.getMessage());
        });



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

        //LOAD COMMANDS
        PluginCommand command = new PluginCommand("stop");
        command.setPermission("minecraft.stop");
        command.setCommandExecutor(new StopCommand());
        Fang.registerCommand(command);

        command = new PluginCommand("op");
        command.setPermission("minecraft.op");
        command.setCommandExecutor(new OpCommand());
        Fang.registerCommand(command);

        command = new PluginCommand("deop");
        command.setPermission("minecraft.deop");
        command.setCommandExecutor(new DeopCommand());
        Fang.registerCommand(command);

        command = new PluginCommand("gamemode");
        command.setPermission("minecraft.gamemode");
        command.setCommandExecutor(new GamemodeCommand());
        Fang.registerCommand(command);

        command = new PluginCommand("seed");
        command.setPermission("minecraft.seed");
        command.setCommandExecutor(new SeedCommand());
        Fang.registerCommand(command);

        command = new PluginCommand("list");
        command.setPermission("minecraft.list");
        command.setCommandExecutor(new ListCommand());
        Fang.registerCommand(command);

        command = new PluginCommand("teleport");
        command.setPermission("minecraft.teleport");
        command.setCommandExecutor(new TeleportCommand());
        Fang.registerCommand(command);

        MinecraftServer.getCommandManager().setUnknownCommandCallback((sender, cc) -> sender.sendMessage("Uknown command. Type '/help' for help."));
    }

    public static Data base = new Data("UserCache.dat");

    public static UUID getOfflineUUID(String name) {
        if(!base.exists("name."+name.toLowerCase()))
            setOfflineUUID(name, UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8)));
        return UUID.fromString(base.getString("name."+name.toLowerCase()));
    }

    public static String getOfflineName(UUID uuid) {
        return base.getString("uuid."+uuid.toString());
    }

    public static void setOfflineUUID(String name, UUID uuid) {
        base.set("name."+name.toLowerCase(), uuid.toString());
        base.set("uuid."+uuid.toString(), name.toLowerCase());
    }

    public static void savePlayers() {
        base.save(DataType.BYTE);
    }
}
