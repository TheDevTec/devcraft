package me.devtec.fang.world.biome;

import me.devtec.fang.world.NoiseGen;
import me.devtec.fang.world.generators.Normal;

public class Temperature {

    NoiseGen noiseGen = new NoiseGen();
    private static final int modifier = Normal.getModifier();

    public double getTemp(int X, int Z){

        //return ((noiseGen.temperatureNoise(X, Z) + 1.875) * 26.66);
        double val = noiseGen.temperatureNoise(X, Z)*170.52;
        return val;
    }

}
