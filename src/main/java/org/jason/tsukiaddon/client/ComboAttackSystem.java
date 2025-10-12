package org.jason.tsukiaddon.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class ComboAttackSystem {
    private static final Map<UUID, ComboState> combos = new HashMap<>();

    public static void registerAttack(UUID playerUUID, Item weapon) {
        WeaponComboConfig.ComboConfig config = WeaponComboConfig.getConfig(weapon);
        if (config == null) return; // No combo configured for this weapon

        ComboState state = combos.get(playerUUID);
        long now = System.currentTimeMillis();

        if (state == null) {
            // First attack
            state = new ComboState(config);
            state.comboCount = 1;
            state.lastAttackTime = now;
            combos.put(playerUUID, state);
            AnimationSystem.playAnimation(playerUUID, config.getAttackAnimation(1));
        } else {
            // Check if we're within combo window
            if (now - state.lastAttackTime <= config.getComboWindow()) {
                // Continue combo
                state.comboCount++;
                if (state.comboCount > config.getMaxCombo()) {
                    state.comboCount = 1; // Reset to first attack
                }
                state.lastAttackTime = now;
                state.sheathePending = false;
                AnimationSystem.playAnimation(playerUUID, config.getAttackAnimation(state.comboCount));
            } else {
                // Combo expired, start new combo
                state.comboCount = 1;
                state.lastAttackTime = now;
                state.sheathePending = false;
                state.config = config; // Update config in case weapon changed
                AnimationSystem.playAnimation(playerUUID, config.getAttackAnimation(1));
            }
        }
    }

    public static void tick(MinecraftClient client) {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<UUID, ComboState>> it = combos.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<UUID, ComboState> entry = it.next();
            ComboState state = entry.getValue();
            WeaponComboConfig.ComboConfig config = state.config;
            long timeSinceLastAttack = now - state.lastAttackTime;

            // Check if combo window expired
            if (timeSinceLastAttack > config.getComboWindow() + config.getSheatheDelay()) {
                // Play sheathe animation if not already played
                if (!state.sheathePending && state.comboCount > 0) {
                    AnimationSystem.playAnimation(entry.getKey(), config.getSheatheAnimation());
                    state.sheathePending = true;
                }

                // Clean up after sheathe animation completes
                if (timeSinceLastAttack > config.getComboWindow() + config.getSheatheDelay() + 1000) {
                    it.remove();
                }
            }
        }
    }

    public static int getCurrentCombo(UUID playerUUID) {
        ComboState state = combos.get(playerUUID);
        return state != null ? state.comboCount : 0;
    }

    private static class ComboState {
        WeaponComboConfig.ComboConfig config;
        int comboCount = 0;
        long lastAttackTime = 0;
        boolean sheathePending = false;

        ComboState(WeaponComboConfig.ComboConfig config) {
            this.config = config;
        }
    }
}

