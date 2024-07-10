package io.wispforest.gadget.dump.fake.recipe;

import io.wispforest.gadget.Gadget;
import io.wispforest.gadget.util.ThrowableUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record WriteErrorRecipe(String exceptionText) implements FakeGadgetRecipe {
    public static WriteErrorRecipe from(Exception e) {
        return new WriteErrorRecipe(ThrowableUtil.throwableToString(e));
    }

    @Override
    public FakeSerializer<WriteErrorRecipe> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class Serializer implements FakeSerializer<WriteErrorRecipe> {
        public static final ResourceLocation ID = Gadget.id("write_error");
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public ResourceLocation id() {
            return ID;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, WriteErrorRecipe recipe) {
            buf.writeUtf(recipe.exceptionText);
        }

        @Override
        public WriteErrorRecipe fromNetwork(FriendlyByteBuf buf) {
            return new WriteErrorRecipe(buf.readUtf());
        }
    }
}
