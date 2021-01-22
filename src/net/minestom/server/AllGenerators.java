package net.minestom.server;

import java.io.IOException;

import net.minestom.server.blocks.BlockEnumGenerator;
import net.minestom.server.enchantment.EnchantmentEnumGenerator;
import net.minestom.server.entitytypes.EntityTypeEnumGenerator;
import net.minestom.server.fluids.FluidEnumGenerator;
import net.minestom.server.items.ItemEnumGenerator;
import net.minestom.server.particles.ParticleEnumGenerator;
import net.minestom.server.potions.PotionEffectEnumGenerator;
import net.minestom.server.potions.PotionEnumGenerator;
import net.minestom.server.sounds.SoundEnumGenerator;
import net.minestom.server.stats.StatsEnumGenerator;

public class AllGenerators {

    public static void main(String[] args) throws IOException {
        BlockEnumGenerator.main(args);
        ItemEnumGenerator.main(args); // must be done after block
        PotionEnumGenerator.main(args);
        PotionEffectEnumGenerator.main(args);
        EnchantmentEnumGenerator.main(args);
        EntityTypeEnumGenerator.main(args);
        SoundEnumGenerator.main(args);
        ParticleEnumGenerator.main(args);
        StatsEnumGenerator.main(args);
        FluidEnumGenerator.main(args);
        RegistriesGenerator.main(args);
    }
}
