package io.wispforest.gadget.dump.fake.recipe;

import com.mojang.serialization.Codec;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public interface FakeGadgetRecipe extends Recipe<Container> {
    @Override
    default boolean matches(Container inventory, Level world) {
        throw new UnsupportedOperationException();
    }

    @Override
    default ItemStack assemble(Container inventory, RegistryAccess registryManager) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean canCraftInDimensions(int width, int height) {
        throw new UnsupportedOperationException();
    }

    @Override
    default ItemStack getResultItem(RegistryAccess registryManager) {
        throw new UnsupportedOperationException();
    }

    @Override
    default NonNullList<ItemStack> getRemainingItems(Container inventory) {
        throw new UnsupportedOperationException();
    }

    @Override
    default NonNullList<Ingredient> getIngredients() {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean isSpecial() {
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
    default ItemStack getToastSymbol() {
        throw new UnsupportedOperationException();
    }

    @Override
    default RecipeType<?> getType() {
        throw new UnsupportedOperationException();
    }

    @Override
    FakeSerializer<?> getSerializer();

    @Override
    default boolean isIncomplete() {
        throw new UnsupportedOperationException();
    }

    interface FakeSerializer<R extends FakeGadgetRecipe> extends RecipeSerializer<R> {
        ResourceLocation id();

        @Override
        default Codec<R> codec() {
            throw new UnsupportedOperationException();
        }
    }
}
