package io.wispforest.gadget.mixin;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/network/protocol/game/ClientboundLevelChunkPacketData$BlockEntityInfo")
public class ClientboundLevelChunkPacketDataBlockEntityInfoMixin {
    private int gadget$originalBlockEntityTypeId = -1;

    @Inject(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;readById(Lnet/minecraft/core/IdMap;)Ljava/lang/Object;"))
    private void saveId(FriendlyByteBuf buf, CallbackInfo ci) {
        int readerIdx = buf.readerIndex();

        try {
            gadget$originalBlockEntityTypeId = buf.readVarInt();
        } finally {
            buf.readerIndex(readerIdx);
        }
    }

    @WrapWithCondition(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;writeId(Lnet/minecraft/core/IdMap;Ljava/lang/Object;)V"))
    private boolean useId(FriendlyByteBuf buf, IdMap<BlockEntityType<?>> registry, Object type) {
        if (type == null) {
            buf.writeVarInt(gadget$originalBlockEntityTypeId);
            return false;
        } else {
            return true;
        }
    }

}
