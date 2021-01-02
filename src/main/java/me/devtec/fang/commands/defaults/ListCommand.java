package me.devtec.fang.commands.defaults;

import me.devtec.fang.Fang;
import me.devtec.fang.Loader;
import me.devtec.fang.commands.CommandExecutor;
import me.devtec.fang.commands.TabCompleter;
import me.devtec.fang.utils.StringUtils;
import net.minestom.server.command.CommandSender;

import java.util.Arrays;
import java.util.Collection;

public class ListCommand extends CommandExecutor implements TabCompleter {

    @Override
    public void onCommand(CommandSender sender, String command, String[] args) {
        sender.sendMessage("There are "+ Fang.getPlayers().size()+" of a max of "+ Loader.p.get().getInt("server.max-players")+" players online: "+ StringUtils.join(Fang.getPlayerNames(), ", "));
    }

    @Override
    public Collection<String> onTabComplete(CommandSender sender, String[] args) {
        return Arrays.asList();
    }
}