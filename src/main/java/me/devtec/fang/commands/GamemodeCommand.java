package me.devtec.fang.commands;

import me.devtec.fang.configs.OpConfig;
import me.devtec.fang.configs.ServerProperties;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Arguments;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;

import java.util.Optional;

/**
 * Command that make a player change gamemode
 */

public class GamemodeCommand extends Command {

    OpConfig opConfig = new OpConfig();
    ServerProperties p = new ServerProperties();

    public GamemodeCommand() {
        super("gamemode");

        setCondition(this::isAllowed);

        setDefaultExecutor(this::usage);

        Argument player = ArgumentType.Word("player");

        GameMode[] gameModes = GameMode.values();
        String[] names = new String[gameModes.length];
        for (int i = 0; i < gameModes.length; i++) {
            names[i] = gameModes[i].name().toLowerCase();
        }
        Argument mode = ArgumentType.Word("mode").from(names);

        setArgumentCallback(this::gameModeCallback, mode);

        addSyntax(this::executeOnSelf, mode);
        addSyntax(this::executeOnOther, player, mode);
    }

    private void usage(CommandSender sender, Arguments arguments) {
        sender.sendMessage("Usage: /gamemode [player] <gamemode>");
    }

    private void executeOnSelf(CommandSender sender, Arguments arguments) {
        Player player = (Player) sender;
        if (opConfig.getOpLevel(player.getUsername()) >= 3) {

            String gamemodeName = arguments.getWord("mode");
            GameMode mode = GameMode.valueOf(gamemodeName.toUpperCase());
            assert mode != null; // mode is not supposed to be null, because gamemodeName will be valid
            player.setGameMode(mode);
            player.sendMessage("You are now playing in " + gamemodeName);
        } else {
            sender.sendMessage(p.get().getString("server.permission-message"));
        }
    }

    private void executeOnOther(CommandSender sender, Arguments arguments) {
        Player player = (Player) sender;
        if (opConfig.getOpLevel(player.getUsername()) >= 3) {

            String gamemodeName = arguments.getWord("mode");
            String targetName = arguments.getWord("player");
            GameMode mode = GameMode.valueOf(gamemodeName.toUpperCase());
            assert mode != null; // mode is not supposed to be null, because gamemodeName will be valid
            Optional<Player> target = player.getInstance().getPlayers().stream().filter(p -> p.getUsername().equalsIgnoreCase(targetName)).findFirst();
            if (target.isPresent()) {
                target.get().setGameMode(mode);
                target.get().sendMessage("You are now playing in " + gamemodeName);
            } else {
                player.sendMessage("'" + targetName + "' is not a valid player name.");
            }
        } else {
            sender.sendMessage(p.get().getString("server.permission-message"));
        }
    }

    private void gameModeCallback(CommandSender sender, String gamemode, int error) {
        sender.sendMessage("'" + gamemode + "' is not a valid gamemode!");
    }

    private boolean isAllowed(CommandSender sender, String commandString) {
        if (!sender.isPlayer()) {
            sender.sendMessage("The command is only available for player");
            return false;
        }
        return true;
    }
}
