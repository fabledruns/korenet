package com.fabledruns.korenet;

import com.fabledruns.korenet.config.ConfigManager;
import com.fabledruns.korenet.config.KorenetConfig;
import com.fabledruns.korenet.desync.config.DesyncHudConfig;
import com.fabledruns.korenet.desync.hud.DesyncHudRenderer;
import com.fabledruns.korenet.desync.input.DesyncHudKeybinds;
import com.fabledruns.korenet.desync.logic.AttackTracker;
import com.fabledruns.korenet.desync.logic.DesyncDetector;
import com.fabledruns.korenet.hud.HudKeybinds;
import com.fabledruns.korenet.hud.KorenetHud;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client entrypoint for Korenet. Loads config and wires up HUDs and tick handlers.
 */
public class KorenetClient implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("korenet");
    public static KorenetConfig CONFIG;
    private static final int LEGACY_INTERVAL_MS = 1000;
    private static final int DEFAULT_INTERVAL_MS = 250;

    /**
     * Initializes client-side systems and registers tick callbacks.
     */
    @Override
    public void onInitializeClient() {
        CONFIG = ConfigManager.load();
        migrateLegacyUpdateInterval();
        LOGGER.info("Loaded config, HUD={}", CONFIG.showHud);

        // init
        HudKeybinds.init();
        KorenetHud.init();
        DesyncHudConfig.load();
        DesyncHudKeybinds.init();
        DesyncHudRenderer.init();
        AttackTracker.init();
        DesyncDetector.init();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (CONFIG == null || !CONFIG.enabled) return;
            PingTracker.tick(CONFIG.updateIntervalMs);
            HudKeybinds.tick();
            DesyncHudKeybinds.tick();
            DesyncDetector.tick();
        });
    }

    /**
     * Migrates legacy update interval values to the current default.
     */
    private static void migrateLegacyUpdateInterval() {
        if (CONFIG == null || CONFIG.updateIntervalMs != LEGACY_INTERVAL_MS) return;
        CONFIG.updateIntervalMs = DEFAULT_INTERVAL_MS;
        try {
            ConfigManager.save(CONFIG);
        } catch (Exception e) {
            LOGGER.warn("Failed to save updated updateIntervalMs", e);
        }
    }
}
