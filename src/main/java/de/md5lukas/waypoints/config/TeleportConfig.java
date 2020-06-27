/*
 *     Waypoints2, A plugin for spigot to add waypoints functionality
 *     Copyright (C) 2019-2020 Lukas Planz
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.md5lukas.waypoints.config;

import de.md5lukas.waypoints.util.VaultHook;
import de.md5lukas.waypoints.util.XPHelper;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class TeleportConfig {

    private TeleportEnabled enabled;
    private TeleportPaymentMethod paymentMethod;
    private TeleportPaymentGrowthType growthType;
    private long baseAmount;
    private double growthModifier;
    private long maxAmount;

    public TeleportConfig(ConfigurationSection cfg) {
        load(cfg);
    }

    public void load(ConfigurationSection cfg) {
        this.enabled = TeleportEnabled.getFromConfig(cfg.getString("enabled"));
        this.paymentMethod = TeleportPaymentMethod.getFromConfig(cfg.getString("method"));
        this.growthType = TeleportPaymentGrowthType.getFromConfig(cfg.getString("growthType"));
        this.baseAmount = cfg.getLong("baseAmount");
        this.growthModifier = cfg.getDouble("growthModifier");
        this.maxAmount = cfg.getLong("maxAmount");
        if (this.maxAmount < 0)
            this.maxAmount = Long.MAX_VALUE;
        if (this.paymentMethod == TeleportPaymentMethod.XP_POINTS || this.paymentMethod == TeleportPaymentMethod.XP_LEVELS)
            this.maxAmount = Math.min(this.maxAmount, Integer.MAX_VALUE);
    }

    public boolean hasPlayerEnoughCurrency(Player player, int n) {
        long cost = calculateCost(n);
        switch (paymentMethod) {
            case XP_POINTS:
                return XPHelper.getPlayerExp(player) >= cost;
            case XP_LEVELS:
                return player.getLevel() >= cost;
            case VAULT:
                return VaultHook.getBalance(player) >= cost;
        }
        return false;
    }

    public boolean withdraw(Player player, int n) {
        long cost = calculateCost(n);
        switch (paymentMethod) {
            case XP_POINTS:
                XPHelper.changePlayerExp(player, (int) -cost);
                return true;
            case XP_LEVELS:
                player.setLevel((int) (player.getLevel() - cost));
                return true;
            case VAULT:
                return VaultHook.withdraw(player, cost);
        }
        return false;
    }

    public long calculateCost(int n) {
        return growthType.calculateCost(n, baseAmount, growthModifier, maxAmount);
    }

    public TeleportEnabled getEnabled() {
        return enabled;
    }

    public TeleportPaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public enum TeleportEnabled {
        PERMISSION_ONLY("permissionOnly"), PAY("pay"), FREE("free");

        private final String inConfig;

        TeleportEnabled(String inConfig) {
            this.inConfig = inConfig;
        }

        public static TeleportEnabled getFromConfig(String inConfig) {
            return Arrays.stream(TeleportEnabled.values()).filter(type -> type.inConfig.equalsIgnoreCase(inConfig)).findFirst()
                    .orElse(PERMISSION_ONLY); // TODO replace with enum matcher when new md5-commons is in place
        }
    }

    public enum TeleportPaymentMethod {
        XP_POINTS("xpPoints"), XP_LEVELS("xpLevels"), VAULT("vault");

        private final String inConfig;

        TeleportPaymentMethod(String inConfig) {
            this.inConfig = inConfig;
        }

        public static TeleportPaymentMethod getFromConfig(String inConfig) {
            return Arrays.stream(TeleportPaymentMethod.values()).filter(type -> type.inConfig.equalsIgnoreCase(inConfig)).findFirst()
                    .orElse(XP_POINTS); // TODO replace with enum matcher when new md5-commons is in place
        }
    }

    public enum TeleportPaymentGrowthType {
        LOCKED("locked"), LINEAR("linear"), MULTIPLY("multiply");

        private final String inConfig;

        TeleportPaymentGrowthType(String inConfig) {
            this.inConfig = inConfig;
        }

        public long calculateCost(int n, long baseAmount, double growthModifier, long maxAmount) {
            switch (this) {
                case LOCKED:
                    return baseAmount;
                case LINEAR:
                    return limit((long) (baseAmount + (n * growthModifier)), maxAmount);
                case MULTIPLY:
                    return limit((long) (baseAmount * (Math.pow(growthModifier, n))), maxAmount);
                default:
                    throw new IllegalStateException(
                            "If you get this error, the configuration seems to be messed up regarding the teleport payment growth modifier. But normally this shouldn't be possible so report this error please");
            }
        }

        private long limit(long value, long maxAmount) {
            if (value < 0)
                return maxAmount;
            return Math.min(maxAmount, value);
        }

        public static TeleportPaymentGrowthType getFromConfig(String inConfig) {
            return Arrays.stream(TeleportPaymentGrowthType.values()).filter(type -> type.inConfig.equalsIgnoreCase(inConfig)).findFirst()
                    .orElse(LOCKED); // TODO replace with enum matcher when new md5-commons is in place
        }
    }
}
