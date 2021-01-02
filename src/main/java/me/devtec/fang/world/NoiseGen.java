package me.devtec.fang.world;

import de.articdive.jnoise.JNoise;
import me.devtec.fang.world.noise.FastNoiseLite;

public class NoiseGen {

    public void SetupNoises(int seed){
        Seed = seed;

        abnormal = new FastNoiseLite();
        abnormalHill = new FastNoiseLite();
        cellular = new FastNoiseLite();

        abnormal.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
        abnormalHill.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
        cellular.SetNoiseType(FastNoiseLite.NoiseType.Cellular);

        abnormal.SetSeed(seed);
        abnormalHill.SetSeed(seed + 1);
        cellular.SetSeed(seed + 2);

        HeightNoiseOne = JNoise.newBuilder().openSimplex().setFrequency(0.01).setSeed(seed).build();
        HeightNoiseTwo = JNoise.newBuilder().openSimplex().setFrequency(0.02).setSeed(seed * 2).build();
        HeightNoiseThree = JNoise.newBuilder().openSimplex().setFrequency(0.04).setSeed(seed * 4).build();
        HeightNoiseFour = JNoise.newBuilder().openSimplex().setFrequency(0.08).setSeed(seed * 8).build();

        StabilizerNoiseOne = JNoise.newBuilder().openSimplex().setFrequency(0.0008).setSeed((seed*872758)/386).build();
        StabilizerNoiseTwo = JNoise.newBuilder().openSimplex().setFrequency(0.0016).setSeed((seed*546565)/225).build();
    }
    private static int Seed;
    public static FastNoiseLite abnormal, abnormalHill, cellular;
    private static JNoise HeightNoiseOne, HeightNoiseTwo, HeightNoiseThree, HeightNoiseFour, StabilizerNoiseOne, StabilizerNoiseTwo;

    public double defaultNoise(int x, int z){
        FastNoiseLite freqAbnormal = abnormal;
        freqAbnormal.SetFrequency((float)(0.0008));
        return freqAbnormal.GetNoise(x, z);
    }

    public JNoise HeightNoiseOne(){
        return HeightNoiseOne;
    }
    public JNoise HeightNoiseTwo(){
        return HeightNoiseTwo;
    }
    public JNoise HeightNoiseThree(){
        return HeightNoiseThree;
    }
    public JNoise HeightNoiseFour(){
        return HeightNoiseFour;
    }
    public JNoise StabilizerNoiseOne(){
        return StabilizerNoiseOne;
    }
    public JNoise StabilizerNoiseTwo(){
        return StabilizerNoiseTwo;
    }

    public double getStabilizerNoiseOne(int X, int Z){
        double noise = StabilizerNoiseOne().getNoise(X, Z);
        return noise;
    }
    public double getStabilizerNoiseTwo(int X, int Z){
        double noise = StabilizerNoiseTwo().getNoise(X, Z);
        return noise;
    }

    public int getY(int X, int Z) {
        double ratio = 1.5 + getStabilizerNoiseTwo(X, Z) - 0.5*((temperatureNoise(X, Z)+1)/2);
        int noise = (int) (64 + (HeightNoiseOne().getNoise(X, Z) * 32) + (HeightNoiseTwo().getNoise(X, Z) * 16) + (HeightNoiseThree().getNoise(X, Z) * 8) + (HeightNoiseFour().getNoise(X, Z) * 4));
        //noise *= getNoise.defaultNoise(X, Z)*ratio;
        noise *= ((getStabilizerNoiseOne(X, Z)+1)/3.5)*ratio;
        return noise;
    }

    public double getTreeNoise(int x, int z, float frequency){
        FastNoiseLite freqAbnormal = abnormal;
        freqAbnormal.SetFrequency(frequency);

        double noise = freqAbnormal.GetNoise(x, z);
        noise = (noise + defaultNoise(x, z))/2;

        return noise;
    }

    public double temperatureNoise(int x, int z){
        float modifier = 0.5f;
        FastNoiseLite freqCellular = cellular;
        freqCellular.SetFrequency(0.001f*modifier);

        double noiseLayer =  (freqCellular.GetNoise(x, z)+1)/2; /*freqAbnormal.GetNoise(x, z);*/

        cellular.SetFrequency(0.002f*modifier);
        cellular.SetSeed((Seed*564158)/638);
        noiseLayer += ((cellular.GetNoise(x, z)+1)/2)*0.5;

        cellular.SetFrequency(0.004f*modifier);
        cellular.SetSeed((Seed*185144)/158);
        noiseLayer += ((cellular.GetNoise(x, z)+1)/2)*0.25;

        cellular.SetFrequency(0.008f*modifier);
        cellular.SetSeed((Seed*451582)/274);
        noiseLayer += ((cellular.GetNoise(x, z)+1)/2)*0.125;

        return noiseLayer;
    }

    public double customPositiveNoise(int x, int z, float frequency){
        return (customNoiseOne(x, z, frequency) + 1) / 2;
    }

    public double customPositiveNoise(int x, int z, float frequency, int chooseNoise){
        if (chooseNoise == 1) {
            return (customNoiseOne(x, z, frequency) + 1) / 2;
        } else if (chooseNoise == 2){
            return (customNoiseTwo(x, z, frequency) + 1) / 2;
        } else if (chooseNoise == 3){
            return (customNoiseThree(x, z, frequency) + 1) / 2;
        } else if (chooseNoise == 4){
            return (customNoiseFour(x, z, frequency) + 1) / 2;
        } else {
            return (customNoiseFive(x, z, frequency) + 1) / 2;
        }
    }

    private double customNoiseOne(int x, int z, float frequency){
        FastNoiseLite freqAbnormal = abnormal;
        freqAbnormal.SetSeed((Seed*573048)/630);
        freqAbnormal.SetFrequency(frequency);
        return freqAbnormal.GetNoise(x, z);
    }

    private double customNoiseTwo(int x, int z, float frequency){
        FastNoiseLite freqAbnormal = abnormal;
        freqAbnormal.SetSeed((Seed*384543)/229);
        freqAbnormal.SetFrequency(frequency);
        return freqAbnormal.GetNoise(x, z);
    }

    private double customNoiseThree(int x, int z, float frequency){
        FastNoiseLite freqAbnormal = abnormal;
        freqAbnormal.SetSeed((Seed*604735)/808);
        freqAbnormal.SetFrequency(frequency);
        return freqAbnormal.GetNoise(x, z);
    }

    private double customNoiseFour(int x, int z, float frequency){
        FastNoiseLite freqAbnormal = abnormal;
        freqAbnormal.SetSeed((Seed*930342)/824);
        freqAbnormal.SetFrequency(frequency);
        return freqAbnormal.GetNoise(x, z);
    }

    private double customNoiseFive(int x, int z, float frequency){
        FastNoiseLite freqAbnormal = abnormal;
        freqAbnormal.SetSeed((Seed*691667)/237);
        freqAbnormal.SetFrequency(frequency);
        return freqAbnormal.GetNoise(x, z);
    }

    /*
    public FastNoiseLite get(){
        return abnormal;
    }
    */

}