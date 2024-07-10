package io.wispforest.gadget.mixin.owo;

import io.wispforest.gadget.client.gui.ComponentEventCounter;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OwoUIAdapter.class)
public abstract class OwoUIAdapterMixin implements Renderable, GuiEventListener {
    @Inject(method = {"render", "method_25394"}, at = @At("HEAD"))
    private void reset(GuiGraphics ctx, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        ComponentEventCounter.reset();
    }

    @Inject(method = {"mouseClicked", "mouseReleased", "mouseScrolled", "mouseDragged", "keyPressed", "charTyped"}, at = @At("HEAD"))
    private void reset(CallbackInfoReturnable<?> cir) {
        ComponentEventCounter.reset();
    }

    @Inject(method = {"mouseClicked", "mouseReleased", "mouseScrolled", "mouseDragged", "keyPressed", "charTyped"}, at = @At("RETURN"))
    private void tally(CallbackInfoReturnable<Boolean> cir) {
        ComponentEventCounter.tally();
    }

    @Inject(method = {"render", "method_25394"}, at = @At("RETURN"))
    private void tally(GuiGraphics ctx, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        ComponentEventCounter.tally();
    }
}
