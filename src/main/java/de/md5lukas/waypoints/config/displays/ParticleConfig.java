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

import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;

public class ParticleConfig {

    private boolean enabled;
    private int interval;
    private double heightOffset;
    private boolean verticalDirection;
    private int amount;
    private double distance;
    private Particle particle;

    public void load(ConfigurationSection cfg) {
        enabled = cfg.getBoolean("enabled");
        interval = cfg.getInt("interval");
        heightOffset = cfg.getDouble("heightOffset");
        verticalDirection = cfg.getBoolean("verticalDirection");
        amount = cfg.getInt("amount");
        //noinspection ConstantConditions
        particle = Particle.valueOf(cfg.getString("particle").toUpperCase());
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getInterval() {
        return interval;
    }

    public double getHeightOffset() {
        return heightOffset;
    }

    public boolean isVerticalDirection() {
        return verticalDirection;
    }

    public int getAmount() {
        return amount;
    }

    public double getDistance() {
        return distance;
    }

    public Particle getParticle() {
        return particle;
    }
}
