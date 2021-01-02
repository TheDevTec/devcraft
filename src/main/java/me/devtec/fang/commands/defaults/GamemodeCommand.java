package me.devtec.fang.commands.defaults;

import me.devtec.fang.commands.CommandExecutor;
import me.devtec.fang.commands.TabCompleter;
import me.devtec.fang.utils.StringUtils;
import net.minestom.server.command.CommandSender;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;

import java.util.Arrays;
import java.util.Collection;

public class GamemodeCommand extends CommandExecutor implements TabCompleter {

    @Override
    public void onCommand(CommandSender sender, String command, String[] args) {
        if(args.length==0) {
            sender.sendMessage("/Gamemode <gamemode> <player>");
            return;
        }
        GameMode mode = GameMode.valueOf(args[0].toUpperCase());
        if(mode==null){
            sender.sendMessage("/Gamemode <gamemode> <player>");
            return;
        }
        ((Player)sender).setGameMode(mode);
        sender.sendMessage("Set own game mode to "+(mode==GameMode.ADVENTURE?"Adventure":(mode==GameMode.SURVIVAL?"Survival":mode==GameMode.CREATIVE?"Creative":"Spectator"))+" Mode");
        return;
    }

    @Override
    public Collection<String> onTabComplete(CommandSender sender, String[] args) {
        if(args.length==1)
            return StringUtils.copyPartialMatches(args[0], Arrays.asList("Survival", "Creative", "Adventure", "Spectator"));
        if(args.length==2)
            return null;
        return Arrays.asList(); //empty list
    }
}
