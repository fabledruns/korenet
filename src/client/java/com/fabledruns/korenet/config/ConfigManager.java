package com.fabledruns.korenet.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Handles loading and saving the main Korenet config file.
 */
public class ConfigManager {
    private static final Logger LOGGER    = LoggerFactory.getLogger("korenet");
    private static final Gson GSON        = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("korenet.json");

    /**
     * Loads config from disk or returns defaults if missing/corrupt.
     */
    public static KorenetConfig load() {
        try {
            if (Files.notExists(CONFIG_PATH)) {
                KorenetConfig config = new KorenetConfig();
                save(config);
                return config;
            }
            KorenetConfig config = GSON.fromJson(Files.readString(CONFIG_PATH), KorenetConfig.class);
            return config != null ? config : new KorenetConfig();
        } catch (Exception e) {
            LOGGER.error("Failed to load config, using defaults", e);
            return new KorenetConfig();
        }
    }

    /**
     * Saves the current config to disk, creating parent directories if needed.
     */
    public static void save(KorenetConfig config) throws IOException {
        Files.createDirectories(CONFIG_PATH.getParent());
        Files.writeString(CONFIG_PATH, GSON.toJson(config));
    }
}