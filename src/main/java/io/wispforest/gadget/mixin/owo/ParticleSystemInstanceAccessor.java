package io.wispforest.gadget.mixin.owo;

import io.wispforest.owo.particles.systems.ParticleSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "io.wispforest.owo.particles.systems.ParticleSystemController$ParticleSystemInstance", remap = false)
public interface ParticleSystemInstanceAccessor<T> {
    @Accessor
    ParticleSystem<T> getSystem();

    @Accessor
    T getData();
}
