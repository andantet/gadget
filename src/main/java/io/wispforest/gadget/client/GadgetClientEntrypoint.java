package io.wispforest.gadget.client;

public interface GadgetClientEntrypoint {
    String KEY = "gadget:client_init";

    /**
     * Invoked on gadget client initialization.
     * <p>
     * All gadget-specific client registration should be done here.
     */
    void onGadgetClientInit();
}
