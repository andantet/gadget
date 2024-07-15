package io.wispforest.gadget.mixin.owo;

import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(targets = "io.wispforest.owo.network.OwoHandshake$HandshakeResponse")
public interface HandshakeResponseAccessor {
    @Accessor
    Map<Identifier, Integer> getRequiredChannels();

    @Accessor
    Map<Identifier, Integer> getRequiredControllers();

    @Accessor
    Map<Identifier, Integer> getOptionalChannels();
}
