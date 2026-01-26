package com.fabledruns.korenet.desync.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DesyncHudConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("korenet");
    public static int x = 0;
    public static int y = -20;
    public static float scale = 1.0f;
    public static float opacity = 1.0f;
    public static boolean autoHide = true;
    public static boolean showBackground = true;

    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("korenet_desync_hud.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(new ConfigData(x, y, scale, opacity, autoHide, showBackground)));
        } catch (IOException e) {
            LOGGER.error("Failed to save desync HUD config", e);
        }
    }

    public static void load() {
        if (Files.notExists(CONFIG_PATH)) return;
        try {
            ConfigData data = GSON.fromJson(Files.readString(CONFIG_PATH), ConfigData.class);
            if (data == null) return;
            x = data.x;
            y = data.y;
            scale = data.scale;
            opacity = data.opacity;
            autoHide = data.autoHide;
            showBackground = data.showBackground;
        } catch (IOException e) {
            LOGGER.error("Failed to load desync HUD config", e);
        }
    }

    private record ConfigData(int x, int y, float scale, float opacity, boolean autoHide, boolean showBackground) {}
}
