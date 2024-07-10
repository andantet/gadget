package io.wispforest.gadget.client.gui.inspector;

import io.wispforest.gadget.mixin.client.AbstractSelectionListAccessor;
import io.wispforest.gadget.mixin.client.AbstractSelectionListEntryMixin;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractWidget;

public final class VanillaSupport {
    private VanillaSupport() {

    }

    public static void init() {
        ElementUtils.registerRootLister((screen, list) -> list.add(screen));

        ElementUtils.registerElementSupport(AbstractWidget.class, ElementSupport.fromLambda(
            AbstractWidget::getX,
            AbstractWidget::getY,
            AbstractWidget::getWidth,
            AbstractWidget::getHeight
        ));

        ElementUtils.registerElementSupport(AbstractSelectionListAccessor.class, ElementSupport.fromLambda(
            w -> ((AbstractWidget) w).getX(),
            w -> ((AbstractWidget) w).getY(),
            w -> ((AbstractWidget) w).getWidth(),
            w -> ((AbstractWidget) w).getHeight()
        ));

        ElementUtils.registerElementSupport(AbstractSelectionList.Entry.class, ElementSupport.fromLambda(
            w -> {
                var list = ((AbstractSelectionListEntryMixin) w).getList();
                return list.getRowLeft();
            },
            w -> {
                var list = ((AbstractSelectionListEntryMixin) w).getList();
                return ((AbstractSelectionListAccessor) list).callGetRowTop(list.children().indexOf(w));
            },
            w -> {
                var list = ((AbstractSelectionListEntryMixin) w).getList();
                return list.getRowWidth();
            },
            w -> {
                var list = ((AbstractSelectionListEntryMixin) w).getList();
                return ((AbstractSelectionListAccessor) list).getItemHeight();
            }
        ));
    }
}
