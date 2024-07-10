package io.wispforest.gadget.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;

// get it? it calls .slice() on passed in bufs?
public class SlicingFriendlyByteBuf extends FriendlyByteBuf {
    public SlicingFriendlyByteBuf(ByteBuf parent) {
        super(parent);
    }

    @Override
    public FriendlyByteBuf writeBytes(ByteBuf byteBuf) {
        return super.writeBytes(byteBuf.slice());
    }

    @Override
    public FriendlyByteBuf writeBytes(ByteBuf byteBuf, int i) {
        return super.writeBytes(byteBuf.slice(), i);
    }

    @Override
    public FriendlyByteBuf setBytes(int i, ByteBuf byteBuf) {
        return super.setBytes(i, byteBuf.slice());
    }

    @Override
    public FriendlyByteBuf setBytes(int i, ByteBuf byteBuf, int j) {
        return super.setBytes(i, byteBuf.slice(), j);
    }
}
