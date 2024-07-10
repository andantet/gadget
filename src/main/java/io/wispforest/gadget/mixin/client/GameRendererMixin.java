package io.wispforest.gadget.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.wispforest.gadget.client.PoseStackLogger;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "renderLevel", at = @At("RETURN"))
    private void checkMatrixStack(float tickDelta, long limitTime, PoseStack matrices, CallbackInfo ci) {
        if (!matrices.clear()) {
            PoseStackLogger.tripError(matrices, "Matrix stack not empty");
        }
    }
}
