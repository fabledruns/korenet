package com.fabledruns.korenet.desync.logic;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;

public class AttackTracker {

    public static void init() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (entity instanceof LivingEntity living) {
                if (!isShieldBlocked(living, player)) {
                    DesyncDetector.registerSwing(living);
                }
            }
            return ActionResult.PASS;
        });
    }

    private static boolean isShieldBlocked(LivingEntity target, PlayerEntity attacker) {
        if (!target.isBlocking()) return false;

        Vec3d attackerPos = attacker.getPos();
        Vec3d targetPos = target.getPos();
        Vec3d fromAttackerToTarget = targetPos.subtract(attackerPos);

        if (fromAttackerToTarget.lengthSquared() < 1.0e-6) return false;

        Vec3d targetLook = target.getRotationVec(1.0F);
        return fromAttackerToTarget.normalize().dotProduct(targetLook) < 0.0D;
    }
}
