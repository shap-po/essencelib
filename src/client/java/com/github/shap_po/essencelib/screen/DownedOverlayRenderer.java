package com.github.shap_po.essencelib.screen;

import com.github.shap_po.essencelib.component.DownedComponent;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class DownedOverlayRenderer implements HudRenderCallback {
    private static final int EDGE_PIXELS = 36;
    private static final int KILLER_FOCUS_TICKS = 35;

    private static boolean wasDowned = false;
    private static int killerFocusTicksRemaining = 0;
    private static @Nullable UUID killerUuid = null;

    @Override
    public void onHudRender(DrawContext context, net.minecraft.client.render.RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null || !DownedComponent.isDowned(player)) return;

        int w = context.getScaledWindowWidth();
        int h = context.getScaledWindowHeight();

        context.fill(0, 0, w, h, 0x14A00000);
        for (int i = 0; i < EDGE_PIXELS; i++) {
            float t = 1.0f - (i / (float) EDGE_PIXELS);
            int a = Math.max(0, Math.min(255, (int) (120.0f * t)));
            int color = (a << 24) | 0xB00000;
            context.fill(0, i, w, i + 1, color);
            context.fill(0, h - i - 1, w, h - i, color);
            context.fill(i, 0, i + 1, h, color);
            context.fill(w - i - 1, 0, w - i, h, color);
        }
    }

    public static void tick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null || client.world == null) {
            wasDowned = false;
            killerFocusTicksRemaining = 0;
            killerUuid = null;
            return;
        }

        boolean downed = DownedComponent.isDowned(player);
        if (downed && !wasDowned) {
            DownedComponent comp = DownedComponent.getNullable(player);
            killerUuid = comp != null ? comp.getLastDownedKillerUuid() : null;
            killerFocusTicksRemaining = killerUuid != null ? KILLER_FOCUS_TICKS : 0;
        } else if (!downed) {
            killerFocusTicksRemaining = 0;
            killerUuid = null;
        }

        if (downed && killerFocusTicksRemaining > 0) {
            lookAtKiller(client, player);
            killerFocusTicksRemaining--;
        }

        wasDowned = downed;
    }

    public static boolean isKillerFocusActive() {
        return killerFocusTicksRemaining > 0;
    }

    private static void lookAtKiller(MinecraftClient client, ClientPlayerEntity player) {
        if (killerUuid == null || client.world == null) return;
        Entity killer = client.world.getEntitiesByClass(
            Entity.class,
            player.getBoundingBox().expand(128.0D),
            e -> killerUuid.equals(e.getUuid()) && e.isAlive()
        ).stream().findFirst().orElse(null);
        if (killer == null) return;

        Vec3d from = player.getCameraPosVec(1.0f);
        Vec3d to = killer.getPos().add(0.0D, killer.getStandingEyeHeight() * 0.7D, 0.0D);
        Vec3d delta = to.subtract(from);
        double xz = Math.sqrt(delta.x * delta.x + delta.z * delta.z);
        if (xz < 1.0E-4) return;

        float targetYaw = (float) (Math.toDegrees(Math.atan2(delta.z, delta.x)) - 90.0D);
        float targetPitch = (float) (-Math.toDegrees(Math.atan2(delta.y, xz)));

        float newYaw = MathHelper.lerpAngleDegrees(0.35f, player.getYaw(), targetYaw);
        float newPitch = MathHelper.lerp(0.35f, player.getPitch(), targetPitch);
        player.setYaw(newYaw);
        player.setPitch(newPitch);
        player.setHeadYaw(newYaw);
    }
}
