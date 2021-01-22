package net.minestom.server.item.rule;

import net.minestom.server.item.ItemStack;
import net.minestom.server.item.StackingRule;
import net.minestom.server.utils.MathUtils;

public class VanillaStackingRule extends StackingRule {

    public VanillaStackingRule(int maxSize) {
        super(maxSize);
    }

    @Override
    public boolean canBeStacked(ItemStack item1, ItemStack item2) {
        return item1.isSimilar(item2);
    }

    @Override
    public boolean canApply(ItemStack item, int newAmount) {
        return MathUtils.isBetween(newAmount, 0, getMaxSize());
    }

    @Override
    public ItemStack apply(ItemStack item, int newAmount) {
        if (newAmount <= 0)
            return ItemStack.getAirItem();

        item.setAmount((byte) newAmount);
        return item;
    }

    @Override
    public int getAmount(ItemStack itemStack) {
        return itemStack.getAmount();
    }
}
