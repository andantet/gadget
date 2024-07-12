package io.wispforest.gadget.dump.fake.recipe;

import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.gadget.Gadget;
import io.wispforest.gadget.util.ThrowableUtil;
import io.wispforest.owo.serialization.EndecRecipeSerializer;
import net.minecraft.util.Identifier;

public record WriteErrorRecipe(String exceptionText) implements FakeGadgetRecipe {
    public static final StructEndec<WriteErrorRecipe> ENDEC = StructEndecBuilder.of(
        Endec.STRING.fieldOf("exceptionText", WriteErrorRecipe::exceptionText),
        WriteErrorRecipe::new
    );

    public static WriteErrorRecipe from(Exception e) {
        return new WriteErrorRecipe(ThrowableUtil.throwableToString(e));
    }

    @Override
    public FakeSerializer<WriteErrorRecipe> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class Serializer extends EndecRecipeSerializer<WriteErrorRecipe> implements FakeSerializer<WriteErrorRecipe> {
        public static final Identifier ID = Gadget.id("write_error");
        public static final Serializer INSTANCE = new Serializer();

        private Serializer() {
            super(ENDEC);
        }

        @Override
        public Identifier id() {
            return ID;
        }
    }
}
