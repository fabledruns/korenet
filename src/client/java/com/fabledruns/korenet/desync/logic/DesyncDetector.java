package com.fabledruns.korenet.desync.logic;

import com.fabledruns.korenet.desync.hud.DesyncHudRenderer;
import com.fabledruns.korenet.desync.hud.DesyncStatus;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.entity.LivingEntity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class DesyncDetector {

    private static final Map<LivingEntity, Integer> lastHurtTime = new HashMap<>();
    private static final Map<LivingEntity, Long> swingTimestamps = new HashMap<>();

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null) {
                swingTimestamps.clear();
                lastHurtTime.clear();
                return;
            }
            cleanupInvalidEntities();
        });
    }

    private static void cleanupInvalidEntities() {
        Iterator<LivingEntity> iterator = swingTimestamps.keySet().iterator();
        while (iterator.hasNext()) {
            LivingEntity entity = iterator.next();
            if (entity == null || !entity.isAlive() || entity.isRemoved()) {
                iterator.remove();
                lastHurtTime.remove(entity);
            }
        }
    }

    public static void registerSwing(LivingEntity entity) {
        lastHurtTime.put(entity, entity.hurtTime);
        swingTimestamps.put(entity, System.currentTimeMillis());
    }

    public static void tick() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<LivingEntity, Long>> iterator = swingTimestamps.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<LivingEntity, Long> entry = iterator.next();
            LivingEntity entity = entry.getKey();
            long swingTime = entry.getValue();
            long elapsed = now - swingTime;

            int oldHurt = lastHurtTime.getOrDefault(entity, -1);

            if (entity.hurtTime != oldHurt) {
                if (elapsed > 200) {
                    DesyncHudRenderer.updateStatus(DesyncStatus.MINOR);
                } else {
                    DesyncHudRenderer.updateStatus(DesyncStatus.SYNCED);
                }
                iterator.remove();
                lastHurtTime.remove(entity);
            } else if (elapsed > 300) {
                DesyncHudRenderer.updateStatus(DesyncStatus.SEVERE);
                iterator.remove();
                lastHurtTime.remove(entity);
            }
        }
    }
}
