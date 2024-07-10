package io.wispforest.gadget.mixin;

import net.minecraft.server.packs.FilePackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FilePackResources.class)
public class FilePackResourcesMixin {
    @ModifyVariable(method = "listResources", at = @At(value = "INVOKE", target = "Ljava/util/Enumeration;hasMoreElements()Z"), ordinal = 3)
    private String makeItBetter(String in) {
        return in.replace("//", "/");
    }
}
