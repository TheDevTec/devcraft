package me.devtec.fang.world;

import me.devtec.fang.Loader;
import me.devtec.fang.data.collections.UnsortedList;
import me.devtec.fang.world.generators.Normal;
import net.minestom.server.instance.ChunkGenerator;
import net.minestom.server.instance.Instance;
import net.minestom.server.world.DimensionType;

import java.util.List;
import java.util.Random;

public class Fang {
    private static final List<World> worlds = new UnsortedList<>();

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
            if(w.world==instance){
                find=w;
                break;
            }
        }
        return find;
    }

    public static World createWorld(String name) {
        long seed = new Random().nextLong();
        return createWorld(name, new Normal(seed), DimensionType.OVERWORLD, seed);
    }

    public static World createWorld(String name, long seed) {
        return createWorld(name, new Normal(seed), DimensionType.OVERWORLD, seed);
    }

    public static World createWorld(String name, DimensionType dimensionType, long seed) {
        return createWorld(name, new Normal(seed), dimensionType, seed);
    }

    public static World createWorld(String name, ChunkGenerator generator, DimensionType dimensionType, long seed) {
        Loader.log("Creating world '"+name+"'..");
        World world = new World(name, generator,dimensionType, seed);
        Loader.log("World '"+name+"' created.");
        worlds.add(world);
        return world;
    }
}
