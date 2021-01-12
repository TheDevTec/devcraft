package me.devtec.fang.commands;

import me.devtec.fang.Fang;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandProcessor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public abstract class CommandExecutor implements CommandProcessor {
    protected String name, perm;
    protected String[] alias;
    protected TabCompleter tab;

    public void initialize(PluginCommand c){
        name=c.getName();
        alias=c.getAliases();
        tab=c.getTabCompleter()==null?(c.getCommandExecutor()instanceof TabCompleter?((TabCompleter) c.getCommandExecutor()):null):c.getTabCompleter();
    }

    public abstract void onCommand(CommandSender sender, String command, String[] args);

    public final boolean enableWritingTracking(){
        return true;
    }

    @NotNull
    @Override
    public String getCommandName() {
        return name;
    }

    @Nullable
    @Override
    public String[] getAliases() {
        return alias;
    }

    @Override
    public boolean process(@NotNull CommandSender sender, @NotNull String command, @NotNull String[] args) {
        onCommand(sender, command, args);
        return true;
    }

    @Override
    public boolean hasAccess(@NotNull Player player) {
        return (perm==null||perm.trim().isEmpty())?true:(player.hasPermission(perm)||Fang.isOpped(player.getUuid()));
    }

    @Nullable
    @Override
    public String[] onWrite(@NotNull CommandSender sender, String text) {
        if(tab==null)return Fang.getPlayerNames().toArray(new String[Fang.getPlayerNames().size()]);
        Collection<String> tt = tab.onTabComplete(sender, text.split(" "));
        return tt.toArray(new String[tt.size()]);
    }

    @Nullable
    public String[] onWrite(String text) {
        if(tab==null)return Fang.getPlayerNames().toArray(new String[Fang.getPlayerNames().size()]);
        Collection<String> tt = tab.onTabComplete(MinecraftServer.getCommandManager().getConsoleSender(), text.split(" "));
        return tt.toArray(new String[tt.size()]);
    }
}
