package net.minestom.server.command.builder;

import java.util.HashMap;
import java.util.Map;

import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import com.sun.istack.internal.NotNull;

import net.minestom.server.chat.ChatColor;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.entity.EntityType;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Enchantment;
import net.minestom.server.item.ItemStack;
import net.minestom.server.particle.Particle;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.utils.entity.EntityFinder;
import net.minestom.server.utils.location.RelativeBlockPosition;
import net.minestom.server.utils.location.RelativeVec;
import net.minestom.server.utils.math.FloatRange;
import net.minestom.server.utils.math.IntRange;
import net.minestom.server.utils.time.UpdateOption;

/**
 * Class used to retrieve argument data in a {@link CommandExecutor}.
 * <p>
 * All id are the one specified in the {@link net.minestom.server.command.builder.arguments.Argument} constructor.
 * <p>
 * All methods are @{@link NotNull} in the sense that you should not have to verify their validity since if the syntax
 * is called, it means that all of its arguments are correct. Be aware that trying to retrieve an argument not present
 * in the syntax will result in a {@link NullPointerException}.
 */
public final class Arguments {

    private Map<String, Object> args = new HashMap<>();

    @SuppressWarnings("unchecked")
	public <T> T get(Argument<T> argument) {
        return (T) getObject(argument.getId());
    }

    public boolean getBoolean(String id) {
        return (boolean) getObject(id);
    }

    public long getLong(String id) {
        return (long) getObject(id);
    }

    public int getInteger(String id) {
        return (int) getObject(id);
    }

    public double getDouble(String id) {
        return (double) getObject(id);
    }

    public float getFloat(String id) {
        return (float) getObject(id);
    }

    public String getString(String id) {
        return (String) getObject(id);
    }

    public String getWord(String id) {
        return getString(id);
    }

    public String[] getStringArray(String id) {
        return (String[]) getObject(id);
    }

    public ChatColor getColor(String id) {
        return (ChatColor) getObject(id);
    }

    public UpdateOption getTime(String id) {
        return (UpdateOption) getObject(id);
    }

    public Enchantment getEnchantment(String id) {
        return (Enchantment) getObject(id);
    }

    public Particle getParticle(String id) {
        return (Particle) getObject(id);
    }

    public PotionEffect getPotionEffect(String id) {
        return (PotionEffect) getObject(id);
    }

    public EntityType getEntityType(String id) {
        return (EntityType) getObject(id);
    }

    public Block getBlockState(String id) {
        return (Block) getObject(id);
    }

    public IntRange getIntRange(String id) {
        return (IntRange) getObject(id);
    }

    public FloatRange getFloatRange(String id) {
        return (FloatRange) getObject(id);
    }

    public EntityFinder getEntities(String id) {
        return (EntityFinder) getObject(id);
    }

    public ItemStack getItemStack(String id) {
        return (ItemStack) getObject(id);
    }

    public NBTCompound getNbtCompound(String id) {
        return (NBTCompound) getObject(id);
    }

    public NBT getNBT(String id) {
        return (NBT) getObject(id);
    }

    public RelativeBlockPosition getRelativeBlockPosition(String id) {
        return (RelativeBlockPosition) getObject(id);
    }

    public RelativeVec getRelativeVector(String id) {
        return (RelativeVec) getObject(id);
    }

    public Object getObject(String id) {
        return args.computeIfAbsent(id, s -> {
            throw new NullPointerException(
                    "The argument with the id '" + id + "' has no value assigned, be sure to check your arguments id, your syntax, and that you do not change the argument id dynamically.");
        });
    }

    protected void setArg(String id, Object value) {
        this.args.put(id, value);
    }

    protected void copy(Arguments arguments) {
        this.args = arguments.args;
    }

    protected void clear() {
        this.args.clear();
    }

    protected void retrieveDefaultValues(Map<String, Object> defaultValuesMap) {
        if (defaultValuesMap == null)
            return;

        for (Map.Entry<String, Object> entry : defaultValuesMap.entrySet()) {
            final String key = entry.getKey();
            if (!args.containsKey(key))
                this.args.put(key, entry.getValue());
        }

    }
}
