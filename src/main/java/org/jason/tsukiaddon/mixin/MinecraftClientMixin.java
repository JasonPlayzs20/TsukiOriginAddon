package org.jason.tsukiaddon.mixin;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import org.jason.tsukiaddon.client.ComboAttackSystem;
import org.jason.tsukiaddon.client.WeaponComboConfig;
import org.jason.tsukiaddon.items.ModItems;
import org.jason.tsukiaddon.network.AnimationPackets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "doAttack", at = @At("HEAD"))
    private void onDoAttack(CallbackInfoReturnable<Boolean> cir) {
        MinecraftClient client = (MinecraftClient) (Object) this;
        ClientPlayerEntity player = client.player;
//        player.sendMessage(Text.literal("hello"));

        if (player != null) {
            ItemStack weapon = player.getMainHandStack();

            if (WeaponComboConfig.hasCombo(weapon.getItem())) {
                // Register attack in combo system with weapon
                ComboAttackSystem.registerAttack(player.getUuid(), weapon.getItem(),true);

                // Send to server for multiplayer
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeUuid(player.getUuid());
                buf.writeInt(ComboAttackSystem.getCurrentCombo(player.getUuid()));
                ClientPlayNetworking.send(AnimationPackets.PLAY_ANIMATION, buf);
            }
        }
    }
}