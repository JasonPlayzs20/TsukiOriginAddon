package org.jason.tsukiaddon.client;

import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.Map;

public class WeaponComboConfig {
    private static final Map<Item, ComboConfig> weaponConfigs = new HashMap<>();

    public static void register(Item item, ComboConfig config) {
        weaponConfigs.put(item, config);
    }

    public static ComboConfig getConfig(Item item) {
        return weaponConfigs.get(item);
    }

    public static boolean hasCombo(Item item) {
        return weaponConfigs.containsKey(item);
    }

    public static class ComboConfig {
        private final String[] attackAnimations;
        private final String sheatheAnimation;
        private final long comboWindow;
        private final long sheatheDelay;

        public ComboConfig(String[] attackAnimations, String sheatheAnimation, long comboWindow, long sheatheDelay) {
            this.attackAnimations = attackAnimations;
            this.sheatheAnimation = sheatheAnimation;
            this.comboWindow = comboWindow;
            this.sheatheDelay = sheatheDelay;
        }

        public String getAttackAnimation(int comboCount) {
            if (comboCount <= 0 || comboCount > attackAnimations.length) {
                return attackAnimations[0];
            }
            return attackAnimations[comboCount - 1];
        }

        public String getSheatheAnimation() {
            return sheatheAnimation;
        }

        public int getMaxCombo() {
            return attackAnimations.length;
        }

        public long getComboWindow() {
            return comboWindow;
        }

        public long getSheatheDelay() {
            return sheatheDelay;
        }

        public static class Builder {
            private String[] attackAnimations;
            private String sheatheAnimation = "sheathe";
            private long comboWindow = 1000;
            private long sheatheDelay = 500;

            public Builder attacks(String... animations) {
                this.attackAnimations = animations;
                return this;
            }

            public Builder sheathe(String animation) {
                this.sheatheAnimation = animation;
                return this;
            }

            public Builder comboWindow(long milliseconds) {
                this.comboWindow = milliseconds;
                return this;
            }

            public Builder sheatheDelay(long milliseconds) {
                this.sheatheDelay = milliseconds;
                return this;
            }

            public ComboConfig build() {
                if (attackAnimations == null || attackAnimations.length == 0) {
                    throw new IllegalStateException("At least one attack animation must be specified");
                }
                return new ComboConfig(attackAnimations, sheatheAnimation, comboWindow, sheatheDelay);
            }
        }

        public static Builder builder() {
            return new Builder();
        }
    }
}
