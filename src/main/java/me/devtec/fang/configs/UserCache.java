package me.devtec.fang.configs;

import me.devtec.fang.data.Data;
import me.devtec.fang.data.DataType;
import me.devtec.fang.data.maps.UnsortedMap;

import java.util.Map;
import java.util.UUID;

public class UserCache {
    private final Map<UUID, Data> cache = new UnsortedMap<>();
    public void save(){
        for(Data d : cache.values())d.save(DataType.BYTE);
    }

    public void clear(){
        save();
        cache.clear();
    }

    public Data getUser(UUID name){
        Data load = cache.getOrDefault(name, null);
        if(load==null){
            load=new Data("configs/Users/"+name+".dat");
            cache.put(name, load);
        }
        return load;
    }
}
