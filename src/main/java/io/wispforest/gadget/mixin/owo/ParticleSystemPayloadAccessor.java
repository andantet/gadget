package io.wispforest.gadget.mixin.owo;

import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "io.wispforest.owo.particles.systems.ParticleSystemController$ParticleSystemPayload")
public interface ParticleSystemPayloadAccessor extends CustomPayload {
    @Accessor
    Vec3d getPos();
}
