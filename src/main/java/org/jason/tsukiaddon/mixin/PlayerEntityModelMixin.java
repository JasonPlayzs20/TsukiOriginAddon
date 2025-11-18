package org.jason.tsukiaddon.mixin;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import org.jason.tsukiaddon.client.AnimationSystem;
import org.jason.tsukiaddon.client.RenderHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Mixin(BipedEntityModel.class)
public abstract class PlayerEntityModelMixin<T extends LivingEntity> {
    @Shadow public ModelPart head;
    @Shadow public ModelPart body;
    @Shadow public ModelPart leftArm;
    @Shadow public ModelPart rightArm;
    @Shadow public ModelPart leftLeg;
    @Shadow public ModelPart rightLeg;

    @Inject(method = "setAngles", at = @At("TAIL"))
    private void setAngles(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if (!(livingEntity instanceof AbstractClientPlayerEntity)) return;
        UUID uuid = ((AbstractClientPlayerEntity) livingEntity).getUuid();
        Map<String, AnimationSystem.BonePose> pose = RenderHook.getPose(uuid);
        if (pose == null) return;

        // Store location offset if present
        float[] locationOffset = null;
        if (pose.containsKey("location")) {
            locationOffset = pose.get("location").rotation;
        }

        // Apply bone rotations and positions
        for (Map.Entry<String, AnimationSystem.BonePose> e : pose.entrySet()) {
            if (Objects.equals(e.getKey(), "Item") || e.getKey().equals("location")) {
                continue; // Skip item and location (handled separately)
            }

            AnimationSystem.BonePose bonePose = e.getValue();
            ModelPart part = mapBone(e.getKey());
            if (part == null) continue;

            float[] rot = bonePose.rotation;
            part.pitch = rot[0];
            part.yaw = rot[1];
            part.roll = rot[2];

            // Apply individual bone position if specified
            if (bonePose.position != null) {
                float[] pos = bonePose.position;
                part.pivotX = pos[0];
                part.pivotY = pos[1];
                part.pivotZ = pos[2];
            }

            // Special handling for body rotation
            if (e.getKey().equals("body") && rot[1] != 0) {
                float bodyYaw = rot[1];
                float bodyPitch = rot[0];
                //X is to left
                //y is to down
                //z is to back
                //therefore we need to move y and z
                //12 is the amount of up
                //
                // Adjust body position based on pitch
                part.pivotY = -((float)(12 * Math.cos(bodyPitch)) - 12);
                part.pivotZ = -((float)(12 * Math.sin(bodyPitch)));

                // Rotate arms around body
                rightArm.pivotZ = (float)(Math.sin(bodyYaw) * 5.0);
                rightArm.pivotX = (float)(-Math.cos(bodyYaw) * 5.0);
                leftArm.pivotZ = (float)(-Math.sin(bodyYaw) * 5.0);
                leftArm.pivotX = (float)(Math.cos(bodyYaw) * 5.0);

                // Rotate legs around body
                rightLeg.pivotX = (float)(-Math.cos(bodyYaw) * 1.9);
                rightLeg.pivotZ = (float)(Math.sin(bodyYaw) * 1.9);
                leftLeg.pivotZ = (float)(-Math.sin(bodyYaw) * 1.9);
                leftLeg.pivotX = (float)(Math.cos(bodyYaw) * 1.9);

                // Apply yaw to limbs
                rightArm.yaw += bodyYaw;
                leftArm.yaw += bodyYaw;
                rightLeg.yaw += bodyYaw;
                leftLeg.yaw += bodyYaw;

                // Adjust arm positions based on body pitch
                rightArm.pivotY = -((float)(12 * Math.cos(bodyPitch)) - 14);
                rightArm.pivotZ += -((float)(12 * Math.sin(bodyPitch)));
                leftArm.pivotY = -((float)(12 * Math.cos(bodyPitch)) - 14);
                leftArm.pivotZ += -((float)(12 * Math.sin(bodyPitch)));

                // Adjust head position based on body pitch
                head.pivotY = -((float)(12 * Math.cos(bodyPitch)) - 12);
                head.pivotZ = -((float)(12 * Math.sin(bodyPitch)));
            }
        }

        // Apply global location offset to all parts AFTER rotations
        if (locationOffset != null) {
            head.pivotX += locationOffset[0];
            head.pivotY += locationOffset[1];
            head.pivotZ += locationOffset[2];

            body.pivotX += locationOffset[0];
            body.pivotY += locationOffset[1];
            body.pivotZ += locationOffset[2];

            rightArm.pivotX += locationOffset[0];
            rightArm.pivotY += locationOffset[1];
            rightArm.pivotZ += locationOffset[2];

            leftArm.pivotX += locationOffset[0];
            leftArm.pivotY += locationOffset[1];
            leftArm.pivotZ += locationOffset[2];

            rightLeg.pivotX += locationOffset[0];
            rightLeg.pivotY += locationOffset[1];
            rightLeg.pivotZ += locationOffset[2];

            leftLeg.pivotX += locationOffset[0];
            leftLeg.pivotY += locationOffset[1];
            leftLeg.pivotZ += locationOffset[2];
        }
    }

    private ModelPart mapBone(String name) {
        switch (name) {
            case "head": return head;
            case "body": return body;
            case "rightArm": return rightArm;
            case "leftArm": return leftArm;
            case "rightLeg": return rightLeg;
            case "leftLeg": return leftLeg;
            default: return null;
        }
    }
}