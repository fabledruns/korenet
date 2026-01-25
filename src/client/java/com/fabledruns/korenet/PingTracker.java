package com.fabledruns.korenet;

import net.minecraft.client.MinecraftClient;

import java.util.ArrayDeque;
import java.util.Deque;

public class PingTracker {

    private static final int MAX_SAMPLES = 20;
    private static final Deque<Integer> samples = new ArrayDeque<>();
    private static long lastUpdate = 0;

    public static void tick(int intervalMs) {
        long now = System.currentTimeMillis();
        if (now - lastUpdate < intervalMs) return;
        lastUpdate = now;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getNetworkHandler() == null) return;
        if (mc.player == null) return;

        var entry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
        if (entry == null) return;
        int ping = entry.getLatency();
        samples.addLast(ping);

        if (samples.size() > MAX_SAMPLES) {
            samples.removeFirst();
        }
    }

    public static int getPing() {
        return samples.isEmpty() ? -1 : samples.getLast();
    }

    public static int getJitter() {
        if (samples.size() < 2) return 0;

        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for (int p : samples) {
            min = Math.min(min, p);
            max = Math.max(max, p);
        }
        return max - min;
    }
}
