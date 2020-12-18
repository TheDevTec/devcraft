package me.devtec.fang.commands;

import me.devtec.fang.Loader;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Arguments;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandExecutor;
import net.minestom.server.storage.StorageLocation;
import net.minestom.server.utils.cache.TemporaryCache;
import net.minestom.server.utils.thread.MinestomThread;

public class StopCommand extends Command {

    public StopCommand() {
        super("stop");
        setDefaultExecutor(new CommandExecutor() {
            @Override
            public void apply(CommandSender sender, Arguments args) {

                sender.sendMessage("Stopping of server..");
                Loader.getWorlds().forEach(world -> world.save());
                /*
                MinecraftServer.getUpdateManager().stop();
                MinecraftServer.getSchedulerManager().shutdown();
                MinecraftServer.getConnectionManager().shutdown();
                MinecraftServer.getNettyServer().stop();
                MinecraftServer.getStorageManager().getLoadedLocations().forEach(StorageLocation::close);
                MinecraftServer.getExtensionManager().shutdown();
                MinecraftServer.getBenchmarkManager().disable();
                MinecraftServer.getCommandManager().stopConsoleThread();
                TemporaryCache.REMOVER_SERVICE.shutdown();
                */
                MinecraftServer.stopCleanly();
                MinestomThread.shutdownAll();
                System.exit(1);
            }
        });
    }
}
