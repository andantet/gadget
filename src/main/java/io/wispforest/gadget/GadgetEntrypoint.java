package io.wispforest.gadget;

public interface GadgetEntrypoint {
    String KEY = "gadget:init";

    /**
     * Invoked on gadget initialization.
     * <p>
     * All gadget-specific common registration should be done here.
     */
    void onGadgetInit();
}
