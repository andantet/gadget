package io.wispforest.gadget.client.nbt;

import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.Arrays;

public record NbtPath(String[] steps) {
    public static final NbtPath EMPTY = new NbtPath(new String[0]);

    public Tag follow(Tag start) {
        for (String element : steps) {
            if (start instanceof CompoundTag compound)
                start = compound.get(element);
            else if (start instanceof CollectionTag<?> list)
                start = list.get(Integer.parseInt(element));
        }

        return start;
    }

    public void set(Tag start, Tag to) {
        for (int i = 0; i < steps.length - 1; i++) {
            if (start instanceof CompoundTag compound)
                start = compound.get(steps[i]);
            else if (start instanceof CollectionTag<?> list)
                start = list.get(Integer.parseInt(steps[i]));
        }

        if (start instanceof CompoundTag compound)
            compound.put(steps[steps.length - 1], to);
        else if (start instanceof CollectionTag<?> list)
            list.setTag(Integer.parseInt(steps[steps.length - 1]), to);
    }

    public void add(Tag start, Tag to) {
        for (int i = 0; i < steps.length - 1; i++) {
            if (start instanceof CompoundTag compound)
                start = compound.get(steps[i]);
            else if (start instanceof CollectionTag<?> list)
                start = list.get(Integer.parseInt(steps[i]));
        }

        if (start instanceof CompoundTag compound)
            compound.put(steps[steps.length - 1], to);
        else if (start instanceof CollectionTag<?> list)
            list.addTag(Integer.parseInt(steps[steps.length - 1]), to);
    }

    public void remove(Tag start) {
        for (int i = 0; i < steps.length - 1; i++) {
            if (start instanceof CompoundTag compound)
                start = compound.get(steps[i]);
            else if (start instanceof CollectionTag<?> list)
                start = list.get(Integer.parseInt(steps[i]));
        }

        if (start instanceof CompoundTag compound)
            compound.remove(steps[steps.length - 1]);
        else if (start instanceof CollectionTag<?> list)
            list.remove(Integer.parseInt(steps[steps.length - 1]));
    }

    public NbtPath parent() {
        String[] newSteps = new String[steps.length - 1];

        System.arraycopy(steps, 0, newSteps, 0, steps.length - 1);

        return new NbtPath(newSteps);
    }

    public NbtPath then(String step) {
        String[] newSteps = new String[steps.length + 1];

        System.arraycopy(steps, 0, newSteps, 0, steps.length);

        newSteps[newSteps.length - 1] = step;

        return new NbtPath(newSteps);
    }

    public String name() {
        return steps[steps.length - 1];
    }

    public boolean startsWith(NbtPath path) {
        if (steps.length < path.steps.length) return false;

        for (int i = 0; i < steps.length && i < path.steps.length; i++) {
            if (!path.steps[i].equals(steps[i]))
                return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return String.join(".", steps);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NbtPath nbtPath = (NbtPath) o;

        return Arrays.equals(steps, nbtPath.steps);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(steps);
    }
}
