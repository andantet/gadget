package io.wispforest.gadget.mixin.nbt;

import io.wispforest.gadget.nbt.LockableNbt;
import io.wispforest.gadget.nbt.LockableNbtInternal;
import io.wispforest.gadget.nbt.NbtLock;
import io.wispforest.gadget.nbt.NbtLocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(ItemStack.class)
public class ItemStackMixin implements LockableNbtInternal {
    @Shadow private @Nullable CompoundTag tag;
    @Unique private final List<NbtLock> gadget$locks = new ArrayList<>();

    @Override
    public List<NbtLock> gadget$locks() {
        return gadget$locks;
    }

    @Override
    public void lock(NbtLock lock) {
        gadget$locks().add(lock);

        if (tag instanceof LockableNbt lockable) {
            lockable.lock(lock);
        }
    }

    @Override
    public void unlock(NbtLock lock) {
        gadget$locks().remove(lock);

        if (tag instanceof LockableNbt lockable) {
            lockable.unlock(lock);
        }
    }

    @Inject(method = "setTag", at = @At("HEAD"))
    private void checkMutability(CompoundTag nbt, CallbackInfo ci) {
        gadget$checkWrite();
    }

    @Inject(method = {"resetHoverName", "removeTagKey"}, at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/world/item/ItemStack;tag:Lnet/minecraft/nbt/CompoundTag;"))
    private void checkMutability(CallbackInfo ci) {
        gadget$checkWrite();
    }

    @Inject(method = "getHoverName", at = @At("HEAD"))
    private void lockGetName(CallbackInfoReturnable<Component> cir) {
        lock(NbtLocks.GET_NAME);
    }

    @Inject(method = "getHoverName", at = @At("RETURN"))
    private void unlockGetName(CallbackInfoReturnable<Component> cir) {
        unlock(NbtLocks.GET_NAME);
    }
}
