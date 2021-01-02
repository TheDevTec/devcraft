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


public class OpCommand extends CommandExecutor implements TabCompleter {

    @Override
    public void onCommand(CommandSender sender, String command, String[] args) {
        if(args.length==0){
            sender.sendMessage("/op <player>");
            return;
        }
        OfflinePlayer target = Fang.getOfflinePlayer(args[0]);
        if(target.isOpped()){
            sender.sendMessage(ChatColor.DARK_RED+"Nothing changed. The player already is an operator");
            return;
        }
        sender.sendMessage("Made "+args[0]+" a server operator");
        target.setOp(true);
    }

    @Override
    public Collection<String> onTabComplete(CommandSender sender, String[] args) {
        if(args.length==1){
            Set<String> s = new UnsortedSet<>();
            Fang.getPlayers().forEach(e -> {
                if(!Fang.isOpped(e.getUsername()))s.add(e.getUsername());
            });
            return s;
        }
        return Arrays.asList();
    }
}
