package me.devtec.fang.world;

import de.articdive.jnoise.JNoise;
import me.devtec.fang.world.noise.FastNoiseLite;

public class NoiseGen {

    public void SetupNoises(int seed){
        Seed = seed;

        abnormal = new FastNoiseLite();
        abnormalHill = new FastNoiseLite();

        abnormal.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
        abnormalHill.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);

        abnormal.SetSeed(seed);
        abnormalHill.SetSeed(seed + 1);

        HeightNoiseOne = JNoise.newBuilder().openSimplex().setFrequency(0.01).setSeed(seed).build();
        HeightNoiseTwo = JNoise.newBuilder().openSimplex().setFrequency(0.02).setSeed(seed * 2).build();
        HeightNoiseThree = JNoise.newBuilder().openSimplex().setFrequency(0.04).setSeed(seed * 4).build();
        HeightNoiseFour = JNoise.newBuilder().openSimplex().setFrequency(0.08).setSeed(seed * 8).build();

        StabilizerNoiseOne = JNoise.newBuilder().openSimplex().setFrequency(0.0008).setSeed(seed).build();
        StabilizerNoiseTwo = JNoise.newBuilder().openSimplex().setFrequency(0.0016).setSeed((seed*546565)/225).build();
    }
    private static int Seed;
    public static FastNoiseLite abnormal, abnormalHill;
    private JNoise HeightNoiseOne, HeightNoiseTwo, HeightNoiseThree, HeightNoiseFour, StabilizerNoiseOne, StabilizerNoiseTwo;

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
    private JNoise StabilizerNoiseOne(){
        return StabilizerNoiseOne;
    }
    private JNoise StabilizerNoiseTwo(){
        return StabilizerNoiseTwo;
    }

    public double getStabilizerNoiseOne(int X, int Z){
        double noise = StabilizerNoiseOne.getNoise(X, Z);
        return noise;
    }
    public double getStabilizerNoiseTwo(int X, int Z){
        double noise = StabilizerNoiseTwo.getNoise(X, Z);
        return noise;
    }

    public int getY(int X, int Z) {
        double ratio = 1.5 + getStabilizerNoiseTwo(X, Z);
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
        FastNoiseLite freqAbnormal = abnormal;
        freqAbnormal.SetFrequency(0.004f);

        double noiseLayer = freqAbnormal.GetNoise(x, z);

        freqAbnormal.SetFrequency(0.002f);
        freqAbnormal.SetSeed((Seed*564158)/638);
        noiseLayer += freqAbnormal.GetNoise(x, z)*0.5;

        freqAbnormal.SetFrequency(0.001f);
        freqAbnormal.SetSeed((Seed*185144)/158);
        noiseLayer += freqAbnormal.GetNoise(x, z)*0.25;

        freqAbnormal.SetFrequency(0.0005f);
        freqAbnormal.SetSeed((Seed*451582)/274);
        noiseLayer += freqAbnormal.GetNoise(x, z)*0.125;

        return noiseLayer;
    }

    public double customNoise(int x, int z, float frequency){
        FastNoiseLite freqAbnormal = abnormal;
        freqAbnormal.SetFrequency(frequency);
        return freqAbnormal.GetNoise(x, z);
    }

    /*
    public FastNoiseLite get(){
        return abnormal;
    }
    */

}
