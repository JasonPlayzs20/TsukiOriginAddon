package org.jason.tsukiaddon.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

import org.jason.tsukiaddon.client.AnimationSystem;
import org.jason.tsukiaddon.client.RenderHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Mixin(HeldItemRenderer.class)
public class PlayerItemModelMixin {
    @Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"))
    private void onRenderItem(LivingEntity entity, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        UUID uuid = entity.getUuid();
        Map<String, AnimationSystem.BonePose> pose = RenderHook.getPose(uuid);
        float[] handRotation = new float[3]; // Initialize with zeros

        if (pose != null) {
            // First pass: get hand rotation
            for (Map.Entry<String, AnimationSystem.BonePose> e : pose.entrySet()) {
                if (e.getKey().contains("Arm")) {
                    if (leftHanded) {
                        if (e.getKey().contains("left")) {
                            handRotation = e.getValue().rotation;
                        }
                    } else {
                        if (e.getKey().contains("right")) {
                            handRotation = e.getValue().rotation;
                        }
                    }
                }
            }

            // Second pass: apply item rotation
            for (Map.Entry<String, AnimationSystem.BonePose> e : pose.entrySet()) {
                if (Objects.equals(e.getKey(), "Item")) {
                    float[] movements = e.getValue().rotation;
                    matrices.multiply(RotationAxis.POSITIVE_X.rotation(movements[0] + handRotation[0]));
                    matrices.multiply(RotationAxis.POSITIVE_Z.rotation(movements[1] - handRotation[1]));
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotation(movements[2] - handRotation[2]));

                    // Apply position offset if it exists
                    if (e.getValue().position != null) {
                        float[] pos = e.getValue().position;
                        matrices.translate(pos[0], pos[1], pos[2]);
                    }
                }
            }
        }
    }
}