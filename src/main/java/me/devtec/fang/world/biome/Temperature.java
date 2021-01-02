package me.devtec.fang.world.biome;

import me.devtec.fang.Loader;
import me.devtec.fang.world.NoiseGen;
import me.devtec.fang.world.generators.Normal;

public class Temperature {

    /*
    static int counter = 0;
    static long sum = 0;
    static int count = 0;
    static int average;
    static double minVal;
    static double maxVal;
    */

    NoiseGen noiseGen = new NoiseGen();
    private static final int modifier = Normal.getModifier();

    public double getTemp(int X, int Z){

        //return ((noiseGen.temperatureNoise(X, Z) + 1.875) * 26.66);
        double val = noiseGen.temperatureNoise(X, Z)*170.52;

        /*
        counter++;
        sum += val;
        count++;
        average = (int) (sum/count);
        if (val > maxVal)
            maxVal = val;
        else if (val < minVal)
            minVal = val;

        if (counter >= 50){
            Loader.log("Max: " + maxVal);
            Loader.log("Min: " + minVal);
            Loader.log("Avg: " + average);
            counter = 0;
        }
        */

        return val;
    }

}
