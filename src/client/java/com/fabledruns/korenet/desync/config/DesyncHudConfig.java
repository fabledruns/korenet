package com.fabledruns.korenet.desync.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DesyncHudConfig {
    public static int x = 0;
    public static int y = -20;
    public static float scale = 1.0f;
    public static float opacity = 1.0f;
    public static boolean autoHide = true;
    public static boolean showBackground = true;

    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("korenet_desync_hud.json").toFile();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(new ConfigData(x, y, scale, opacity, autoHide, showBackground), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                ConfigData data = GSON.fromJson(reader, ConfigData.class);
                x = data.x;
                y = data.y;
                scale = data.scale;
                opacity = data.opacity;
                autoHide = data.autoHide;
                showBackground = data.showBackground;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private record ConfigData(int x, int y, float scale, float opacity, boolean autoHide, boolean showBackground) {}
}
