package com.fabledruns.korenet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;

import java.util.ArrayList;
import java.util.List;

public class PingTracker {

    private static final int MAX_SAMPLES = 20;
    private static final List<Integer> samples = new ArrayList<>();
    private static int progressMs = 0;

    public static void tick(int intervalMs) {
        progressMs += 50;
        if (progressMs < intervalMs) return;
        progressMs = 0;

        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.getNetworkHandler() == null || mc.player == null) {
            clear();
            return;
        }

        PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
        if (entry == null) return;

        int ping = entry.getLatency();

        synchronized (samples) {
            samples.add(ping);
            if (samples.size() > MAX_SAMPLES) {
                samples.remove(0);
            }
        }
    }

    public static int getPing() {
        synchronized (samples) {
            return samples.isEmpty() ? -1 : samples.get(samples.size() - 1);
        }
    }

    public static int getJitter() {
        synchronized (samples) {
            if (samples.size() < 2) return 0;

            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;

            for (int p : samples) {
                if (p < min) min = p;
                if (p > max) max = p;
            }
            return max - min;
        }
    }

    public static void clear() {
        synchronized (samples) {
            samples.clear();
        }
    }
}