package org.jason.tsukiaddon.client;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RenderHook {
    private static final Map<UUID, Map<String,float[]>> POSES = new ConcurrentHashMap<>();

    public static void setModelPose(UUID player, Map<String,float[]> transforms) {
        if (transforms == null || transforms.isEmpty()) POSES.remove(player);
        else POSES.put(player, transforms);
    }

    public static Map<String,float[]> getPose(UUID player) {
        return POSES.get(player);
    }
}
