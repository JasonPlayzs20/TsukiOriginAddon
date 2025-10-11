package org.jason.tsukiaddon.client;

import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.origin.OriginLayers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.mixin.client.particle.ParticleManagerAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;

import java.util.Map;
import java.util.Queue;



public class TsukiaddonClient implements ClientModInitializer {


    private static final double PARTICLE_REMOVE_RADIUS = 32.0;
    private static final double RADIUS_SQUARED = PARTICLE_REMOVE_RADIUS * PARTICLE_REMOVE_RADIUS;
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

    @Override
    public void onInitializeClient() {
        ORIGIN_COMPONENT = ComponentRegistry.getOrCreate(
                new Identifier("origins", "origin"),
                OriginComponent.class
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null) return;

        });
    }
    }

