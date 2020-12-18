package me.devtec.fang.commands;

import me.devtec.fang.world.Fang;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Arguments;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandExecutor;
import net.minestom.server.utils.thread.MinestomThread;

public class StopCommand extends Command {

    public StopCommand() {
        super("stop");
        setDefaultExecutor(new CommandExecutor() {
            @Override
            public void apply(CommandSender sender, Arguments args) {
                sender.sendMessage("Stopping of server..");
                Fang.getWorlds().forEach(world -> world.save());
                MinecraftServer.stopCleanly();
                MinestomThread.shutdownAll();
                System.exit(1);
            }
        });
    }
}
