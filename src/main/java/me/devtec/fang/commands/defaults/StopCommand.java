package me.devtec.fang.commands.defaults;

import me.devtec.fang.commands.CommandExecutor;
import me.devtec.fang.commands.TabCompleter;
import me.devtec.fang.Fang;
import me.devtec.fang.world.World;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.utils.thread.MinestomThread;

import java.util.Arrays;
import java.util.Collection;

public class StopCommand extends CommandExecutor implements TabCompleter {

    public void onCommand(CommandSender sender, String cmd, String[] args) {
        sender.sendMessage("Stopping of server..");
        Fang.getWorlds().forEach(World::save);
        MinecraftServer.stopCleanly();
        MinestomThread.shutdownAll();
        System.exit(1);
    }

    @Override
    public Collection<String> onTabComplete(CommandSender sender, String[] args) {
        return Arrays.asList();
    }
}
