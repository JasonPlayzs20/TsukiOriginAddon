package org.jason.tsukiaddon.mixin;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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
public abstract class PlayerEntityModelMixin <T extends LivingEntity> {
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
        Map<String, float[]> pose = RenderHook.getPose(uuid);
        if (pose == null) return;

        for (Map.Entry<String, float[]> e : pose.entrySet()) {
            float[] rot = e.getValue();
            if (Objects.equals(e.getKey(), "Item")) {
                continue;
            }
            ModelPart part = mapBone(e.getKey());
            if (part != null) {
                part.pitch = rot[0];
                part.yaw = rot[1];
                part.roll = rot[2];

                if (e.getKey().equals("body") && rot[1] != 0) {
                    float bodyYaw = rot[1];
                    rightArm.pivotZ = (float)(Math.sin(bodyYaw) * 5.0);
                    rightArm.pivotX = (float)(-Math.cos(bodyYaw) * 5.0);
                    leftArm.pivotZ = (float)(-Math.sin(bodyYaw) * 5.0);
                    leftArm.pivotX = (float)(Math.cos(bodyYaw) * 5.0);


                    rightLeg.pivotX = (float)(-Math.cos(bodyYaw) * 1.9);
                    rightLeg.pivotZ = (float)(Math.sin(bodyYaw) * 1.9);
                    leftLeg.pivotZ = (float)(-Math.sin(bodyYaw) * 1.9);
                    leftLeg.pivotX = (float)(Math.cos(bodyYaw) * 1.9);

                    rightArm.yaw += bodyYaw;
                    leftArm.yaw += bodyYaw;

                    rightLeg.yaw += bodyYaw;
                    leftLeg.yaw += bodyYaw;
                }
            }
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
