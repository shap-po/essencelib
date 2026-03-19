// InventoryScreenMixin.java
package com.github.shap_po.essencelib.mixin.client;

import com.github.shap_po.essencelib.EssenceLibClient;
import com.github.shap_po.essencelib.registry.ModDataComponentTypes;
import com.github.shap_po.essencelib.screen.EssenceResourceHudRenderer;
import com.github.shap_po.essencelib.screen.LevelInfoRenderer;
import com.github.shap_po.essencelib.screen.LevelProgressScreen;
import com.github.shap_po.essencelib.util.ActiveEssenceHelper;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void essencelib$render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        InventoryScreen screen = (InventoryScreen) (Object) this;
        int x = ((HandledScreenAccessor) screen).getX();
        int y = ((HandledScreenAccessor) screen).getY();
        LevelInfoRenderer.renderInInventory(context, x, y, mouseX, mouseY);
        EssenceResourceHudRenderer.renderInventoryEditorHint(
            context,
            screen.width,
            screen.height
        );
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void essencelib$onBadgeClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (EssenceResourceHudRenderer.onInventoryMouseClicked(mouseX, mouseY, button)) {
            cir.setReturnValue(true);
            return;
        }
        if (button != 0) return; // left-click only
        InventoryScreen screen = (InventoryScreen) (Object) this;
        int x = ((HandledScreenAccessor) screen).getX();
        int y = ((HandledScreenAccessor) screen).getY();
        if (LevelInfoRenderer.isOverBadge(x, y, (int) mouseX, (int) mouseY)) {
            var client = net.minecraft.client.MinecraftClient.getInstance();
            if (client.player != null && !client.player.isCreative()) {
                client.setScreen(new LevelProgressScreen(screen));
                cir.setReturnValue(true);
            }
        }
    }

    private static int getEssenceSlotIndexForStack(net.minecraft.entity.player.PlayerEntity player, ItemStack stack) {
        if (stack.isEmpty()) return -1;
        Identifier essenceId = stack.get(ModDataComponentTypes.ESSENCE_ID);
        if (essenceId == null) return -1;
        var opt = TrinketsApi.getTrinketComponent(player);
        if (opt.isEmpty()) return -1;
        var essenceInv = opt.get().getInventory().getOrDefault("soul", null);
        if (essenceInv == null) return -1;
        var inv = essenceInv.getOrDefault("essence", null);
        if (inv == null) return -1;
        for (int i = 0; i < inv.size(); i++) {
            ItemStack s = inv.getStack(i);
            if (!s.isEmpty() && essenceId.equals(s.get(ModDataComponentTypes.ESSENCE_ID))) {
                return i;
            }
        }
        return -1;
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void essencelib$onShiftRightClickSlot(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (button != 1) return; // right-click only
        if (!net.minecraft.client.gui.screen.Screen.hasShiftDown()) return;

        InventoryScreen screen = (InventoryScreen) (Object) this;
        Slot slot = ((HandledScreenInvoker) screen).invokerGetSlotAt(mouseX, mouseY);
        if (slot == null) return;

        ItemStack stack = slot.getStack();
        if (!ActiveEssenceHelper.hasActivePower(stack)) return;

        var client = net.minecraft.client.MinecraftClient.getInstance();
        if (client.player == null || client.currentScreen != screen) return;

        int essenceSlot = getEssenceSlotIndexForStack(client.player, stack);
        if (essenceSlot < 0 || essenceSlot >= EssenceLibClient.KEY_BINDINGS.size()) return;

        KeyBinding kb = EssenceLibClient.KEY_BINDINGS.get(essenceSlot);
        client.setScreen(new com.github.shap_po.essencelib.screen.KeybindSetScreen(screen, essenceSlot, kb));
        cir.setReturnValue(true);
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    private void essencelib$onHudReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (EssenceResourceHudRenderer.onInventoryMouseReleased(button)) {
            cir.setReturnValue(true);
        }
    }
}
