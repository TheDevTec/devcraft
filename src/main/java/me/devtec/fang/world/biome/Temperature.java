package me.devtec.fang.world.biome;

import me.devtec.fang.world.NoiseGen;
import me.devtec.fang.world.generators.Normal;

public class Temperature {

    NoiseGen noiseGen = new NoiseGen();
    private static final int modifier = Normal.getModifier();

    public int getTemp(int X, int Z){

        double val = noiseGen.temperatureNoise(X, Z);

        int returnVal = (int)(val*100) %100;
        //Loader.log(String.valueOf(returnVal));
        return returnVal;
    }

}
