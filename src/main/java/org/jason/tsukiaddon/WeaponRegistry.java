package org.jason.tsukiaddon;

import org.jason.tsukiaddon.client.WeaponComboConfig;
import org.jason.tsukiaddon.items.ModItems;

public class WeaponRegistry {
    public static void registerWeapons() {
        // Adamysticus - 4 hit combo
        WeaponComboConfig.register(ModItems.adamysticus,
                WeaponComboConfig.ComboConfig.builder()
                        .attacks("adamysticus_attack_1", "adamysticus_attack_2", "adamysticus_attack_3", "adamysticus_attack_4")
                        .sheathe("adamysticus_sheathe")
                        .comboWindow(1000)
                        .sheatheDelay(500)
                        .build()
        );

        // Example: Another weapon with 3 hit combo
        // WeaponComboConfig.register(ModItems.SOME_SWORD,
        //     WeaponComboConfig.ComboConfig.builder()
        //         .attacks("sword_slash_1", "sword_slash_2", "sword_slash_3")
        //         .sheathe("sword_sheathe")
        //         .comboWindow(800) // Faster combo timing
        //         .sheatheDelay(300)
        //         .build()
        // );

        // Example: Simple 2-hit weapon
        // WeaponComboConfig.register(ModItems.DAGGER,
        //     WeaponComboConfig.ComboConfig.builder()
        //         .attacks("dagger_stab", "dagger_slash")
        //         .comboWindow(600) // Very fast combo
        //         .build()
        // );
    }
}