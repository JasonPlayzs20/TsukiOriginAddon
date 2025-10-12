package org.jason.tsukiaddon.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class AnimationPackets {
    public static final Identifier PLAY_ANIMATION = new Identifier("tsukiaddon", "play_animation");

    public static void sendToTracking(ServerPlayerEntity player, UUID targetUUID, String animationName) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(targetUUID);
        buf.writeString(animationName);

        for (ServerPlayerEntity tracking : player.getServerWorld().getPlayers()) {
            if (tracking.squaredDistanceTo(player) < 64*64) {
                ServerPlayNetworking.send(tracking, PLAY_ANIMATION, buf);
            }
        }
    }
}
