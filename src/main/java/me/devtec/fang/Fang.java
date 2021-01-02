package me.devtec.fang;

import me.devtec.fang.commands.CommandExecutor;
import me.devtec.fang.commands.PluginCommand;
import me.devtec.fang.data.Ref;
import me.devtec.fang.data.collections.UnsortedList;
import me.devtec.fang.data.collections.UnsortedSet;
import me.devtec.fang.data.maps.UnsortedMap;
import me.devtec.fang.player.OfflinePlayer;
import me.devtec.fang.world.World;
import me.devtec.fang.world.generators.Normal;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.ChunkGenerator;
import net.minestom.server.instance.Instance;
import net.minestom.server.world.DimensionType;

import java.util.*;

public class Fang {

    private static final List<World> worlds = new UnsortedList<>();
    private static final Map<String, PluginCommand> commands = new UnsortedMap<>();

    public static boolean isOpped(String name){
        return getOfflinePlayer(name).isOpped();
    }

    public static boolean isOpped(UUID name){
        return getOfflinePlayer(name).isOpped();
    }

    public static void setOp(String name, boolean op){
        getOfflinePlayer(name).setOp(op);
    }

    public static void setOp(UUID name, boolean op){
        getOfflinePlayer(name).setOp(op);
    }

    public static OfflinePlayer getOfflinePlayer(String name){
        return new OfflinePlayer(name);
    }

    public static OfflinePlayer getOfflinePlayer(UUID uuid){
        return new OfflinePlayer(uuid);
    }

    public static List<World> getWorlds(){
        return new UnsortedList<>(worlds);
    }

    public static World getWorld(String name){
        World find = null;
        for(World w : worlds){
            if(w.getName().equals(name)){
                find=w;
                break;
            }
        }
        return find;
    }

    public static World getWorld(Instance instance){
        World find = null;
        for(World w : worlds){
            if(Ref.get(w,"world").equals(instance)){
                find=w;
                break;
            }
        }
        return find;
    }

    public static World createWorld(String name) {
        long seed = new Random().nextLong();
        return createWorld(name, new Normal(), DimensionType.OVERWORLD, seed);
    }

    public static World createWorld(String name, long seed) {
        return createWorld(name, new Normal(), DimensionType.OVERWORLD, seed);
    }

    public static World createWorld(String name, DimensionType dimensionType, long seed) {
        return createWorld(name, new Normal(), dimensionType, seed);
    }

    public static World createWorld(String name, ChunkGenerator generator, DimensionType dimensionType, long seed) {
        Loader.log("Creating world '"+name+"'..");
        World world = new World(name, generator,dimensionType, seed);
        Loader.log("World '"+name+"' created.");
        worlds.add(world);
        return world;
    }

    public static Collection<Player> getPlayers() {
        return MinecraftServer.getConnectionManager().getOnlinePlayers();
    }

    public static Collection<String> getPlayerNames() {
        Set<String> names = new UnsortedSet<>();
        MinecraftServer.getConnectionManager().getOnlinePlayers().forEach(a -> names.add(a.getUsername()));
        return names;
    }

    public static void registerCommand(PluginCommand command) {
        commands.put(command.getName(), command);
        CommandExecutor ex = command.getCommandExecutor();
        if(ex==null){
            if(command.getTabCompleter()!=null && command.getTabCompleter() instanceof CommandExecutor){
                command.setCommandExecutor((CommandExecutor) command.getTabCompleter());
                ex=command.getCommandExecutor();
            }else return; //command executor cannot be null
        }
        ex.initialize(command);
        MinecraftServer.getCommandManager().register(ex);
    }

    public static void unregisterCommand(PluginCommand command) {
        commands.remove(command.getName());
        Map<?, ?> ref = (Map<?, ?>) Ref.get(MinecraftServer.getCommandManager(), "commandProcessorMap");
        ref.remove(command.getName());
        for (String alias : command.getAliases())ref.remove(alias);
    }

    public static PluginCommand getCommand(String name){
        return commands.get(name);
    }

    public static void unregisterCommand(String command) {
        unregisterCommand(getCommand(command));
    }

    public static Collection<OfflinePlayer> getOperators() {
        Set<OfflinePlayer> players = new UnsortedSet<>();
        for(String u : Loader.opConfig.getKeys())players.add(getOfflinePlayer(UUID.fromString(u)));
        return players;
    }
}
