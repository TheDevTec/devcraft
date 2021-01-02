package me.devtec.fang.commands;

import net.minestom.server.command.CommandSender;

import java.util.Collection;

public interface TabCompleter {
    public Collection<String> onTabComplete(CommandSender sender, String[] args);
}
