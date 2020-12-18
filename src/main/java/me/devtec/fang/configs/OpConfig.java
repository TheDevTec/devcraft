package me.devtec.fang.configs;

import me.devtec.fang.data.Data;
import me.devtec.fang.data.DataType;

public class OpConfig {

    private Data data = new Data("configs/ops.yml");

    public void addOp(String name, int level){
        data.set(name, level);
        data.save(DataType.YAML);
    }

    public void removeOp(String name){
        if (data.exists(name)){
            data.remove(name);
            data.save(DataType.YAML);
        }
    }

    /*
    public boolean isOp(String name){
        boolean returnVal = false;
        if (data.exists(name)){
            returnVal = true;
        }
        return returnVal;
    }
    */

    public int getOpLevel(String name){
        return data.getInt(name);
    }

}
