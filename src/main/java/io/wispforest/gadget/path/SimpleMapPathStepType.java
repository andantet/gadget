package io.wispforest.gadget.path;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.wispforest.gadget.util.PrettyPrinters;
import io.wispforest.gadget.util.ReflectionUtil;
import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.endec.ReflectiveEndecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public record SimpleMapPathStepType(Function<String, Object> fromImpl, Function<Object, String> toImpl) implements MapPathStepType {
    private static final BiMap<String, SimpleMapPathStepType> REGISTRY = HashBiMap.create();
    private static final Map<Class<?>, SimpleMapPathStepType> CLASS_TO_TYPE = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> void register(String name, Class<T> klass, Function<String, T> fromImpl, Function<T, String> toImpl) {
        var type = new SimpleMapPathStepType((Function<String, Object>) fromImpl, (Function<Object, String>) toImpl);

        REGISTRY.put(name, type);
        CLASS_TO_TYPE.put(klass, type);
    }

    private static <T> void registerForRegistry(Class<T> klass, Registry<T> registry) {
        register(registry.key().location().toString(), klass, x -> registry.get(new ResourceLocation(x)), x -> registry.getKey(x).toString());
    }

    public static SimpleMapPathStepType getFor(Class<?> klass) {
        return ReflectionUtil.findFor(klass, CLASS_TO_TYPE);
    }

    public static void init() { }

    static {
        register("int", Integer.class, Integer::parseInt, Object::toString);
        register("string", String.class, x -> x, String::toString);
        register("identifier", ResourceLocation.class, ResourceLocation::new, ResourceLocation::toString);

        registerForRegistry(Block.class, BuiltInRegistries.BLOCK);
        registerForRegistry(Item.class, BuiltInRegistries.ITEM);
        registerForRegistry(MobEffect.class, BuiltInRegistries.MOB_EFFECT);

        ReflectiveEndecBuilder.register(Endec.STRING.xmap(REGISTRY::get, REGISTRY.inverse()::get), SimpleMapPathStepType.class);
    }

    @Override
    public Object fromNetwork(String data) {
        return fromImpl.apply(data);
    }

    @Override
    public String toNetwork(Object obj) {
        return toImpl.apply(obj);
    }

    @Override
    public String toPretty(Object obj) {
        return PrettyPrinters.tryPrint(obj);
    }
}
