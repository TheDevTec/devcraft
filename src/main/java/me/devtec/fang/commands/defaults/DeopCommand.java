package me.devtec.fang.commands.defaults;

import me.devtec.fang.Fang;
import me.devtec.fang.commands.CommandExecutor;
import me.devtec.fang.commands.TabCompleter;
import me.devtec.fang.data.collections.UnsortedSet;
import me.devtec.fang.player.OfflinePlayer;
import net.minestom.server.chat.ChatColor;
import net.minestom.server.command.CommandSender;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;


public class DeopCommand extends CommandExecutor implements TabCompleter {

    @Override
    public void onCommand(CommandSender sender, String command, String[] args) {
        if(args.length==0){
            sender.sendMessage("/deop <player>");
            return;
        }
        OfflinePlayer target = Fang.getOfflinePlayer(args[0]);
        if(!target.isOpped()){
            sender.sendMessage(ChatColor.DARK_RED+"Nothing changed. The player is not an operator");
            return;
        }
        sender.sendMessage("Made "+args[0]+" not longer a server operator");
        target.setOp(false);
    }

    @Override
    public Collection<String> onTabComplete(CommandSender sender, String[] args) {
        if(args.length==1){
            Set<String> players = new UnsortedSet<>();
            Fang.getOperators().forEach(a -> players.add(a.getName()==null?a.getUuid().toString():a.getName()));
            return players;
        }
        return Arrays.asList();
    }
}

