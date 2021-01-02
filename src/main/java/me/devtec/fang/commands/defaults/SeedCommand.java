package me.devtec.fang.commands.defaults;

import me.devtec.fang.Fang;
import me.devtec.fang.commands.CommandExecutor;
import me.devtec.fang.commands.TabCompleter;
import net.minestom.server.chat.ChatColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.entity.Player;

import java.util.Arrays;
import java.util.Collection;

public class SeedCommand extends CommandExecutor implements TabCompleter {
    @Override
    public void onCommand(CommandSender sender, String command, String[] args) {
        sender.sendMessage("Seed: ["+ ChatColor.BRIGHT_GREEN+Fang.getWorld(((Player)sender).getInstance()).getSeed()+ChatColor.WHITE+"]");
    }

    @Override
    public Collection<String> onTabComplete(CommandSender sender, String[] args) {
        return Arrays.asList();
    }
}
