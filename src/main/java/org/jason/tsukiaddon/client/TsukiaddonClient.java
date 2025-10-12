package org.jason.tsukiaddon.client;

import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.origin.OriginLayers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.mixin.client.particle.ParticleManagerAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.origin.OriginLayers;
import io.github.apace100.origins.component.OriginComponent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import org.jason.tsukiaddon.WeaponRegistry;
import org.jason.tsukiaddon.network.AnimationPackets;
import org.lwjgl.glfw.GLFW;

import java.util.Map;
import java.util.Queue;
import java.util.UUID;


public class TsukiaddonClient implements ClientModInitializer {

    private static ComponentKey<OriginComponent> ORIGIN_COMPONENT;

    // Check if a player has the gloomer origin
    private static boolean isTsuki(PlayerEntity player) {

        try {
//            player.sendMessage(Text.literal("Seen player1"));
            if (TsukiaddonClient.ORIGIN_COMPONENT == null) return false;
//            player.sendMessage(Text.literal("Seen player2"));
            OriginComponent component = TsukiaddonClient.ORIGIN_COMPONENT.get(player);
//            player.sendMessage(Text.literal("Seen player3"));
            OriginLayer layer = OriginLayers.getLayer(new Identifier("origins", "origin"));
//            player.sendMessage(Text.literal("Seen player4"));
            if (layer == null) return false;
//            player.sendMessage(Text.literal("Seen player5"));
            Origin origin = component.getOrigin(layer);
//            player.sendMessage(Text.literal(origin.toString()));

            if (origin != null) {
                String originId = origin.getIdentifier().toString();
                return originId.equals("tsuki:tsuki");
            }
        } catch (Exception e) {
            // Player might not have component yet
            player.sendMessage(Text.literal(e.getMessage()));
        }
        return false;
    }
    public static KeyBinding PLAY_KEY;
    @Override
    public void onInitializeClient() {
        PLAY_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.everflame.play",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "category.everflame"
        ));
        ORIGIN_COMPONENT = ComponentRegistry.getOrCreate(
                new Identifier("origins", "origin"),
                OriginComponent.class
        );
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            AnimationRegistry.registerAll();
            WeaponRegistry.registerWeapons();
        });

//        ClientPlayNetworking.registerGlobalReceiver(AnimationPackets.PLAY_ANIMATION, (client, handler, buf, responseSender) -> {
//            UUID playerUUID = buf.readUuid();
//            int comboCount = buf.readInt();
//
//            client.execute(() -> {
//                ComboAttackSystem.registerAttack(playerUUID);
//            });
//        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

//            if (PLAY_KEY.isPressed()) {
//                // Send to server instead of playing directly
//                PacketByteBuf buf = PacketByteBufs.create();
//                buf.writeUuid(client.player.getUuid());
//                buf.writeString("test");
//                ClientPlayNetworking.send(AnimationPackets.PLAY_ANIMATION, buf);
//            }

            AnimationSystem.tick(client);
            ComboAttackSystem.tick(client);
        });


    }
    }

