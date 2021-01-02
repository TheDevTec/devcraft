package me.devtec.fang.player;

import me.devtec.fang.Loader;
import me.devtec.fang.data.Data;
import me.devtec.fang.data.DataType;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;

import java.util.UUID;

public class OfflinePlayer {

    private final String name;
    private final UUID uuid;
    public OfflinePlayer(String name){
        UUID u;
        String n;
        try{
            u=UUID.fromString(name);
            n= Loader.getOfflineName(u);
        }catch(Exception er) {
            n = name;
            u = Loader.getOfflineUUID(n);
        }
        uuid=u;
        this.name=n;
    }

    public OfflinePlayer(UUID uuid){
        this.uuid=uuid;
        name= Loader.getOfflineName(uuid);
    }

    public UUID getUuid(){
        return uuid;
    }

    public boolean isOnline(){
        return getPlayer()!=null;
    }

    public String getName(){
        return name;
    }

    public Player getPlayer(){
        return MinecraftServer.getConnectionManager().getPlayer(uuid);
    }

    private Data d;
    public Data getData(){
        if(d==null)d=new Data("PlayerData/"+uuid+".dat");;
        return d;
    }

    public void save(){
        if(d!=null)d.save(DataType.BYTE);
    }

    public boolean isOpped() {
        return Loader.opConfig.getBoolean(uuid+"");
    }

    public void setOp(boolean op) {
        if(op)
            Loader.opConfig.set(uuid+"", true);
        else
            Loader.opConfig.remove(uuid+"");
        Loader.opConfig.save(DataType.JSON);
    }
}
