package org.jason.tsukiaddon.client;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RenderHook {
    private static final Map<UUID, Map<String, AnimationSystem.BonePose>> POSES = new ConcurrentHashMap<>();

    public static void setModelPose(UUID player, Map<String, AnimationSystem.BonePose> transforms) {
        if (transforms == null || transforms.isEmpty()) {
            POSES.remove(player);
        } else {
            POSES.put(player, transforms);
        }
    }

    public static Map<String, AnimationSystem.BonePose> getPose(UUID player) {
        return POSES.get(player);
    }
}