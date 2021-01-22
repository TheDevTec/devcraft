package net.minestom.server.recipe;

import net.minestom.server.entity.Player;

public abstract class Recipe {
    protected final RecipeType recipeType;
    protected final String recipeId;

    protected Recipe(RecipeType recipeType, String recipeId) {
        this.recipeType = recipeType;
        this.recipeId = recipeId;
    }

    public abstract boolean shouldShow(Player player);

    public RecipeType getRecipeType() {
        return recipeType;
    }

    public String getRecipeId() {
        return recipeId;
    }

    protected enum RecipeType {
        SHAPELESS,
        SHAPED,
        SMELTING,
        BLASTING,
        SMOKING,
        CAMPFIRE_COOKING,
        STONECUTTING,
        SMITHING
    }

}
