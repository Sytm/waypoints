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

package de.md5lukas.waypoints.config.displays;

import de.md5lukas.waypoints.config.general.DisplaysActiveWhen;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DisplayConfig {

    private DisplaysActiveWhen displaysActiveWhen;
    private List<Material> displaysActiveWhenRequiredMaterials;

    private final ActionBarConfig actionBarConfig;
    private final BeaconConfig beaconConfig;
    private final BlinkingBlockConfig blinkingBlockConfig;
    private final CompassConfig compassConfig;
    private final ParticleConfig particleConfig;
    private final WrongWorldConfig wrongWorldConfig;

    public DisplayConfig(ConfigurationSection cfg) {
        this.actionBarConfig = new ActionBarConfig();
        this.beaconConfig = new BeaconConfig();
        this.blinkingBlockConfig = new BlinkingBlockConfig();
        this.compassConfig = new CompassConfig();
        this.particleConfig = new ParticleConfig();
        this.wrongWorldConfig = new WrongWorldConfig();
        load(cfg);
    }

    public void load(ConfigurationSection cfg) {
        this.displaysActiveWhen = DisplaysActiveWhen.getFromConfig(cfg.getString("misc.displaysActiveWhen.enabled"));
        displaysActiveWhenRequiredMaterials = cfg.getStringList("misc.displaysActiveWhen.requiredItems").stream().map(Material::matchMaterial)
                .collect(Collectors.toList());

        this.actionBarConfig.load(Objects.requireNonNull(cfg.getConfigurationSection("actionBar")));
        this.beaconConfig.load(Objects.requireNonNull(cfg.getConfigurationSection("beacon")));
        this.blinkingBlockConfig.load(Objects.requireNonNull(cfg.getConfigurationSection("blinkingBlock")));
        this.compassConfig.load(Objects.requireNonNull(cfg.getConfigurationSection("compass")));
        this.particleConfig.load(Objects.requireNonNull(cfg.getConfigurationSection("particles")));
        this.wrongWorldConfig.load(Objects.requireNonNull(cfg.getConfigurationSection("wrongWorld")));
    }

    public ActionBarConfig getActionBarConfig() {
        return actionBarConfig;
    }

    public BeaconConfig getBeaconConfig() {
        return beaconConfig;
    }

    public BlinkingBlockConfig getBlinkingBlockConfig() {
        return blinkingBlockConfig;
    }

    public CompassConfig getCompassConfig() {
        return compassConfig;
    }

    public ParticleConfig getParticleConfig() {
        return particleConfig;
    }

    public WrongWorldConfig getWrongWorldConfig() {
        return wrongWorldConfig;
    }

    public DisplaysActiveWhen getDisplaysActiveWhen() {
        return displaysActiveWhen;
    }

    public boolean isDisplayActiveForPlayer(Player player) {
        switch (displaysActiveWhen) {
            case ITEM_IN_INVENTORY:
                return Arrays.stream(player.getInventory().getContents()).filter(Objects::nonNull).anyMatch(stack -> {
                    for (Material material : displaysActiveWhenRequiredMaterials) {
                        if (material == stack.getType()) {
                            return true;
                        }
                    }
                    return false;
                });
            case ITEM_IN_HOTBAR:
                for (int i = 0; i < 9; i++) {
                    for (Material material : displaysActiveWhenRequiredMaterials) {
                        ItemStack stack = player.getInventory().getItem(i);
                        if (stack != null && material == stack.getType()) {
                            return true;
                        }
                    }
                }
                return false;
            case ITEM_IN_HAND:
                PlayerInventory inventory = player.getInventory();
                Material mainHand = inventory.getItemInMainHand().getType();
                Material offHand = inventory.getItemInOffHand().getType();
                for (Material material : displaysActiveWhenRequiredMaterials) {
                    if (material == mainHand || material == offHand) {
                        return true;
                    }
                }
                return false;
            default:
                return true;
        }
    }
}
