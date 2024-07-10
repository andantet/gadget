package io.wispforest.gadget.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

public final class PrettyPrinters {
    private static final Map<Class<?>, Function<Object, String>> PRINTERS = new HashMap<>();

    private PrettyPrinters() {

    }

    public static String tryPrint(Object o) {
        if (o == null)
            return "null";

        var printer = ReflectionUtil.findFor(o.getClass(), PRINTERS);

        if (printer != null)
            return printer.apply(o);
        else
            return null;
    }

    @SuppressWarnings("unchecked")
    @SafeVarargs
    public static <T> void register(Function<T, String> printer, Class<? extends T>... classes) {
        for (Class<?> klass : classes) {
            PRINTERS.put(klass, (Function<Object, String>) printer);
        }
    }

    static {
        register(Object::toString,
            // Standard library classes
            Boolean.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class,
            Character.class, Class.class, UUID.class,

            AtomicBoolean.class, AtomicInteger.class, AtomicLong.class,

            // Minecraft classes
            BlockState.class, FluidState.class, Level.class, ResourceLocation.class);

        register(x -> "\"" + x + "\"", String.class);

        register(x -> "MinecraftServer", MinecraftServer.class);

        register(x -> BuiltInRegistries.ITEM.getKey(x).toString(), Item.class);
        register(x -> BuiltInRegistries.BLOCK.getKey(x).toString(), Block.class);
        register(x -> BuiltInRegistries.ENTITY_TYPE.getKey(x).toString(), EntityType.class);
        register(x -> Objects.toString(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(x), x.toString()), BlockEntityType.class);
        register(x -> Objects.toString(BuiltInRegistries.MOB_EFFECT.getKey(x), x.toString()), MobEffect.class);
    }
}
