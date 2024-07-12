package io.wispforest.gadget.dump.fake.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public interface FakeGadgetRecipe extends Recipe<CraftingRecipeInput> {
    @Override
    default boolean matches(CraftingRecipeInput input, World world) {
        throw new UnsupportedOperationException();
    }

    @Override
    default ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean fits(int width, int height) {
        throw new UnsupportedOperationException();
    }

    @Override
    default ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup){
        throw new UnsupportedOperationException();
    }

    @Override
    default DefaultedList<ItemStack> getRemainder(CraftingRecipeInput input) {
        throw new UnsupportedOperationException();
    }

    @Override
    default DefaultedList<Ingredient> getIngredients() {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean isIgnoredInRecipeBook() {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean showNotification() {
        throw new UnsupportedOperationException();
    }

    @Override
    default String getGroup() {
        throw new UnsupportedOperationException();
    }

    @Override
    default ItemStack createIcon() {
        throw new UnsupportedOperationException();
    }

    @Override
    default RecipeType<?> getType() {
        throw new UnsupportedOperationException();
    }

    @Override
    FakeSerializer<?> getSerializer();

    @Override
    default boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    interface FakeSerializer<R extends FakeGadgetRecipe> extends RecipeSerializer<R> {
        Identifier id();

        @Override
        default MapCodec<R> codec() {
            throw new UnsupportedOperationException();
        }
    }
}
