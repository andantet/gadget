package io.wispforest.gadget.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.wispforest.gadget.client.PoseStackLogger;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Inject(method = "checkPoseStack", at = @At("HEAD"), cancellable = true)
    private void checkEmpty(PoseStack matrices, CallbackInfo ci) {
        if (!matrices.clear()
         && PoseStackLogger.tripError(matrices, "Matrix stack not empty")) {
            ci.cancel();
        }
    }
}
