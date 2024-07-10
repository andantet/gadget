package io.wispforest.gadget.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.wispforest.gadget.client.PoseStackLogger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Deque;

@Mixin(PoseStack.class)
public class PoseStackMixin {
    @Shadow @Final private Deque<PoseStack.Pose> poseStack;

    @Inject(method = "popPose", at = @At("HEAD"), cancellable = true)
    private void onPop(CallbackInfo ci) {
        if (poseStack.size() == 1
         && PoseStackLogger.tripError((PoseStack) (Object) this, "Tried to pop empty MatrixStack")) {
            ci.cancel();
            return;
        }

        PoseStackLogger.logOp((PoseStack)(Object) this, false, poseStack.size() - 2);
    }

    @Inject(method = "pushPose", at = @At("HEAD"))
    private void onPush(CallbackInfo ci) {
        PoseStackLogger.logOp((PoseStack)(Object) this, true, poseStack.size() - 1);
    }
}
