package me.devtec.fang.configs;

import me.devtec.fang.data.Data;
import me.devtec.fang.data.DataType;

import java.util.Arrays;

public class ServerProperties {

    private static Data c = new Data("server-properties.yml");

    public final void Setup(){
        c.addDefault("view-distance.chunk", 6, Arrays.asList("# View distance of chunks (Defaulty 6)"));
        c.addDefault("view-distance.entity", 8, Arrays.asList("# View distance of entities for players (Defaulty 8)"));
        c.addDefault("server.ip", "localhost", Arrays.asList("# Server IP (Defaulty localhost)"));
        c.addDefault("server.port", 25565, Arrays.asList("# Server port (Defaulty 25565)"));
        c.addDefault("server.online-mode", true, Arrays.asList("# Protect the server with online-mode and prevent connections from the warez players"));
        c.addDefault("server.rate", 300, Arrays.asList("# Rate limit of server (Defaulty 300)"));
        c.addDefault("server.netty-threads", 4, Arrays.asList("# Count of Netty threads (Defaulty 4)"));
        c.addDefault("server.netty-compressions-threshold", 512, Arrays.asList("# Compression of threshold (Defaulty 512, For BungeeCord use value -1)"));
        c.addDefault("server.name", "Fang", Arrays.asList("# Brand server name"));
        c.addDefault("server.packet-maxSize", 30000, Arrays.asList("# Packet limit of server (Defaulty 30,000)"));
        c.addDefault("server.difficulty", "EASY", Arrays.asList("# Default difficulty of server"));
        c.addDefault("server.level", "world", Arrays.asList("# Name of server world"));

        c.addDefault("server.type", "SERVER", Arrays.asList("# Types: SERVER, BUNGEECORD, VELOCITY"));
        c.addDefault("server.secret", "NONE", Arrays.asList("# Server key for Velocity proxy"));
        c.addDefault("server.permission-message", "Sorry, you dont have enough permissions", Arrays.asList("# Displayed when player doesnt have enough perms"));
        c.save(DataType.YAML);
    }

    public Data get(){
        return c;
    }

}
