package io.wispforest.gadget.mixin.client;

import io.wispforest.gadget.Gadget;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSelectionList.class)
public abstract class AbstractSelectionListMixin<E extends AbstractSelectionList.Entry<E>> {
    @Shadow public abstract void setFocused(GuiEventListener focused);

    @Shadow @Nullable protected abstract AbstractSelectionList.Entry<?> getEntryAtPosition(double x, double y);

    @Shadow @Nullable public abstract E getFocused();

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void onRightClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            if (!Gadget.CONFIG.rightClickDump()) {
                cir.setReturnValue(false);
                return;
            }

            AbstractSelectionList.Entry<?> clickedEntry = this.getEntryAtPosition(mouseX, mouseY);
            if (clickedEntry != null) {
                if (clickedEntry.mouseClicked(mouseX, mouseY, button)) {
                    E parentEntry = this.getFocused();

                    if (clickedEntry != parentEntry && parentEntry instanceof ContainerEventHandler) {
                        ContainerEventHandler parentElement = (ContainerEventHandler) parentEntry;
                        parentElement.setFocused((GuiEventListener) null);
                    }

                    this.setFocused(clickedEntry);
                    cir.setReturnValue(true);
                    return;
                }
            }

            cir.setReturnValue(false);
            return;
        }
    }
}
