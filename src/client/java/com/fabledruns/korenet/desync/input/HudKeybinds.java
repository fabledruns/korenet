package com.fabledruns.korenet.desync.input;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import com.fabledruns.korenet.config.KorenetHudConfigScreen;

public class HudKeybinds {
    public static KeyBinding openConfig;

    public static void init() {
        try {
            openConfig = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                    "key.korenet.open_hud_editor",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_H,
                    "category.korenet"
                )
            );
        } catch (RuntimeException ignored) {
            
        }
    }

    public static void tick() {
        if (openConfig != null && openConfig.wasPressed()) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null) {
                client.setScreen(new KorenetHudConfigScreen(client.currentScreen));
            }
        }
    }

}
