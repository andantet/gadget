package io.wispforest.gadget.util;

import blue.endless.jankson.Jankson;
import io.wispforest.owo.config.ConfigWrapper;
import io.wispforest.owo.config.ConfigWrapper.BuilderConsumer;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.util.Observable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class GadgetConfig extends ConfigWrapper<io.wispforest.gadget.util.GadgetConfigModel> {

    public final Keys keys = new Keys();

    private final Option<java.lang.Boolean> menuButtonEnabled = this.optionForKey(this.keys.menuButtonEnabled);
    private final Option<java.lang.Boolean> rightClickDump = this.optionForKey(this.keys.rightClickDump);
    private final Option<java.lang.Boolean> dropChunkData = this.optionForKey(this.keys.dropChunkData);
    private final Option<io.wispforest.gadget.util.GadgetConfigModel.DumpSafetyMode> dumpSafety = this.optionForKey(this.keys.dumpSafety);
    private final Option<java.lang.Boolean> debugKeysInScreens = this.optionForKey(this.keys.debugKeysInScreens);
    private final Option<java.lang.Boolean> matrixStackDebugging = this.optionForKey(this.keys.matrixStackDebugging);
    private final Option<java.lang.Boolean> uiInspector = this.optionForKey(this.keys.uiInspector);
    private final Option<java.lang.Boolean> silenceStartupErrors = this.optionForKey(this.keys.silenceStartupErrors);
    private final Option<java.lang.String> quiltflowerVersion = this.optionForKey(this.keys.quiltflowerVersion);
    private final Option<io.wispforest.gadget.util.GadgetConfigModel.MappingsType> mappings = this.optionForKey(this.keys.mappings);
    private final Option<io.wispforest.gadget.util.GadgetConfigModel.UICounterMode> uiCounterMode = this.optionForKey(this.keys.uiCounterMode);
    private final Option<java.lang.Boolean> inspectClasses = this.optionForKey(this.keys.inspectClasses);
    private final Option<java.lang.Boolean> fullDecompilationContext = this.optionForKey(this.keys.fullDecompilationContext);
    private final Option<java.util.List<java.lang.String>> hiddenFields = this.optionForKey(this.keys.hiddenFields);
    private final Option<java.lang.Boolean> errorCheckOwoUi = this.optionForKey(this.keys.errorCheckOwoUi);
    private final Option<java.lang.Boolean> internalSettings_debugMatrixStackDebugging = this.optionForKey(this.keys.internalSettings_debugMatrixStackDebugging);
    private final Option<java.lang.Boolean> internalSettings_injectMatrixStackErrors = this.optionForKey(this.keys.internalSettings_injectMatrixStackErrors);
    private final Option<java.lang.Boolean> internalSettings_dumpTRMappings = this.optionForKey(this.keys.internalSettings_dumpTRMappings);
    private final Option<java.lang.Boolean> internalSettings_dumpFieldDataRequests = this.optionForKey(this.keys.internalSettings_dumpFieldDataRequests);

    private GadgetConfig() {
        super(io.wispforest.gadget.util.GadgetConfigModel.class);
    }

    private GadgetConfig(BuilderConsumer consumer) {
        super(io.wispforest.gadget.util.GadgetConfigModel.class, consumer);
    }

    public static GadgetConfig createAndLoad() {
        var wrapper = new GadgetConfig();
        wrapper.load();
        return wrapper;
    }

    public static GadgetConfig createAndLoad(BuilderConsumer consumer) {
        var wrapper = new GadgetConfig(consumer);
        wrapper.load();
        return wrapper;
    }

    public boolean menuButtonEnabled() {
        return menuButtonEnabled.value();
    }

    public void menuButtonEnabled(boolean value) {
        menuButtonEnabled.set(value);
    }

    public boolean rightClickDump() {
        return rightClickDump.value();
    }

    public void rightClickDump(boolean value) {
        rightClickDump.set(value);
    }

    public boolean dropChunkData() {
        return dropChunkData.value();
    }

    public void dropChunkData(boolean value) {
        dropChunkData.set(value);
    }

    public io.wispforest.gadget.util.GadgetConfigModel.DumpSafetyMode dumpSafety() {
        return dumpSafety.value();
    }

    public void dumpSafety(io.wispforest.gadget.util.GadgetConfigModel.DumpSafetyMode value) {
        dumpSafety.set(value);
    }

    public boolean debugKeysInScreens() {
        return debugKeysInScreens.value();
    }

    public void debugKeysInScreens(boolean value) {
        debugKeysInScreens.set(value);
    }

    public boolean matrixStackDebugging() {
        return matrixStackDebugging.value();
    }

    public void matrixStackDebugging(boolean value) {
        matrixStackDebugging.set(value);
    }

    public boolean uiInspector() {
        return uiInspector.value();
    }

    public void uiInspector(boolean value) {
        uiInspector.set(value);
    }

    public boolean silenceStartupErrors() {
        return silenceStartupErrors.value();
    }

    public void silenceStartupErrors(boolean value) {
        silenceStartupErrors.set(value);
    }

    public java.lang.String quiltflowerVersion() {
        return quiltflowerVersion.value();
    }

    public void quiltflowerVersion(java.lang.String value) {
        quiltflowerVersion.set(value);
    }

    public io.wispforest.gadget.util.GadgetConfigModel.MappingsType mappings() {
        return mappings.value();
    }

    public void mappings(io.wispforest.gadget.util.GadgetConfigModel.MappingsType value) {
        mappings.set(value);
    }

    public void subscribeToMappings(Consumer<io.wispforest.gadget.util.GadgetConfigModel.MappingsType> subscriber) {
        mappings.observe(subscriber);
    }

    public io.wispforest.gadget.util.GadgetConfigModel.UICounterMode uiCounterMode() {
        return uiCounterMode.value();
    }

    public void uiCounterMode(io.wispforest.gadget.util.GadgetConfigModel.UICounterMode value) {
        uiCounterMode.set(value);
    }

    public boolean inspectClasses() {
        return inspectClasses.value();
    }

    public void inspectClasses(boolean value) {
        inspectClasses.set(value);
    }

    public boolean fullDecompilationContext() {
        return fullDecompilationContext.value();
    }

    public void fullDecompilationContext(boolean value) {
        fullDecompilationContext.set(value);
    }

    public java.util.List<java.lang.String> hiddenFields() {
        return hiddenFields.value();
    }

    public void hiddenFields(java.util.List<java.lang.String> value) {
        hiddenFields.set(value);
    }

    public void subscribeToHiddenFields(Consumer<java.util.List<java.lang.String>> subscriber) {
        hiddenFields.observe(subscriber);
    }

    public boolean errorCheckOwoUi() {
        return errorCheckOwoUi.value();
    }

    public void errorCheckOwoUi(boolean value) {
        errorCheckOwoUi.set(value);
    }

    public final InternalSettings_ internalSettings = new InternalSettings_();
    public class InternalSettings_ implements InternalSettings {
        public boolean debugMatrixStackDebugging() {
            return internalSettings_debugMatrixStackDebugging.value();
        }

        public void debugMatrixStackDebugging(boolean value) {
            internalSettings_debugMatrixStackDebugging.set(value);
        }

        public boolean injectMatrixStackErrors() {
            return internalSettings_injectMatrixStackErrors.value();
        }

        public void injectMatrixStackErrors(boolean value) {
            internalSettings_injectMatrixStackErrors.set(value);
        }

        public boolean dumpTRMappings() {
            return internalSettings_dumpTRMappings.value();
        }

        public void dumpTRMappings(boolean value) {
            internalSettings_dumpTRMappings.set(value);
        }

        public boolean dumpFieldDataRequests() {
            return internalSettings_dumpFieldDataRequests.value();
        }

        public void dumpFieldDataRequests(boolean value) {
            internalSettings_dumpFieldDataRequests.set(value);
        }

    }
    public interface InternalSettings {
        boolean debugMatrixStackDebugging();
        void debugMatrixStackDebugging(boolean value);
        boolean injectMatrixStackErrors();
        void injectMatrixStackErrors(boolean value);
        boolean dumpTRMappings();
        void dumpTRMappings(boolean value);
        boolean dumpFieldDataRequests();
        void dumpFieldDataRequests(boolean value);
    }
    public static class Keys {
        public final Option.Key menuButtonEnabled = new Option.Key("menuButtonEnabled");
        public final Option.Key rightClickDump = new Option.Key("rightClickDump");
        public final Option.Key dropChunkData = new Option.Key("dropChunkData");
        public final Option.Key dumpSafety = new Option.Key("dumpSafety");
        public final Option.Key debugKeysInScreens = new Option.Key("debugKeysInScreens");
        public final Option.Key matrixStackDebugging = new Option.Key("matrixStackDebugging");
        public final Option.Key uiInspector = new Option.Key("uiInspector");
        public final Option.Key silenceStartupErrors = new Option.Key("silenceStartupErrors");
        public final Option.Key quiltflowerVersion = new Option.Key("quiltflowerVersion");
        public final Option.Key mappings = new Option.Key("mappings");
        public final Option.Key uiCounterMode = new Option.Key("uiCounterMode");
        public final Option.Key inspectClasses = new Option.Key("inspectClasses");
        public final Option.Key fullDecompilationContext = new Option.Key("fullDecompilationContext");
        public final Option.Key hiddenFields = new Option.Key("hiddenFields");
        public final Option.Key errorCheckOwoUi = new Option.Key("errorCheckOwoUi");
        public final Option.Key internalSettings_debugMatrixStackDebugging = new Option.Key("internalSettings.debugMatrixStackDebugging");
        public final Option.Key internalSettings_injectMatrixStackErrors = new Option.Key("internalSettings.injectMatrixStackErrors");
        public final Option.Key internalSettings_dumpTRMappings = new Option.Key("internalSettings.dumpTRMappings");
        public final Option.Key internalSettings_dumpFieldDataRequests = new Option.Key("internalSettings.dumpFieldDataRequests");
    }
}

