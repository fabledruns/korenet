package com.fabledruns.korenet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("korenet.json");

    public static KorenetConfig load() {
        try {
            if (Files.notExists(CONFIG_PATH)) {
                KorenetConfig config = new KorenetConfig();
                save(config);
                return config;
            }
            return GSON.fromJson(Files.readString(CONFIG_PATH), KorenetConfig.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new KorenetConfig();
        }
    }

    public static void save(KorenetConfig config) throws IOException {
        Files.writeString(CONFIG_PATH, GSON.toJson(config));
    }
}