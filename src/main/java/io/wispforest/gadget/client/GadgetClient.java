package io.wispforest.gadget.client;

import io.wispforest.gadget.Gadget;
import io.wispforest.gadget.client.command.ChatLogCommand;
import io.wispforest.gadget.client.command.ReloadMappingsCommand;
import io.wispforest.gadget.client.config.GadgetConfigScreen;
import io.wispforest.gadget.client.dump.ClientPacketDumper;
import io.wispforest.gadget.client.dump.handler.ClientPacketHandlers;
import io.wispforest.gadget.client.field.FieldDataScreen;
import io.wispforest.gadget.client.field.RemoteFieldDataSource;
import io.wispforest.gadget.client.gui.ContextMenuScreens;
import io.wispforest.gadget.client.gui.GadgetScreen;
import io.wispforest.gadget.client.gui.inspector.UIInspector;
import io.wispforest.gadget.client.log.ChatLogAppender;
import io.wispforest.gadget.client.nbt.StackNbtDataScreen;
import io.wispforest.gadget.client.resource.ViewResourcesScreen;
import io.wispforest.gadget.mappings.MappingsManager;
import io.wispforest.gadget.mixin.client.AbstractContainerScreenAccessor;
import io.wispforest.gadget.network.BlockEntityTarget;
import io.wispforest.gadget.network.EntityTarget;
import io.wispforest.gadget.network.GadgetNetworking;
import io.wispforest.gadget.network.InspectionTarget;
import io.wispforest.gadget.network.packet.c2s.OpenFieldDataScreenC2SPacket;
import io.wispforest.gadget.network.packet.c2s.RequestResourceC2SPacket;
import io.wispforest.gadget.network.packet.s2c.*;
import io.wispforest.owo.config.ui.ConfigScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.layers.Layer;
import io.wispforest.owo.ui.layers.Layers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.lwjgl.glfw.GLFW;

import java.io.ByteArrayInputStream;
import java.util.List;

public class GadgetClient implements ClientModInitializer {
    public static final KeyMapping INSPECT_KEY = new KeyMapping("key.gadget.inspect", GLFW.GLFW_KEY_I, KeyMapping.CATEGORY_MISC);
    public static final KeyMapping DUMP_KEY = new KeyMapping("key.gadget.dump", GLFW.GLFW_KEY_K, KeyMapping.CATEGORY_MISC);

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(INSPECT_KEY);
        KeyBindingHelper.registerKeyBinding(DUMP_KEY);

        ClientPacketHandlers.init();
        UIInspector.init();
        ServerData.init();
        ContextMenuScreens.init();
        ChatLogAppender.init();

        ConfigScreen.registerProvider("gadget", GadgetConfigScreen::new);

        GadgetNetworking.CHANNEL.registerClientbound(OpenFieldDataScreenS2CPacket.class, (packet, access) -> {
            access.runtime().setScreen(new FieldDataScreen(
                packet.target(),
                false,
                true, packet.rootData(),
                packet.rootChildren()
            ));
        });

        GadgetNetworking.CHANNEL.registerClientbound(FieldDataResponseS2CPacket.class, (packet, access) -> {
            if (access.runtime().screen instanceof FieldDataScreen gui
                && gui.target().equals(packet.target())
                && gui.dataSource() instanceof RemoteFieldDataSource remote) {
                remote.acceptPacket(packet);
            }
        });

        GadgetNetworking.CHANNEL.registerClientbound(FieldDataErrorS2CPacket.class, (packet, access) -> {
            if (access.runtime().screen instanceof FieldDataScreen gui
                && gui.target().equals(packet.target())
                && gui.dataSource() instanceof RemoteFieldDataSource remote) {
                remote.acceptPacket(packet);
            }
        });

        GadgetNetworking.CHANNEL.registerClientbound(ResourceListS2CPacket.class, (packet, access) -> {
            var screen = new ViewResourcesScreen(access.runtime().screen, packet.resources());

            screen.resRequester(
                (id, idx) -> GadgetNetworking.CHANNEL.clientHandle().send(new RequestResourceC2SPacket(id, idx)));

            access.runtime().setScreen(screen);
        });

        GadgetNetworking.CHANNEL.registerClientbound(ResourceDataS2CPacket.class, (packet, access) -> {
            if (!(access.runtime().screen instanceof ViewResourcesScreen screen))
                return;

            screen.openFile(packet.id(), () -> new ByteArrayInputStream(packet.data()));
        });

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (client.getOverlay() == null) {
                MappingsManager.init();
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!INSPECT_KEY.consumeClick()) return;

            InspectionTarget target;

            if (!client.options.getCameraType().isFirstPerson()
             && client.player != null) {
                target = new EntityTarget(client.player.getId());
            } else {
                Entity camera = client.getCameraEntity();
                if (camera == null) camera = client.player;

                HitResult hitResult = raycast(camera, client.getFrameTime());

                if (hitResult == null) return;

                if (hitResult instanceof EntityHitResult ehr) {
                    target = new EntityTarget(ehr.getEntity().getId());
                } else {
                    BlockPos blockPos = hitResult instanceof BlockHitResult blockHitResult
                        ? blockHitResult.getBlockPos()
                        : BlockPos.containing(hitResult.getLocation());

                    target = new BlockEntityTarget(blockPos);
                }
            }

            if (!GadgetNetworking.CHANNEL.canSendToServer()) {
                if (target.resolve(client.level) == null) {
                    client.player.displayClientMessage(Component.translatable("message.gadget.fail.notfound"), true);
                    return;
                }

                client.setScreen(new FieldDataScreen(
                    target,
                    true,
                    false,
                    null,
                    null
                ));
            } else {
                GadgetNetworking.CHANNEL.clientHandle().send(new OpenFieldDataScreenC2SPacket(target));
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!DUMP_KEY.consumeClick()) return;

            if (ClientPacketDumper.isDumping()) {
                ClientPacketDumper.stop();
            } else {
                ClientPacketDumper.start(true);
            }
        });

        List<String> alignToButtons = List.of(
            "menu.multiplayer",
            "menu.shareToLan",
            "menu.playerReporting"
        );

        Layers.add(Containers::verticalFlow, instance -> {
            if (!Gadget.CONFIG.menuButtonEnabled()) return;

            instance.adapter.rootComponent.child(
                Components.button(
                    Component.translatable("text.gadget.menu_button"),
                    button -> Minecraft.getInstance().setScreen(new GadgetScreen(instance.screen))
                ).<Button>configure(button -> {
                    button.margins(Insets.left(4)).sizing(Sizing.fixed(20));
                    instance.alignComponentToWidget(widget -> {
                        if (!(widget instanceof Button daButton)) return false;
                        return daButton.getMessage().getContents() instanceof TranslatableContents translatable
                            && alignToButtons.contains(translatable.getKey());
                    }, Layer.Instance.AnchorSide.RIGHT, 0, button);
                })
            );
        }, TitleScreen.class, PauseScreen.class);

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof AbstractContainerScreen<?> handled)
                ScreenKeyboardEvents.allowKeyPress(screen).register((screen1, key, scancode, modifiers) -> {
                    if (!INSPECT_KEY.matches(key, scancode)) return true;

                    double mouseX = client.mouseHandler.xpos()
                        * (double)client.getWindow().getGuiScaledWidth() / (double)client.getWindow().getScreenWidth();
                    double mouseY = client.mouseHandler.ypos()
                        * (double)client.getWindow().getGuiScaledHeight() / (double)client.getWindow().getScreenHeight();
                    var slot = ((AbstractContainerScreenAccessor) handled).callFindSlot(mouseX, mouseY);

                    if (slot == null) return true;
                    if (slot instanceof CreativeModeInventoryScreen.CustomCreativeSlot) return true;
                    if (slot.getItem().isEmpty()) return true;

                    client.setScreen(new StackNbtDataScreen(handled, slot));

                    return false;
                });

            ScreenKeyboardEvents.allowKeyPress(screen).register((screen1, key, scancode, modifiers) -> {
                if (!Screen.hasShiftDown()) return true;
                if (!INSPECT_KEY.matches(key, scancode)) return true;

                UIInspector.dumpWidgetTree(screen1);

                return false;
            });
        });

        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            if (Gadget.CONFIG.nonNullEmptyNbtTooltip()
             && stack.getTag() != null
             && stack.getTag().isEmpty()) {
                lines.add(Component.translatable("text.gadget.nonNullEmptyNbt"));
            }
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            ReloadMappingsCommand.register(dispatcher);
            ChatLogCommand.register(dispatcher);
        });

        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            if (Gadget.CONFIG.internalSettings.injectMatrixStackErrors() && Screen.hasShiftDown()) {
                context.matrixStack().popPose();
            }
        });

        for (EntrypointContainer<GadgetClientEntrypoint> container : FabricLoader.getInstance().getEntrypointContainers("gadget:client_init", GadgetClientEntrypoint.class)) {
            try {
                container.getEntrypoint().onGadgetClientInit();
            } catch (Exception e) {
                Gadget.LOGGER.error("{}'s `gadget:client_init` entrypoint handler threw an exception",
                    container.getProvider().getMetadata().getId(), e);
            }
        }
    }

    // 100% not stolen from owo-whats-this
    // https://github.com/wisp-forest/owo-whats-this/blob/master/src/main/java/io/wispforest/owowhatsthis/OwoWhatsThis.java#L155-L171.
    public static HitResult raycast(Entity entity, float tickDelta) {
        var blockTarget = entity.pick(5, tickDelta, false);

        var maxReach = entity.getViewVector(tickDelta).scale(5);
        var entityTarget = ProjectileUtil.getEntityHitResult(
            entity,
            entity.getEyePosition(),
            entity.getEyePosition().add(maxReach),
            entity.getBoundingBox().expandTowards(maxReach),
            candidate -> true,
            5 * 5
        );

        return entityTarget != null && entityTarget.distanceTo(entity) < blockTarget.distanceTo(entity)
            ? entityTarget
            : blockTarget;
    }
}
