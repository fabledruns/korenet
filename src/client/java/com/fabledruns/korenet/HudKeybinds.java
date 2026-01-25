package com.fabledruns.korenet;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class HudKeybinds {
    public static KeyBinding openConfig;

    public static void init() {
        openConfig = KeyBindingHelper.registerKeyBinding(
            createKeyBinding("Open Korenet HUD Editor", GLFW.GLFW_KEY_H, "Korenet")
        );
    }

    public static void tick() {
        if (openConfig != null && openConfig.wasPressed()) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null) {
                client.setScreen(new KorenetHudConfigScreen(client.currentScreen));
            }
        }
    }

    private static KeyBinding createKeyBinding(String translationKey, int keyCode, String category) {
        // 1.21.8 signature: (String, InputUtil.Type, int, String)
        try {
            java.lang.reflect.Constructor<KeyBinding> ctor = KeyBinding.class.getConstructor(
                String.class,
                InputUtil.Type.class,
                int.class,
                String.class
            );
            return ctor.newInstance(translationKey, InputUtil.Type.KEYSYM, keyCode, category);
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            // Fall through to newer signature
        }

        // 1.21.10+ signature: (String, InputUtil.Key, String)
        try {
            java.lang.reflect.Constructor<KeyBinding> ctor = KeyBinding.class.getConstructor(
                String.class,
                InputUtil.Key.class,
                String.class
            );
            InputUtil.Key key = InputUtil.Type.KEYSYM.createFromCode(keyCode);
            return ctor.newInstance(translationKey, key, category);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to create key binding", e);
        }
    }
}
