package me.devtec.simplecraft;

import me.devtec.simplecraft.commands.Stop;
import me.devtec.simplecraft.data.Data;
import me.devtec.simplecraft.data.DataType;
import me.devtec.simplecraft.data.Ref;
import me.devtec.simplecraft.data.collections.UnsortedList;
import me.devtec.simplecraft.world.World;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.extras.optifine.OptifineSupport;
import net.minestom.server.instance.Instance;
import net.minestom.server.storage.systems.FileStorageSystem;
import net.minestom.server.utils.Position;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class Loader {
    static List<World> w = new UnsortedList<>();
    public static Logger logger = Logger.getLogger("Minecraft");

    public static void main(String[] args) {
        // Initialization

        Data c = new Data("server.properties");
        if(!c.exists("entity-view-distance"))
            c.set("entity-view-distance", 12);
        if(!c.exists("chunk-view-distance"))
            c.set("chunk-view-distance", 8);
        if(!c.exists("port"))
            c.set("port", 25565);
        if(!c.exists("ip"))
            c.set("ip", "localhost");
        if(!c.exists("online-mode"))
            c.set("online-mode", true);
        c.save(DataType.YAML);

        MinecraftServer minecraftServer = MinecraftServer.init();
        MinecraftServer.getStorageManager().defineDefaultStorageSystem(FileStorageSystem::new);
        w.add(new World("world", new Random().nextLong()));
        MinecraftServer.setChunkViewDistance(c.getInt("chunk-distance"));
        MinecraftServer.setEntityViewDistance(c.getInt("entity-distance"));
        MinecraftServer.getCommandManager().register(new Stop());
        // Add an event callback to specify the spawning instance (and the spawn position)
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addEventCallback(PlayerLoginEvent.class, event -> {
            final Player player = event.getPlayer();
            logger.info("Logging to the game "+player.getUsername());
            event.setSpawningInstance((Instance)Ref.get(getWorlds().get(0), "world"));
            player.setRespawnPoint(new Position(0, w.get(0).getHighestY(0, 0), 0));
        });
        globalEventHandler.addEventCallback(PlayerChatEvent.class, event -> {
            final Player player = event.getSender();
            logger.info(player.getUsername()+": "+event.getMessage());
        });

        // Start the server on port 25565
        minecraftServer.start(c.getString("ip"), c.getInt("port"));
        //enable optifine support
        OptifineSupport.enable();
    }

    public static List<World> getWorlds() {
        return w;
    }
}