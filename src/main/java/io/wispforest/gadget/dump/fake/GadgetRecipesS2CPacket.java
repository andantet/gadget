package io.wispforest.gadget.dump.fake;

import io.wispforest.gadget.Gadget;
import io.wispforest.gadget.dump.fake.recipe.FakeGadgetRecipe;
import io.wispforest.gadget.dump.fake.recipe.ReadErrorRecipe;
import io.wispforest.gadget.dump.fake.recipe.WriteErrorRecipe;
import io.wispforest.gadget.util.NetworkUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.ArrayList;
import java.util.List;

public record GadgetRecipesS2CPacket(List<RecipeHolder<?>> recipes) implements FakeGadgetPacket {
    public static final int ID = -4;

    public static GadgetRecipesS2CPacket read(FriendlyByteBuf buf, ConnectionProtocol state, PacketFlow side) {
        int size = buf.readVarInt();
        List<RecipeHolder<?>> recipes = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            FriendlyByteBuf subBuf = NetworkUtil.readOfLengthIntoTmp(buf);

            try {
                recipes.add(ClientboundUpdateRecipesPacket.fromNetwork(subBuf));
            } catch (Exception e) {
                subBuf.readerIndex(0);
                recipes.add(ReadErrorRecipe.from(e, subBuf));
            }
        }

        return new GadgetRecipesS2CPacket(recipes);
    }

    @Override
    public int id() {
        return ID;
    }

    @Override
    public Packet<?> unwrapVanilla() {
        return new ClientboundUpdateRecipesPacket(recipes);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void writeToDump(FriendlyByteBuf buf, ConnectionProtocol state, PacketFlow side) {
        buf.writeVarInt(recipes.size());

        for (var recipe : recipes) {
            try (var ignored = NetworkUtil.writeByteLength(buf)) {
                int startWriteIdx = buf.writerIndex();

                try {
                    if (recipe.value() instanceof FakeGadgetRecipe fakeRecipe) {
                        writeFake(buf, new RecipeHolder<>(recipe.id(), fakeRecipe));
                        return;
                    }

                    RecipeSerializer<?> serializer = recipe.value().getSerializer();
                    ResourceLocation serializerId = BuiltInRegistries.RECIPE_SERIALIZER.getResourceKey(serializer).map(ResourceKey::location).orElse(null);

                    if (serializerId == null)
                        throw new UnsupportedOperationException(serializer + " is not a registered serializer!");

                    buf.writeResourceLocation(serializerId);
                    buf.writeResourceLocation(recipe.id());

                    ((RecipeSerializer<Recipe<?>>) serializer).toNetwork(buf, recipe.value());
                } catch (Exception e) {
                    buf.writerIndex(startWriteIdx);

                    Gadget.LOGGER.error("Error while writing recipe {}", recipe, e);

                    WriteErrorRecipe writeError = WriteErrorRecipe.from(e);
                    writeFake(buf, new RecipeHolder<>(recipe.id(), writeError));
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void writeFake(FriendlyByteBuf buf, RecipeHolder<? extends FakeGadgetRecipe> recipe) {
        buf.writeResourceLocation(recipe.value().getSerializer().id());
        buf.writeResourceLocation(recipe.id());
        ((RecipeSerializer<FakeGadgetRecipe>) recipe.value().getSerializer()).toNetwork(buf, recipe.value());
    }
}
