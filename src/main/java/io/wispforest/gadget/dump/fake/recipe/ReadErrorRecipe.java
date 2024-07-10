package io.wispforest.gadget.dump.fake.recipe;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.concurrent.ThreadLocalRandom;

public record ReadErrorRecipe(byte[] data, Exception exception) implements FakeGadgetRecipe {
    public static RecipeHolder<ReadErrorRecipe> from(Exception exception, FriendlyByteBuf buf) {
        int start = buf.readerIndex();
        ResourceLocation recipeId = new ResourceLocation(
            "gadget-fake",
            "cringe-recipe-bruh-" + ThreadLocalRandom.current().nextInt()
        );

        try {
            buf.readResourceLocation();
            recipeId = buf.readResourceLocation();
        } catch (Exception e) {
            exception.addSuppressed(e);
        }

        buf.readerIndex(start);
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);

        return new RecipeHolder<>(recipeId, new ReadErrorRecipe(bytes, exception));
    }

    @Override
    public FakeSerializer<?> getSerializer() {
        throw new UnsupportedOperationException();
    }
}
