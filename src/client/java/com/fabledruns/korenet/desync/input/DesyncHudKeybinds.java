package com.fabledruns.korenet.desync.input;

import com.fabledruns.korenet.desync.config.DesyncHudConfigScreen;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class DesyncHudKeybinds {
    public static KeyBinding openConfig;

    public static void init() {
        openConfig = KeyBindingHelper.registerKeyBinding(
            createKeyBinding("Open Desync HUD Editor", GLFW.GLFW_KEY_J, "Korenet")
        );
    }

    public static void tick() {
        if (openConfig != null && openConfig.wasPressed()) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null) {
                client.setScreen(new DesyncHudConfigScreen(client.currentScreen));
            }
        }
    }

    private static KeyBinding createKeyBinding(String translationKey, int keyCode, String category) {
        try {
            java.lang.reflect.Constructor<KeyBinding> ctor = KeyBinding.class.getConstructor(
                String.class,
                InputUtil.Type.class,
                int.class,
                String.class
            );
            return ctor.newInstance(translationKey, InputUtil.Type.KEYSYM, keyCode, category);
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            // Fall through
        }

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
