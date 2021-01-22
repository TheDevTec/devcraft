package net.minestom.server.recipe;

import net.minestom.server.item.ItemStack;
import net.minestom.server.network.packet.server.play.DeclareRecipesPacket;

public abstract class BlastingRecipe extends Recipe {
    private String group;
    private DeclareRecipesPacket.Ingredient ingredient;
    private ItemStack result;
    private float experience;
    private int cookingTime;

    protected BlastingRecipe(
            String recipeId,
            String group,
            ItemStack result,
            float experience,
            int cookingTime
    ) {
        super(RecipeType.BLASTING, recipeId);
        this.group = group;
        this.result = result;
        this.experience = experience;
        this.cookingTime = cookingTime;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public DeclareRecipesPacket.Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(DeclareRecipesPacket.Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public ItemStack getResult() {
        return result;
    }

    public void setResult(ItemStack result) {
        this.result = result;
    }

    public float getExperience() {
        return experience;
    }

    public void setExperience(float experience) {
        this.experience = experience;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
    }
}
