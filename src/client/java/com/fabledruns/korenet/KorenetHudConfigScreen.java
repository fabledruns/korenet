package com.fabledruns.korenet;

import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import java.io.IOException;

public class KorenetHudConfigScreen extends Screen {

    private static final int MIN_INTERVAL_MS = 50;
    private static final int MAX_INTERVAL_MS = 1000;

    private final Screen parent;
    private boolean dragging = false;
    private int dragStartX, dragStartY;
    private int startX, startY;

    public KorenetHudConfigScreen(Screen parent) {
        super(Text.literal("Korenet HUD Editor"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int w = this.width;
        int h = this.height;
        int yStart = h / 2 + 20;

        super.init();

        ScreenMouseEvents.afterMouseClick(this).register((screen, mouseX, mouseY, button) ->
            handleMouseClicked(mouseX, mouseY, button)
        );
        ScreenMouseEvents.afterMouseRelease(this).register((screen, mouseX, mouseY, button) ->
            handleMouseReleased(mouseX, mouseY, button)
        );
        ScreenMouseEvents.afterMouseDrag(this).register((screen, mouseX, mouseY, button, deltaX, deltaY) ->
            handleMouseDragged(mouseX, mouseY, button, deltaX, deltaY)
        );

        this.addDrawableChild(new FloatSliderWidget(w / 2 - 100, yStart, 200, 20,
            Text.literal("Scale: " + String.format("%.2fx", KorenetClient.CONFIG.hudScale)),
            (KorenetClient.CONFIG.hudScale - 0.5f) / 1.5f) {
            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("Scale: " + String.format("%.2fx", KorenetClient.CONFIG.hudScale)));
            }

            @Override
            protected void applyValue() {
                KorenetClient.CONFIG.hudScale = (float) (this.value * 1.5d + 0.5d);
            }
        });

        this.addDrawableChild(new FloatSliderWidget(w / 2 - 100, yStart + 24, 200, 20,
            Text.literal("Opacity: " + String.format("%.0f%%", KorenetClient.CONFIG.hudOpacity * 100)),
            KorenetClient.CONFIG.hudOpacity) {
            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("Opacity: " + String.format("%.0f%%", KorenetClient.CONFIG.hudOpacity * 100)));
            }

            @Override
            protected void applyValue() {
                KorenetClient.CONFIG.hudOpacity = (float) this.value;
            }
        });

        this.addDrawableChild(new FloatSliderWidget(w / 2 - 100, yStart + 48, 200, 20,
            Text.literal("Update Interval: " + KorenetClient.CONFIG.updateIntervalMs + "ms"),
            intervalToSlider(KorenetClient.CONFIG.updateIntervalMs)) {
            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("Update Interval: " + KorenetClient.CONFIG.updateIntervalMs + "ms"));
            }

            @Override
            protected void applyValue() {
                KorenetClient.CONFIG.updateIntervalMs = sliderToInterval(this.value);
            }
        });

        this.addDrawableChild(CyclingButtonWidget.onOffBuilder(KorenetClient.CONFIG.hudShowBackground)
            .build(w / 2 - 100, yStart + 72, 200, 20, Text.literal("Show Background"),
                (button, value) -> KorenetClient.CONFIG.hudShowBackground = value));

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Done"), button -> {
            try {
                ConfigManager.save(KorenetClient.CONFIG);
            } catch (IOException e) {
                KorenetClient.LOGGER.error("Failed to save HUD config", e);
            }
            this.client.setScreen(this.parent);
        }).dimensions(w / 2 - 100, h - 30, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0x80000000);
        KorenetHud.renderHudDemo(context);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Click and drag the HUD to move"), this.width / 2, 20, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    private void handleMouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isMouseOverHud(mouseX, mouseY)) {
            dragging = true;
            dragStartX = (int) mouseX;
            dragStartY = (int) mouseY;
            startX = KorenetClient.CONFIG.hudX;
            startY = KorenetClient.CONFIG.hudY;
        }
    }

    private void handleMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!dragging) return;
        int dx = (int) (mouseX - dragStartX);
        int dy = (int) (mouseY - dragStartY);
        KorenetClient.CONFIG.hudX = startX + dx;
        KorenetClient.CONFIG.hudY = startY + dy;
        snapToCorners();
    }

    private void handleMouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
    }

    private boolean isMouseOverHud(double mouseX, double mouseY) {
        if (this.client == null) return false;

        int screenWidth = this.client.getWindow().getScaledWidth();
        int screenHeight = this.client.getWindow().getScaledHeight();
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;

        int x = centerX + KorenetClient.CONFIG.hudX;
        int y = centerY + KorenetClient.CONFIG.hudY;

        String pingText = "Ping: 42ms";
        String jitterText = "Jitter: 7ms";

        int lineHeight = this.textRenderer.fontHeight + 2;
        int textWidth = Math.max(this.textRenderer.getWidth(pingText), this.textRenderer.getWidth(jitterText));
        int paddingX = 6;
        int paddingY = 4;
        int bgWidth = (int) ((textWidth + paddingX * 2) * KorenetClient.CONFIG.hudScale);
        int bgHeight = (int) ((lineHeight * 2 + paddingY * 2) * KorenetClient.CONFIG.hudScale);

        int boxX = x - bgWidth / 2;
        int boxY = y - bgHeight / 2;

        return mouseX >= boxX && mouseX <= boxX + bgWidth && mouseY >= boxY && mouseY <= boxY + bgHeight;
    }

    private void snapToCorners() {
        int threshold = 20;
        int screenWidth = this.client.getWindow().getScaledWidth();
        int screenHeight = this.client.getWindow().getScaledHeight();

        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;

        int hudX = centerX + KorenetClient.CONFIG.hudX;
        int hudY = centerY + KorenetClient.CONFIG.hudY;

        if (Math.abs(hudX - 20) < threshold) KorenetClient.CONFIG.hudX = -centerX + 20;
        if (Math.abs(hudX - (screenWidth - 20)) < threshold) KorenetClient.CONFIG.hudX = centerX - 20;
        if (Math.abs(hudY - 20) < threshold) KorenetClient.CONFIG.hudY = -centerY + 20;
        if (Math.abs(hudY - (screenHeight - 20)) < threshold) KorenetClient.CONFIG.hudY = centerY - 20;
    }

    private static int sliderToInterval(double value) {
        int ms = (int) Math.round(MIN_INTERVAL_MS + (MAX_INTERVAL_MS - MIN_INTERVAL_MS) * value);
        return Math.max(MIN_INTERVAL_MS, Math.min(MAX_INTERVAL_MS, ms));
    }

    private static double intervalToSlider(int ms) {
        int clamped = Math.max(MIN_INTERVAL_MS, Math.min(MAX_INTERVAL_MS, ms));
        return (double) (clamped - MIN_INTERVAL_MS) / (MAX_INTERVAL_MS - MIN_INTERVAL_MS);
    }

    private abstract static class FloatSliderWidget extends SliderWidget {
        public FloatSliderWidget(int x, int y, int width, int height, Text text, double value) {
            super(x, y, width, height, text, value);
        }

        @Override
        public void appendClickableNarrations(net.minecraft.client.gui.screen.narration.NarrationMessageBuilder builder) {
            super.appendClickableNarrations(builder);
        }
    }
}
