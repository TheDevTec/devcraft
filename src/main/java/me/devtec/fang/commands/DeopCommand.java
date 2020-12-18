package me.devtec.fang.commands;

import me.devtec.fang.configs.OpConfig;
import me.devtec.fang.configs.ServerProperties;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Arguments;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public final class DeopCommand extends Command {

    OpConfig opConfig = new OpConfig();
    ServerProperties p = new ServerProperties();

    public DeopCommand() {
        super("deop");
        //setCondition(this::isAllowed);
        //this would disallow command for console

        setDefaultExecutor(this::usage);

        List<String> players = new ArrayList<>();
        for (Player p : MinecraftServer.getConnectionManager().getOnlinePlayers()){
            players.add(p.getUsername());
        }

        Argument player = ArgumentType.Word("player");

        setArgumentCallback(this::gameModeCallback, player);

        addSyntax(this::executeOnOther, player);
        }

        private void usage(CommandSender sender, Arguments arguments) {
        sender.sendMessage("Usage: /op [player] <level>");
        }

        private void executeOnOther(CommandSender sender, Arguments arguments) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (opConfig.getOpLevel(player.getUsername()) >= 3) {

                    String targetName = arguments.getWord("player");

                    Optional<Player> target = player.getInstance().getPlayers().stream().filter(p -> p.getUsername().equalsIgnoreCase(targetName)).findFirst();
                    if (target.isPresent()) {
                        target.get().setPermissionLevel(0);
                        target.get().sendMessage("You have been deopped.");

                        opConfig.removeOp(targetName);
                    } else {
                        player.sendMessage("'" + targetName + "' is not a valid player name.");
                    }
                } else {
                    sender.sendMessage(p.get().getString("server.permission-message"));
                }
            } else {
                String targetName = arguments.getWord("player");

                Optional<Player> target = MinecraftServer.getConnectionManager().getOnlinePlayers().stream().filter(p -> p.getUsername().equalsIgnoreCase(targetName)).findFirst();
                if (target.isPresent()) {
                    target.get().setPermissionLevel(0);
                    target.get().sendMessage("You have been deopped.");

                    opConfig.removeOp(targetName);
                } else {
                    sender.sendMessage("'" + targetName + "' is not a valid player name.");
                }
            }
        }

        private void gameModeCallback(CommandSender sender, String player, int error) {
            sender.sendMessage("'" + player + "' is not a valid player!");
        }

    /*
    private boolean isAllowed(CommandSender sender, String commandString) {
        if (!sender.isPlayer()) {
            sender.sendMessage("The command is only available for player");
            return false;
        }
        return true;
    }
    */
}
