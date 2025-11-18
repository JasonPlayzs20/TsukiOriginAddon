package org.jason.tsukiaddon.client;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class AnimationSystem {
    private static final Gson GSON = new Gson();
    private static final Map<String, AnimationData> loadedAnimations = new HashMap<>();
    private static final Map<UUID, PlayingAnimation> activeAnimations = new HashMap<>();

    public static void loadAnimation(String filepath) {
        try {
            Identifier id = new Identifier("tsukiaddon", "animations/" + filepath + ".json");
            Optional<Resource> resourceOpt = MinecraftClient.getInstance().getResourceManager().getResource(id);

            if (resourceOpt.isEmpty()) {
                System.err.println("Animation file not found: " + filepath + ".json");
                return;
            }

            Resource resource = resourceOpt.get();
            InputStreamReader reader = new InputStreamReader(resource.getInputStream());
            JsonElement json = JsonParser.parseReader(reader);
            AnimationData data = AnimationData.fromJson(json);
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
        activeAnimations.put(playerUUID, new PlayingAnimation(data));
    }

    public static void tick(MinecraftClient client) {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<UUID, PlayingAnimation>> it = activeAnimations.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, PlayingAnimation> entry = it.next();
            PlayingAnimation animation = entry.getValue();

            int currentFrame = animation.getCurrentFrame(now);

            if (currentFrame >= animation.animationData.frames.size()) {
                RenderHook.setModelPose(entry.getKey(), null);
                it.remove();
                continue;
            }

            Map<String, BonePose> pose = animation.animationData.frames.get(currentFrame);
            RenderHook.setModelPose(entry.getKey(), pose);
        }
    }

    public static void stopAnimation(UUID playerUUID) {
        activeAnimations.remove(playerUUID);
        RenderHook.setModelPose(playerUUID, null);
    }

    public static void stopAllAnimations() {
        for (UUID uuid : activeAnimations.keySet()) {
            RenderHook.setModelPose(uuid, null);
        }
        activeAnimations.clear();
    }

    // New class to hold both rotation and position
    public static class BonePose {
        public float[] rotation;
        public float[] position; // Can be null if no position offset

        public BonePose(float[] rotation, float[] position) {
            this.rotation = rotation;
            this.position = position;
        }

        public BonePose(float[] rotation) {
            this.rotation = rotation;
            this.position = null;
        }
    }

    private static class AnimationData {
        String name;
        int duration;
        int fps;
        List<Map<String, BonePose>> frames;

        // Custom deserializer to handle both array format and object format
        public static AnimationData fromJson(JsonElement json) {
            JsonObject obj = json.getAsJsonObject();
            AnimationData data = new AnimationData();
            data.name = obj.get("name").getAsString();
            data.duration = obj.get("duration").getAsInt();
            data.fps = obj.get("fps").getAsInt();
            data.frames = new ArrayList<>();

            for (JsonElement frameElement : obj.get("frames").getAsJsonArray()) {
                JsonObject frameObj = frameElement.getAsJsonObject();
                Map<String, BonePose> frame = new HashMap<>();

                for (String boneName : frameObj.keySet()) {
                    JsonElement boneData = frameObj.get(boneName);

                    if (boneData.isJsonArray()) {
                        // Old format: just rotation array
                        float[] rotation = GSON.fromJson(boneData, float[].class);
                        frame.put(boneName, new BonePose(rotation));
                    } else if (boneData.isJsonObject()) {
                        // New format: object with rotation and position
                        JsonObject boneObj = boneData.getAsJsonObject();
                        float[] rotation = GSON.fromJson(boneObj.get("rotation"), float[].class);
                        float[] position = boneObj.has("position")
                                ? GSON.fromJson(boneObj.get("position"), float[].class)
                                : null;
                        frame.put(boneName, new BonePose(rotation, position));
                    }
                }

                data.frames.add(frame);
            }

            return data;
        }
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
            float frameTime = 1000f / animationData.fps;
            return (int) (elapsed / frameTime);
        }
    }
}