package com.fabledruns.korenet.desync.hud;

import com.fabledruns.korenet.desync.config.DesyncHudConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class DesyncHudRenderer {

    private static DesyncStatus currentStatus = DesyncStatus.SYNCED;
    private static long lastUpdateTime = 0;
    private static final int DISPLAY_DURATION = 3000;

    @SuppressWarnings("deprecation")
    public static void init() {
        HudRenderCallback.EVENT.register(DesyncHudRenderer::renderHud);
    }

    public static void updateStatus(DesyncStatus status) {
        currentStatus = status;
        lastUpdateTime = System.currentTimeMillis();
    }

    private static void renderHud(DrawContext context, RenderTickCounter tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.options.hudHidden) return;

        boolean isRecent = System.currentTimeMillis() - lastUpdateTime < DISPLAY_DURATION;
        boolean shouldRender = !DesyncHudConfig.autoHide || (currentStatus != DesyncStatus.SYNCED) || isRecent;

        if (!shouldRender) return;

        renderHudContent(context, client);
    }

    public static void renderHudDemo(DrawContext context) {
        renderHudContent(context, MinecraftClient.getInstance());
    }

    private static void renderHudContent(DrawContext context, MinecraftClient client) {
        String message = currentStatus.getMessage();
        int color = currentStatus.getColor();

        TextRenderer font = client.textRenderer;
        int textWidth = font.getWidth(message);
        int textHeight = font.fontHeight;

        int paddingX = 6;
        int paddingY = 4;
        int bgWidth = textWidth + paddingX * 2;
        int bgHeight = textHeight + paddingY * 2;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;

        int x = centerX + DesyncHudConfig.x;
        int y = centerY + DesyncHudConfig.y;

        int boxX = x - bgWidth / 2;
        int boxY = y - bgHeight / 2;

        float alpha = DesyncHudConfig.opacity;
        if (alpha <= 0.05f) return;

        int alphaInt = (int) (alpha * 255);
        int colorWithAlpha = (color & 0x00FFFFFF) | (alphaInt << 24);
        int bgAlpha = (int) (alpha * 0x80);
        int bgColor = (bgAlpha << 24) | 0x000000;

        Object matrices = context.getMatrices();
        pushMatrix(matrices);
        translateMatrix(matrices, (float) x, (float) y, 0.0f);
        scaleMatrix(matrices, DesyncHudConfig.scale, DesyncHudConfig.scale, 1.0f);
        translateMatrix(matrices, -(float) x, -(float) y, 0.0f);

        if (DesyncHudConfig.showBackground) {
            context.fill(boxX, boxY, boxX + bgWidth, boxY + bgHeight, bgColor);
        }

        context.drawText(font, Text.literal(message), boxX + paddingX, boxY + paddingY, colorWithAlpha, true);

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

    public static boolean isMouseOverHud(double mouseX, double mouseY) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer font = client.textRenderer;

        String message = currentStatus.getMessage();
        int textWidth = font.getWidth(message);
        int textHeight = font.fontHeight;

        int bgWidth = (int) ((textWidth + 12) * DesyncHudConfig.scale);
        int bgHeight = (int) ((textHeight + 8) * DesyncHudConfig.scale);

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        int x = screenWidth / 2 + DesyncHudConfig.x;
        int y = screenHeight / 2 + DesyncHudConfig.y;

        int boxX = x - bgWidth / 2;
        int boxY = y - bgHeight / 2;

        return mouseX >= boxX && mouseX <= boxX + bgWidth && mouseY >= boxY && mouseY <= boxY + bgHeight;
    }
}
