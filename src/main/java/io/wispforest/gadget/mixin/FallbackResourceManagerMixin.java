package io.wispforest.gadget.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.wispforest.gadget.Gadget;
import io.wispforest.gadget.pond.MixinState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.FallbackResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collection;

@Mixin(FallbackResourceManager.class)
public class FallbackResourceManagerMixin {
    @WrapOperation(method = "listPackResources", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/PackResources;listResources(Lnet/minecraft/server/packs/PackType;Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/server/packs/PackResources$ResourceOutput;)V"))
    private void ignoreErrorsIfNeeded(PackResources pack, PackType type, String namespace, String prefix, PackResources.ResourceOutput consumer, Operation<Collection<ResourceLocation>> original) {
        if (MixinState.IS_IGNORING_ERRORS.get() != null) {
            try {
                original.call(pack, type, namespace, prefix, consumer);
            } catch (Exception e) {
                Gadget.LOGGER.error("Resource pack {} threw an error while loading all resources, which has been ignored", pack.packId());
            }
        } else {
            original.call(pack, type, namespace, prefix, consumer);
        }
    }
}
