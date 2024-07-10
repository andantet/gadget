package io.wispforest.gadget.testmod.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class GadgetTestmodClient implements ClientModInitializer {
    public static final FunnyItem FUNNY_ITEM = new FunnyItem();

    @Override
    public void onInitializeClient() {
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("gadget-testmod", "funny"), FUNNY_ITEM);

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("gadget-testmod")
                .then(literal("epic")
                    .executes(ctx -> {
                        ClientPlayNetworking.send(new EpicPacket("cringe"));
                        return 1;
                    })));
        });

        ServerPlayNetworking.registerGlobalReceiver(EpicPacket.TYPE, (pkt, player, sender) -> {
            // Do nothing.
        });
    }
}
