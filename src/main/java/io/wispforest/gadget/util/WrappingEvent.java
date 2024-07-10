package io.wispforest.gadget.util;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

@SuppressWarnings("NonExtendableApiUsage")
public class WrappingEvent<F, T> extends Event<T> {
    private final Event<F> wrapped;
    private final Function<T, F> wrapper;

    public WrappingEvent(Event<F> wrapped, Function<T, F> wrapper) {
        this.wrapped = wrapped;
        this.wrapper = wrapper;
        this.invoker = null;
    }

    @Override
    public void register(T listener) {
        wrapped.register(wrapper.apply(listener));
    }

    @Override
    public void register(ResourceLocation phase, T listener) {
        wrapped.register(phase, wrapper.apply(listener));
    }

    @Override
    public void addPhaseOrdering(ResourceLocation firstPhase, ResourceLocation secondPhase) {
        wrapped.addPhaseOrdering(firstPhase, secondPhase);
    }
}
