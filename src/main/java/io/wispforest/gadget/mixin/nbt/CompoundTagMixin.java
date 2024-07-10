package io.wispforest.gadget.mixin.nbt;

import com.google.common.collect.ForwardingMap;
import io.wispforest.gadget.nbt.LockableNbt;
import io.wispforest.gadget.nbt.LockableNbtInternal;
import io.wispforest.gadget.nbt.NbtLock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(CompoundTag.class)
public class CompoundTagMixin implements LockableNbtInternal {
    @Shadow @Final private Map<String, Tag> tags;
    @Unique private final List<NbtLock> gadget$locks = new ArrayList<>();

    @Override
    public List<NbtLock> gadget$locks() {
        return gadget$locks;
    }

    @Override
    public void lock(NbtLock lock) {
        gadget$locks().add(lock);

        for (var child : tags.entrySet()) {
            if (child.getValue() instanceof LockableNbt lockable) {
                lockable.lock(lock);
            }
        }
    }

    @Override
    public void unlock(NbtLock lock) {
        gadget$locks().remove(lock);

        for (var child : tags.entrySet()) {
            if (child.getValue() instanceof LockableNbt lockable) {
                lockable.unlock(lock);
            }
        }
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "<init>(Ljava/util/Map;)V", at = @At("LOAD"), argsOnly = true)
    private Map<String, Tag> wrapMap(Map<String, Tag> old) {
        return new ForwardingMap<>() {
            @Override
            protected Map<String, Tag> delegate() {
                return old;
            }

            @Override
            public Tag put(String key, Tag value) {
                CompoundTagMixin.this.gadget$checkWrite();
                return super.put(key, value);
            }

            @Override
            public void putAll(Map<? extends String, ? extends Tag> map) {
                CompoundTagMixin.this.gadget$checkWrite();
                super.putAll(map);
            }

            @Override
            public Tag remove(Object key) {
                CompoundTagMixin.this.gadget$checkWrite();
                return super.remove(key);
            }
        };
    }
}
