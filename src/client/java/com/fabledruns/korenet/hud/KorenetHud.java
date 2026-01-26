package com.fabledruns.korenet.hud;

import com.fabledruns.korenet.KorenetClient;
import com.fabledruns.korenet.PingTracker;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

public class KorenetHud {

    public static void renderHud(DrawContext context, RenderTickCounter tickCounter) {
        if ( KorenetClient.CONFIG == null) return;
        if (!KorenetClient.CONFIG.enabled || !KorenetClient.CONFIG.showHud) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options.hudHidden) return;

        renderHudContent(context, mc, false);
    }

    public static void renderHudDemo(DrawContext context) {
        MinecraftClient mc = MinecraftClient.getInstance();
        renderHudContent(context, mc, true);
    }

    private static void renderHudContent(DrawContext context, MinecraftClient mc, boolean demoMode) {
        int ping = PingTracker.getPing();
        int jitter = PingTracker.getJitter();

        HudText hudText = switch (getDisplayMode(mc, demoMode)) {
            case DEMO         -> new HudText("Ping: 42ms", "Jitter: 7ms", true);
            case NO_NETWORK   -> new HudText("Ping: N/A", "Jitter: N/A", false);
            case SINGLEPLAYER -> new HudText("Ping: N/A (SP)", "Jitter: N/A", false);
            case LIVE         -> new HudText(
                ping < 0 ? "Ping: --" : "Ping: " + ping + "ms",
                "Jitter: " + jitter + "ms",
                ping >= 0
            );
        };

        int x = mc.getWindow().getScaledWidth()  / 2 + KorenetClient.CONFIG.hudX;
        int y = mc.getWindow().getScaledHeight() / 2 + KorenetClient.CONFIG.hudY;

        int lineHeight = mc.textRenderer.fontHeight + 2;
        
        int paddingX = 6; 
        int paddingY = 4;

        int bgWidth  = Math.max(mc.textRenderer.getWidth(hudText.pingText()), mc.textRenderer.getWidth(hudText.jitterText())) + paddingX * 2;
        int bgHeight = lineHeight * 2 + paddingY * 2;

        float alpha = KorenetClient.CONFIG.hudOpacity;
        if (alpha <= 0.05f) return;

        int alphaInt = (int) (alpha * 255);
        int pingBaseColor = hudText.showPingValue()
            ? ((demoMode ? 42 : ping) < 75 ? 0x55FF55 : (demoMode ? 42 : ping) <= 160 ? 0xFFFF55 : 0xFF5555)
            : 0xFFFFFF;
        int pingColor = (pingBaseColor & 0x00FFFFFF) | (alphaInt << 24);
        int jitterColor = (0xAAAAAA & 0x00FFFFFF) | (alphaInt << 24);
        int bgColor = ((int) (alpha * 0x80) << 24) | 0x000000;

        Object matrices = context.getMatrices();
        pushMatrix(matrices);
        translateMatrix(matrices, (float) x, (float) y, 0.0f);
        scaleMatrix(matrices, KorenetClient.CONFIG.hudScale, KorenetClient.CONFIG.hudScale, 1.0f);
        translateMatrix(matrices, -(float) x, -(float) y, 0.0f);

        int boxX = x - bgWidth / 2;
        int boxY = y - bgHeight / 2;

        if (KorenetClient.CONFIG.hudShowBackground) {
            context.fill(boxX, boxY, boxX + bgWidth, boxY + bgHeight, bgColor);
        }

        context.drawText(mc.textRenderer, Text.literal(hudText.pingText()), boxX + paddingX, boxY + paddingY, pingColor, true);
        context.drawText(mc.textRenderer, Text.literal(hudText.jitterText()), boxX + paddingX, boxY + paddingY + lineHeight, jitterColor, true);

        popMatrix(matrices);
    }

    private static void pushMatrix(Object matrices) {
        try {
            matrices.getClass().getMethod("push").invoke(matrices);
            return;
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            // Fall through
        }
        try {
            matrices.getClass().getMethod("pushMatrix").invoke(matrices);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to push matrix stack", e);
        }
    }

    private static void popMatrix(Object matrices) {
        try {
            matrices.getClass().getMethod("pop").invoke(matrices);
            return;
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            // Fall through
        }
        try {
            matrices.getClass().getMethod("popMatrix").invoke(matrices);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to pop matrix stack", e);
        }
    }

    private static void translateMatrix(Object matrices, float x, float y, float z) {
        try {
            matrices.getClass().getMethod("translate", float.class, float.class, float.class).invoke(matrices, x, y, z);
            return;
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            // Fall through
        }
        try {
            matrices.getClass().getMethod("translate", float.class, float.class).invoke(matrices, x, y);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to translate matrix stack", e);
        }
    }

    private static void scaleMatrix(Object matrices, float x, float y, float z) {
        try {
            matrices.getClass().getMethod("scale", float.class, float.class, float.class).invoke(matrices, x, y, z);
            return;
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            // Fall through
        }
        try {
            matrices.getClass().getMethod("scale", float.class, float.class).invoke(matrices, x, y);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to scale matrix stack", e);
        }
    }

    @SuppressWarnings("deprecation")
    public static void init() {
        HudRenderCallback.EVENT.register(KorenetHud::renderHud);
    }

    private static HudDisplayMode getDisplayMode(MinecraftClient mc, boolean demoMode) {
        if (demoMode) return HudDisplayMode.DEMO;
        if (mc.player == null || mc.getNetworkHandler() == null) return HudDisplayMode.NO_NETWORK;
        if (mc.isInSingleplayer()) return HudDisplayMode.SINGLEPLAYER;
        return HudDisplayMode.LIVE;
    }

    private enum HudDisplayMode {
        DEMO,
        NO_NETWORK,
        SINGLEPLAYER,
        LIVE
    }

    private record HudText(String pingText, String jitterText, boolean showPingValue) {}
}
