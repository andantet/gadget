package io.wispforest.gadget.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.wispforest.gadget.Gadget;
import io.wispforest.gadget.util.ReflectionUtil;
import net.minecraft.client.gui.screens.Screen;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public final class PoseStackLogger {
    private static volatile int ERROR_MODE = 0;
    private static final Map<PoseStack, StringBuilder> LOGS = Collections.synchronizedMap(new WeakHashMap<>());

    private PoseStackLogger() {

    }

    public static void logOp(PoseStack stack, boolean push, int indent) {
        if (ERROR_MODE != 2 && !(Gadget.CONFIG.internalSettings.debugMatrixStackDebugging() && Screen.hasShiftDown())) return;

        var log = LOGS.computeIfAbsent(stack, unused -> new StringBuilder());

        log.append(" ".repeat(indent));
        log.append(push ? "> " : "< ").append(ReflectionUtil.getCallingMethodData(4));
        log.append("\n");
    }

    public static boolean tripError(PoseStack stack, String message) {
        if (!Gadget.CONFIG.matrixStackDebugging()) return false;

        switch (ERROR_MODE) {
            case 0 -> ERROR_MODE = 1;
            case 2 -> {
                Gadget.LOGGER.error("Push/pop log of matrix stack:\n{}", LOGS.getOrDefault(stack, new StringBuilder()));
                throw new IllegalStateException(message);
            }
        }

        return true;
    }

    public static void startLoggingIfNeeded() {
        if (Gadget.CONFIG.internalSettings.debugMatrixStackDebugging() && Screen.hasShiftDown()) {
            for (var log : LOGS.values()) {
                Gadget.LOGGER.error("log:\n{}", log);
            }
        }

        if (ERROR_MODE == 1)
            ERROR_MODE = 2;

        LOGS.clear();
    }
}
