package io.wispforest.gadget.mixin.client;

import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    // TODO: idfk anymore.
//    @Inject(method = "renderWorld", at = @At("RETURN"))
//    private void checkMatrixStack(RenderTickCounter tickCounter, CallbackInfo ci) {
//        if (!matrices.isEmpty()) {
//            MatrixStackLogger.tripError(matrices, "Matrix stack not empty");
//        }
//    }
}
