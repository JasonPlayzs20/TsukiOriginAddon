package org.jason.tsukiaddon.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jason.tsukiaddon.items.ModItems;
import org.jason.tsukiaddon.network.AnimationPackets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerAttackMixin {

    @Inject(method = "attack(Lnet/minecraft/entity/Entity;)V", at = @At("TAIL"))
    private void onAttack(Entity target, CallbackInfo ci) {
        System.out.println("mixed");

        PlayerEntity player = (PlayerEntity) (Object) this;
        System.out.println("Attack mixin triggered! Target = " + target);

        if (player.getMainHandStack().getItem() == ModItems.adamysticus) {
            if (!player.getWorld().isClient && player instanceof ServerPlayerEntity serverPlayer) {
                AnimationPackets.sendToTracking(serverPlayer, player.getUuid(), "test");
            }
        }
    }
}

