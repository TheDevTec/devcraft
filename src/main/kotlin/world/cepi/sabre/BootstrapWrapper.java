package world.cepi.sabre;

import net.minestom.server.Bootstrap;

/**
 * Bootstrap wrapper for Minestom. Written in java to prevent Kotlin Bootstrap errors
 */
public class BootstrapWrapper {

    public static void main(String[] args) {
        Bootstrap.bootstrap("world.cepi.sabre.SabreKt", args);
    }

}