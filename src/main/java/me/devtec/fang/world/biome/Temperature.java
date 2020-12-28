package me.devtec.fang.world.biome;

import me.devtec.fang.world.NoiseGen;

public class Temperature {



    NoiseGen noiseGen = new NoiseGen();

    public int getTemp(int ChunkX, int ChunkZ){

        double val = noiseGen.temperatureNoise(ChunkX, ChunkZ);
        val = (val+1)/2;

        return (int)(val*100);


    }

}
