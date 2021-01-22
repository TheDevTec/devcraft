package net.minestom.server.network.packet.server.play;

import net.minestom.server.item.ItemStack;
import net.minestom.server.network.packet.server.ServerPacket;
import net.minestom.server.network.packet.server.ServerPacketIdentifier;
import net.minestom.server.utils.binary.BinaryWriter;
import net.minestom.server.utils.validate.Check;

public class DeclareRecipesPacket implements ServerPacket {

    public DeclaredRecipe[] recipes;

    @Override
    public void write(BinaryWriter writer) {
        Check.notNull(recipes, "Recipes cannot be null!");

        writer.writeVarInt(recipes.length);
        for (DeclaredRecipe recipe : recipes) {
            recipe.write(writer);
        }
    }

    @Override
    public int getId() {
        return ServerPacketIdentifier.DECLARE_RECIPES;
    }

    public abstract static class DeclaredRecipe {
        protected final String recipeId;
        protected final String recipeType;

        protected DeclaredRecipe(String recipeId, String recipeType) {
            this.recipeId = recipeId;
            this.recipeType = recipeType;
        }

        public void write(BinaryWriter writer) {
            writer.writeSizedString(recipeType);
            writer.writeSizedString(recipeId);
        }
    }

    public static class DeclaredShapelessCraftingRecipe extends DeclaredRecipe {
        private final String group;
        private final Ingredient[] ingredients;
        private final ItemStack result;

        public DeclaredShapelessCraftingRecipe(
                String recipeId,
                String group,
                Ingredient[] ingredients,
                ItemStack result
        ) {
            super(recipeId, "crafting_shapeless");
            this.group = group;
            this.ingredients = ingredients;
            this.result = result;
        }

        @Override
        public void write(BinaryWriter writer) {
            // Write type & id
            super.write(writer);
            // Write recipe specific stuff.
            writer.writeSizedString(group);
            writer.writeVarInt(ingredients.length);
            for (Ingredient ingredient : ingredients) {
                ingredient.write(writer);
            }
            writer.writeItemStack(result);
        }
    }

    public static class DeclaredShapedCraftingRecipe extends DeclaredRecipe {
        public final int width;
        public final int height;
        private final String group;
        private final Ingredient[] ingredients;
        private final ItemStack result;

        public DeclaredShapedCraftingRecipe(
                String recipeId,
                int width,
                int height,
                String group,
                Ingredient[] ingredients,
                ItemStack result
        ) {
            super(recipeId, "crafting_shaped");
            this.group = group;
            this.ingredients = ingredients;
            this.result = result;
            this.width = width;
            this.height = height;
        }

        @Override
        public void write(BinaryWriter writer) {
            // Write type & id
            super.write(writer);
            // Write recipe specific stuff.
            writer.writeVarInt(width);
            writer.writeVarInt(height);
            writer.writeSizedString(group);
            for (Ingredient ingredient : ingredients) {
                ingredient.write(writer);
            }
            writer.writeItemStack(result);
        }
    }

    public static class DeclaredSmeltingRecipe extends DeclaredRecipe {
        private final String group;
        private final Ingredient ingredient;
        private final ItemStack result;
        private final float experience;
        private final int cookingTime;

        public DeclaredSmeltingRecipe(
                String recipeId,
                String group,
                Ingredient ingredient,
                ItemStack result,
                float experience,
                int cookingTime
        ) {
            super(recipeId, "smelting");
            this.group = group;
            this.ingredient = ingredient;
            this.result = result;
            this.experience = experience;
            this.cookingTime = cookingTime;
        }

        @Override
        public void write(BinaryWriter writer) {
            // Write type & id
            super.write(writer);
            // Write recipe specific stuff.
            writer.writeSizedString(group);
            ingredient.write(writer);
            writer.writeItemStack(result);
            writer.writeFloat(experience);
            writer.writeVarInt(cookingTime);
        }
    }

    public static class DeclaredBlastingRecipe extends DeclaredRecipe {
        private final String group;
        private final Ingredient ingredient;
        private final ItemStack result;
        private final float experience;
        private final int cookingTime;

        public DeclaredBlastingRecipe(
                String recipeId,
                String group,
                Ingredient ingredient,
                ItemStack result,
                float experience,
                int cookingTime
        ) {
            super(recipeId, "blasting");
            this.group = group;
            this.ingredient = ingredient;
            this.result = result;
            this.experience = experience;
            this.cookingTime = cookingTime;
        }

        @Override
        public void write(BinaryWriter writer) {
            // Write type & id
            super.write(writer);
            // Write recipe specific stuff.
            writer.writeSizedString(group);
            ingredient.write(writer);
            writer.writeItemStack(result);
            writer.writeFloat(experience);
            writer.writeVarInt(cookingTime);
        }
    }

    public static class DeclaredSmokingRecipe extends DeclaredRecipe {
        private final String group;
        private final Ingredient ingredient;
        private final ItemStack result;
        private final float experience;
        private final int cookingTime;

        public DeclaredSmokingRecipe(
                String recipeId,
                String group,
                Ingredient ingredient,
                ItemStack result,
                float experience,
                int cookingTime
        ) {
            super(recipeId, "smoking");
            this.group = group;
            this.ingredient = ingredient;
            this.result = result;
            this.experience = experience;
            this.cookingTime = cookingTime;
        }

        @Override
        public void write(BinaryWriter writer) {
            // Write type & id
            super.write(writer);
            // Write recipe specific stuff.
            writer.writeSizedString(group);
            ingredient.write(writer);
            writer.writeItemStack(result);
            writer.writeFloat(experience);
            writer.writeVarInt(cookingTime);
        }
    }

    public static class DeclaredCampfireCookingRecipe extends DeclaredRecipe {
        private final String group;
        private final Ingredient ingredient;
        private final ItemStack result;
        private final float experience;
        private final int cookingTime;

        public DeclaredCampfireCookingRecipe(
                String recipeId,
                String group,
                Ingredient ingredient,
                ItemStack result,
                float experience,
                int cookingTime
        ) {
            super(recipeId, "campfire_cooking");
            this.group = group;
            this.ingredient = ingredient;
            this.result = result;
            this.experience = experience;
            this.cookingTime = cookingTime;
        }

        @Override
        public void write(BinaryWriter writer) {
            // Write type & id
            super.write(writer);
            // Write recipe specific stuff.
            writer.writeSizedString(group);
            ingredient.write(writer);
            writer.writeItemStack(result);
            writer.writeFloat(experience);
            writer.writeVarInt(cookingTime);
        }
    }

    public static class DeclaredStonecutterRecipe extends DeclaredRecipe {
        private final String group;
        private final Ingredient ingredient;
        private final ItemStack result;

        public DeclaredStonecutterRecipe(
                String recipeId,
                String group,
                Ingredient ingredient,
                ItemStack result
        ) {
            super(recipeId, "stonecutter");
            this.group = group;
            this.ingredient = ingredient;
            this.result = result;
        }

        @Override
        public void write(BinaryWriter writer) {
            // Write type & id
            super.write(writer);
            // Write recipe specific stuff.
            writer.writeSizedString(group);
            ingredient.write(writer);
            writer.writeItemStack(result);
        }
    }

    public final static class DeclaredSmithingRecipe extends DeclaredRecipe {
        private final Ingredient base;
        private final Ingredient addition;
        private final ItemStack result;

        public DeclaredSmithingRecipe(
                String recipeId,
                Ingredient base,
                Ingredient addition,
                ItemStack result
        ) {
            super(recipeId, "smithing");
            this.base = base;
            this.addition = addition;
            this.result = result;
        }

        @Override
        public void write(BinaryWriter writer) {
            // Write type & id
            super.write(writer);
            // Write recipe specific stuff.
            base.write(writer);
            addition.write(writer);
            writer.writeItemStack(result);
        }
    }

    public static class Ingredient {

        // The count of each item should be 1
        public ItemStack[] items;

        private void write(BinaryWriter writer) {
            writer.writeVarInt(items.length);
            for (ItemStack itemStack : items) {
                writer.writeItemStack(itemStack);
            }
        }

    }
}
