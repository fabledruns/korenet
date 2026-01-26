package com.fabledruns.korenet.mixin.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.fabledruns.korenet.hud.KorenetHud;

@Mixin(InGameHud.class)
public class KorenetHudMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void korenet$renderHud(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        KorenetHud.renderHud(context, tickCounter);
    }
}
