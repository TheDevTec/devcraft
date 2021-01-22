package net.minestom.server.command.builder.arguments;

import net.minestom.server.command.builder.arguments.minecraft.ArgumentColor;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentFloatRange;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentIntRange;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentItemStack;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentNbtCompoundTag;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentNbtTag;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentTime;
import net.minestom.server.command.builder.arguments.minecraft.SuggestionType;
import net.minestom.server.command.builder.arguments.minecraft.registry.ArgumentBlockState;
import net.minestom.server.command.builder.arguments.minecraft.registry.ArgumentEnchantment;
import net.minestom.server.command.builder.arguments.minecraft.registry.ArgumentEntityType;
import net.minestom.server.command.builder.arguments.minecraft.registry.ArgumentParticle;
import net.minestom.server.command.builder.arguments.minecraft.registry.ArgumentPotionEffect;
import net.minestom.server.command.builder.arguments.number.ArgumentDouble;
import net.minestom.server.command.builder.arguments.number.ArgumentFloat;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;
import net.minestom.server.command.builder.arguments.number.ArgumentLong;
import net.minestom.server.command.builder.arguments.relative.ArgumentRelativeBlockPosition;
import net.minestom.server.command.builder.arguments.relative.ArgumentRelativeVec2;
import net.minestom.server.command.builder.arguments.relative.ArgumentRelativeVec3;

/**
 * Convenient class listing all the basics {@link Argument}.
 * <p>
 * Please see the specific class documentation for further info.
 */
public class ArgumentType {

    public static ArgumentBoolean Boolean(String id) {
        return new ArgumentBoolean(id);
    }

    public static ArgumentLong Long(String id) {
        return new ArgumentLong(id);
    }

    public static ArgumentInteger Integer(String id) {
        return new ArgumentInteger(id);
    }

    public static ArgumentDouble Double(String id) {
        return new ArgumentDouble(id);
    }

    public static ArgumentFloat Float(String id) {
        return new ArgumentFloat(id);
    }

    public static ArgumentString String(String id) {
        return new ArgumentString(id);
    }

    public static ArgumentWord Word(String id) {
        return new ArgumentWord(id);
    }

    public static ArgumentDynamicWord DynamicWord(String id, SuggestionType suggestionType) {
        return new ArgumentDynamicWord(id, suggestionType);
    }

    public static ArgumentDynamicWord DynamicWord(String id) {
        return DynamicWord(id, SuggestionType.ASK_SERVER);
    }

    public static ArgumentStringArray StringArray(String id) {
        return new ArgumentStringArray(id);
    }

    public static ArgumentDynamicStringArray DynamicStringArray(String id) {
        return new ArgumentDynamicStringArray(id);
    }

    // Minecraft specific arguments

    public static ArgumentColor Color(String id) {
        return new ArgumentColor(id);
    }

    public static ArgumentTime Time(String id) {
        return new ArgumentTime(id);
    }

    public static ArgumentEnchantment Enchantment(String id) {
        return new ArgumentEnchantment(id);
    }

    public static ArgumentParticle Particle(String id) {
        return new ArgumentParticle(id);
    }

    public static ArgumentPotionEffect Potion(String id) {
        return new ArgumentPotionEffect(id);
    }

    public static ArgumentEntityType EntityType(String id) {
        return new ArgumentEntityType(id);
    }

    public static ArgumentBlockState BlockState(String id) {
        return new ArgumentBlockState(id);
    }

    public static ArgumentIntRange IntRange(String id) {
        return new ArgumentIntRange(id);
    }

    public static ArgumentFloatRange FloatRange(String id) {
        return new ArgumentFloatRange(id);
    }

    public static ArgumentEntity Entities(String id) {
        return new ArgumentEntity(id);
    }

    public static ArgumentItemStack ItemStack(String id) {
        return new ArgumentItemStack(id);
    }

    public static ArgumentNbtCompoundTag NbtCompound(String id) {
        return new ArgumentNbtCompoundTag(id);
    }

    public static ArgumentNbtTag NBT(String id) {
        return new ArgumentNbtTag(id);
    }

    public static ArgumentRelativeBlockPosition RelativeBlockPosition(String id) {
        return new ArgumentRelativeBlockPosition(id);
    }

    public static ArgumentRelativeVec3 RelativeVec3(String id) {
        return new ArgumentRelativeVec3(id);
    }

    public static ArgumentRelativeVec2 RelativeVec2(String id) {
        return new ArgumentRelativeVec2(id);
    }

}
