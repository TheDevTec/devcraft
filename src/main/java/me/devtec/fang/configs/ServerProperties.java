package me.devtec.fang.configs;

import me.devtec.fang.data.Data;
import me.devtec.fang.data.DataType;

import java.util.Collections;

public class ServerProperties {

    private Data c;

    public final void Setup(){
        c = new Data("server.properties");
        c.addDefault("view-distance.chunk", 6, Collections.singletonList("# View distance of chunks (Defaulty 6)"));
        c.addDefault("view-distance.entity", 8, Collections.singletonList("# View distance of entities for players (Defaulty 8)"));
        c.addDefault("server.ip", "localhost", Collections.singletonList("# Server IP (Defaulty localhost)"));
        c.addDefault("server.port", 25565, Collections.singletonList("# Server port (Defaulty 25565)"));
        c.addDefault("server.online-mode", true, Collections.singletonList("# Protect the server with online-mode and prevent connections from the warez players"));
        c.addDefault("server.max-players", 10, Collections.singletonList("# Maximum of players on the server"));
        c.addDefault("server.motd", "Fang server", Collections.singletonList("# Server motd"));
        c.addDefault("server.rate", 300, Collections.singletonList("# Rate limit of server (Defaulty 300)"));
        c.addDefault("server.netty-threads", 4, Collections.singletonList("# Count of Netty threads (Defaulty 4)"));
        c.addDefault("server.netty-compressions-threshold", 512, Collections.singletonList("# Compression of threshold (Defaulty 512, For BungeeCord use value -1)"));
        c.addDefault("server.name", "Fang", Collections.singletonList("# Brand server name"));
        c.addDefault("server.packet-maxSize", 30000, Collections.singletonList("# Packet limit of server (Defaulty 30,000)"));
        c.addDefault("server.difficulty", "EASY", Collections.singletonList("# Default difficulty of server"));
        c.addDefault("server.level", "world", Collections.singletonList("# Name of server world"));

        c.addDefault("server.type", "SERVER", Collections.singletonList("# Types: SERVER, BUNGEECORD, VELOCITY"));
        c.addDefault("server.secret", "NONE", Collections.singletonList("# Server key for Velocity proxy"));
        c.addDefault("server.permission-message", "Sorry, you dont have enough permissions", Collections.singletonList("# Displayed when player doesnt have enough perms"));
        c.save(DataType.YAML);
    }

    public Data get(){
        return c;
    }

}
