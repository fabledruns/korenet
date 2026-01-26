package com.fabledruns.korenet.config;

import com.fabledruns.korenet.desync.config.DesyncHudConfigScreen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class KorenetConfigScreen extends Screen {

    private final Screen parent;

    public KorenetConfigScreen(Screen parent) {
        super(Text.literal("Korenet Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int w = this.width;
        int h = this.height;
        int yStart = h / 2 - 20;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Korenet HUD"), button ->
            this.client.setScreen(new KorenetHudConfigScreen(this)))
            .dimensions(w / 2 - 100, yStart, 200, 20)
            .build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Desync HUD"), button ->
            this.client.setScreen(new DesyncHudConfigScreen(this)))
            .dimensions(w / 2 - 100, yStart + 24, 200, 20)
            .build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Done"), button ->
            this.client.setScreen(this.parent))
            .dimensions(w / 2 - 100, h - 30, 200, 20)
            .build());
    }
}
