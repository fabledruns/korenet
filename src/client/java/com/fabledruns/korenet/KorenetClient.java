package com.fabledruns.korenet;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KorenetClient implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("korenet");
    public static KorenetConfig CONFIG;

    @Override
    public void onInitializeClient() {
        CONFIG = ConfigManager.load();
        if (CONFIG.updateIntervalMs == 1000) {
            CONFIG.updateIntervalMs = 250;
            try {
                ConfigManager.save(CONFIG);
            } catch (Exception e) {
                LOGGER.warn("Failed to save updated updateIntervalMs", e);
            }
        }
        LOGGER.info("Loaded config, HUD={}", CONFIG.showHud);

        HudKeybinds.init();
        KorenetHud.init();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (CONFIG == null || !CONFIG.enabled) return;
            PingTracker.tick(CONFIG.updateIntervalMs);
            HudKeybinds.tick();
        });
    }
}
