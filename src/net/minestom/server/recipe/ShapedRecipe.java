package net.minestom.server.recipe;

import java.util.List;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.packet.server.play.DeclareRecipesPacket;

public abstract class ShapedRecipe extends Recipe {
    private final int width;
    private final int height;
    private String group;
    private final List<DeclareRecipesPacket.Ingredient> ingredients;
    private ItemStack result;

    protected ShapedRecipe(String recipeId,
                        int width,
                        int height,
                        String group,
                        List<DeclareRecipesPacket.Ingredient> ingredients,
                        ItemStack result) {
        super(RecipeType.SHAPED, recipeId);
        this.width = width;
        this.height = height;
        this.group = group;
        this.ingredients = ingredients!=null?ingredients:new ArrayList<>();
        this.result = result;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void addIngredient(DeclareRecipesPacket.Ingredient ingredient) {
        if (ingredients.size() + 1 > width * height) {
            throw new IndexOutOfBoundsException("You cannot add more ingredients than width*height");
        }

        ingredients.add(ingredient);
    }

    public List<DeclareRecipesPacket.Ingredient> getIngredients() {
        return ingredients;
    }

    @NotNull
    public ItemStack getResult() {
        return result;
    }

    public void setResult(ItemStack result) {
        this.result = result;
    }
}
