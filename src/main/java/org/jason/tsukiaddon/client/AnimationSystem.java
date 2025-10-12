package org.jason.tsukiaddon.client;

import com.google.gson.Gson;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
public class AnimationSystem {
    private static final Gson GSON = new Gson();
    private static final Map<String,AnimationData> loadedAnimations = new HashMap<>();
    private static final Map<UUID,PlayingAnimation> activeAnimations = new HashMap<>();


    public static void loadAnimation(String filepath) {
        try {
            Identifier id = new Identifier("everflame", "animations/" + filepath + ".json");
            Optional<Resource> resourceOpt = MinecraftClient.getInstance().getResourceManager().getResource(id);

            if (resourceOpt.isEmpty()) {
                System.err.println("Animation file not found: " + filepath + ".json");
                return;
            }

            Resource resource = resourceOpt.get();
            InputStreamReader reader = new InputStreamReader(resource.getInputStream());

            AnimationData data = GSON.fromJson(reader, AnimationData.class);
            loadedAnimations.put(filepath, data);
            reader.close();

            System.out.println("Animation Successfully Loaded: " + filepath);
        } catch (IOException e) {
            System.err.println("Failed to load Animation: " + filepath);
            e.printStackTrace();
        }

    }

    public static void playAnimation(UUID playerUUID, String animationName) {
        AnimationData data = loadedAnimations.get(animationName);
        if (data == null) {
            System.err.println("Animation Not Found: " + animationName);
            return;
        }
        activeAnimations.put(playerUUID,new PlayingAnimation(data));
    }

    public static void tick(MinecraftClient client) {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<UUID,PlayingAnimation>> it = activeAnimations.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<UUID,PlayingAnimation> entry = it.next();
            PlayingAnimation animation = entry.getValue();

            int currentFrame = animation.getCurrentFrame(now);

            if (currentFrame >= animation.animationData.frames.size()) {
                RenderHook.setModelPose(entry.getKey(), null);
                it.remove();
                continue;
            }

            Map<String,float[]> pose = animation.animationData.frames.get(currentFrame);
            RenderHook.setModelPose(entry.getKey(),pose);
        }
    }


    private static class AnimationData {
        String name;
        int duration;
        int fps;
        List<Map<String,float[]>> frames;
    }

    private static class PlayingAnimation {
        AnimationData animationData;
        long startTime;

        PlayingAnimation(AnimationData animationData) {
            this.animationData = animationData;
            this.startTime = System.currentTimeMillis();
        }

        int getCurrentFrame(long now) {
            long elapsed = now - startTime;
            float frameTime = 1000f/ animationData.fps;
            return (int) (elapsed/frameTime);
        }
    }
}
