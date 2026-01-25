package com.fabledruns.korenet;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

public class KorenetHud {

    public static void renderHud(DrawContext context, RenderTickCounter tickCounter) {
        if (KorenetClient.CONFIG == null) return;
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

        String pingText;
        String jitterText;
        boolean hasPingValue = false;

        if (demoMode) {
            pingText = "Ping: 42ms";
            jitterText = "Jitter: 7ms";
            hasPingValue = true;
        } else if (mc.player == null || mc.getNetworkHandler() == null) {
            pingText = "Ping: N/A";
            jitterText = "Jitter: N/A";
        } else if (mc.isInSingleplayer()) {
            pingText = "Ping: N/A (SP)";
            jitterText = "Jitter: N/A";
        } else {
            pingText = ping < 0 ? "Ping: --" : "Ping: " + ping + "ms";
            jitterText = "Jitter: " + jitter + "ms";
            hasPingValue = ping >= 0;
        }

        int screenWidth = mc.getWindow().getScaledWidth();
        int screenHeight = mc.getWindow().getScaledHeight();
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;

        int x = centerX + KorenetClient.CONFIG.hudX;
        int y = centerY + KorenetClient.CONFIG.hudY;

        int lineHeight = mc.textRenderer.fontHeight + 2;
        int textWidth = Math.max(mc.textRenderer.getWidth(pingText), mc.textRenderer.getWidth(jitterText));
        int paddingX = 6;
        int paddingY = 4;
        int bgWidth = textWidth + paddingX * 2;
        int bgHeight = lineHeight * 2 + paddingY * 2;

        float alpha = KorenetClient.CONFIG.hudOpacity;
        if (alpha <= 0.05f) return;

        int alphaInt = (int) (alpha * 255);
        int pingBaseColor;
        if (hasPingValue) {
            int effectivePing = demoMode ? 42 : ping;
            if (effectivePing < 75) {
                pingBaseColor = 0x55FF55;
            } else if (effectivePing <= 160) {
                pingBaseColor = 0xFFFF55;
            } else {
                pingBaseColor = 0xFF5555;
            }
        } else {
            pingBaseColor = 0xFFFFFF;
        }
        int pingColor = (pingBaseColor & 0x00FFFFFF) | (alphaInt << 24);
        int jitterColor = (0xAAAAAA & 0x00FFFFFF) | (alphaInt << 24);
        int bgAlpha = (int) (alpha * 0x80);
        int bgColor = (bgAlpha << 24) | 0x000000;

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

        context.drawText(mc.textRenderer, Text.literal(pingText), boxX + paddingX, boxY + paddingY, pingColor, true);
        context.drawText(mc.textRenderer, Text.literal(jitterText), boxX + paddingX, boxY + paddingY + lineHeight, jitterColor, true);

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
}
