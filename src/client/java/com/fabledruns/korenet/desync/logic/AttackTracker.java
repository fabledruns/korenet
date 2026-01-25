package com.fabledruns.korenet.desync.logic;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;

public class AttackTracker {

    public static void init() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (entity instanceof LivingEntity living) DesyncDetector.registerSwing(living);
            return ActionResult.PASS;
        });
    }
}
