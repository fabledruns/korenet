package com.fabledruns.korenet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;


public class PingTracker {

    private static final int MAX_SAMPLES = 20;
    private static final int TICK_MS = 50;
    private static final int[] samples = new int[MAX_SAMPLES];
    private static int sampleCount = 0;
    private static int sampleIndex = 0;
    private static int progressMs = 0;

    public static void tick(int intervalMs) {
        progressMs += TICK_MS;
        int clampedIntervalMs = Math.max(intervalMs, TICK_MS);
        if (progressMs < clampedIntervalMs) return;
        progressMs = 0;

        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.getNetworkHandler() == null || mc.player == null) {
            clear();
            return;
        }

        PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
        if (entry == null) return;

        int ping = entry.getLatency();

        samples[sampleIndex] = ping;
        sampleIndex = (sampleIndex + 1) % MAX_SAMPLES;
        if (sampleCount < MAX_SAMPLES) {
            sampleCount++;
        }
    }

    public static int getPing() {
        if (sampleCount == 0) return -1;
        int lastIndex = sampleIndex - 1;
        if (lastIndex < 0) lastIndex = MAX_SAMPLES - 1;
        return samples[lastIndex];
    }

    public static int getJitter() {
        if (sampleCount < 2) return 0;

        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        int startIndex = sampleIndex - sampleCount;
        if (startIndex < 0) startIndex += MAX_SAMPLES;

        for (int i = 0; i < sampleCount; i++) {
            int index = (startIndex + i) % MAX_SAMPLES;
            int p = samples[index];
            if (p < min) min = p;
            if (p > max) max = p;
        }
        return max - min;
    }

    public static void clear() {
        sampleCount = 0;
        sampleIndex = 0;
        progressMs = 0;
    }
}