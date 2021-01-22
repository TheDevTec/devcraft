package net.minestom.server.recipe;

import java.util.List;

import java.util.ArrayList;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.packet.server.play.DeclareRecipesPacket;

public abstract class ShapelessRecipe extends Recipe {
    private String group;
    private final List<DeclareRecipesPacket.Ingredient> ingredients;
    private ItemStack result;

    protected ShapelessRecipe(
            String recipeId,
            String group,
            List<DeclareRecipesPacket.Ingredient> ingredients,
            ItemStack result
    ) {
        super(RecipeType.SHAPELESS, recipeId);
        this.group = group;
        this.ingredients = ingredients!=null?ingredients:new ArrayList<>();
        this.result = result;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void addIngredient(DeclareRecipesPacket.Ingredient ingredient) {
        ingredients.add(ingredient);
    }

    public List<DeclareRecipesPacket.Ingredient> getIngredients() {
        return ingredients;
    }

    public ItemStack getResult() {
        return result;
    }

    public void setResult(ItemStack result) {
        this.result = result;
    }
}
