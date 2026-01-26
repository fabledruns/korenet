package com.fabledruns.korenet.desync.config;

import com.fabledruns.korenet.desync.hud.DesyncHudRenderer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

/**
 * Screen used to configure the desync HUD placement and appearance.
 */
public class DesyncHudConfigScreen extends Screen {

    private final Screen parent;
    private boolean dragging = false;
    private int dragStartX, dragStartY;
    private int startX, startY;

    public DesyncHudConfigScreen(Screen parent) {
        super(Text.literal("Desync HUD Config"));
        this.parent = parent;
    }

    /**
     * Builds the UI controls for the desync HUD settings.
     */
    @Override
    protected void init() {
        int w = this.width;
        int h = this.height;
        int x = w / 2 - 100;
        int yStart = h / 2 + 20;
        int row = 24;

        super.init();

        ScreenMouseEvents.afterMouseClick(this).register(
            (screen, mouseX, mouseY, button) -> handleMouseClicked(mouseX, mouseY, button)
        );
        ScreenMouseEvents.afterMouseRelease(this).register(
            (screen, mouseX, mouseY, button) -> handleMouseReleased(mouseX, mouseY, button)
        );
        ScreenMouseEvents.afterMouseDrag(this).register(
            (screen, mouseX, mouseY, button, deltaX, deltaY) -> handleMouseDragged(mouseX, mouseY, button, deltaX, deltaY)
        );

        this.addDrawableChild(new FloatSliderWidget(x, yStart, 200, 20,
            Text.literal("Scale: " + String.format("%.2fx", DesyncHudConfig.scale)),
            (DesyncHudConfig.scale - 0.5f) / 1.5f) {
            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("Scale: " + String.format("%.2fx", DesyncHudConfig.scale)));
            }

            @Override
            protected void applyValue() {
                DesyncHudConfig.scale = (float) (this.value * 1.5d + 0.5d);
            }
        });

        this.addDrawableChild(new FloatSliderWidget(x, yStart + row, 200, 20,
            Text.literal("Opacity: " + String.format("%.0f%%", DesyncHudConfig.opacity * 100)),
            DesyncHudConfig.opacity) {
            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("Opacity: " + String.format("%.0f%%", DesyncHudConfig.opacity * 100)));
            }

            @Override
            protected void applyValue() {
                DesyncHudConfig.opacity = (float) this.value;
            }
        });

        this.addDrawableChild(CyclingButtonWidget.onOffBuilder(DesyncHudConfig.autoHide)
            .build(x, yStart + row * 2, 200, 20, Text.literal("Auto-Hide"),
                (button, value) -> DesyncHudConfig.autoHide = value));

        this.addDrawableChild(CyclingButtonWidget.onOffBuilder(DesyncHudConfig.showBackground)
            .build(x, yStart + row * 3, 200, 20, Text.literal("Show Background"),
                (button, value) -> DesyncHudConfig.showBackground = value));

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Done"), button -> {
            DesyncHudConfig.save();
            this.client.setScreen(this.parent);
        }).dimensions(x, h - 30, 200, 20).build());
    }

    /**
     * Renders a translucent backdrop and a live HUD preview.
     */
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0x80000000);
        DesyncHudRenderer.renderHudDemo(context);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Click and drag the HUD to move"), this.width / 2, 20, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    /**
     * Persists config when leaving the screen (Esc/back).
     */
    @Override
    public void close() {
        DesyncHudConfig.save();
        super.close();
    }

    private void handleMouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && DesyncHudRenderer.isMouseOverHud(mouseX, mouseY)) {
            dragging = true;
            dragStartX = (int) mouseX;
            dragStartY = (int) mouseY;
            startX = DesyncHudConfig.x;
            startY = DesyncHudConfig.y;
        }
    }

    private void handleMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!dragging) return;
        int dx = (int) (mouseX - dragStartX);
        int dy = (int) (mouseY - dragStartY);
        DesyncHudConfig.x = startX + dx;
        DesyncHudConfig.y = startY + dy;
        snapToCorners();
    }

    private void handleMouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
    }

    private void snapToCorners() {
        int threshold = 20;
        int screenWidth = this.client.getWindow().getScaledWidth();
        int screenHeight = this.client.getWindow().getScaledHeight();

        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;

        int hudX = centerX + DesyncHudConfig.x;
        int hudY = centerY + DesyncHudConfig.y;

        if (Math.abs(hudX - 20) < threshold) DesyncHudConfig.x = -centerX + 20;
        if (Math.abs(hudX - (screenWidth - 20)) < threshold) DesyncHudConfig.x = centerX - 20;
        if (Math.abs(hudY - 20) < threshold) DesyncHudConfig.y = -centerY + 20;
        if (Math.abs(hudY - (screenHeight - 20)) < threshold) DesyncHudConfig.y = centerY - 20;
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
