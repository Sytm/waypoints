/*
 *     Waypoints
 *     Copyright (C) 2019  Lukas Planz
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

package de.md5lukas.wp.util;

import de.md5lukas.wp.config.Config;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.regex.Pattern;

public class StringHelper {

	public static final Pattern MCUSERNAMEPATTERN = Pattern.compile("^\\w{3,16}$");

	public static String generateDirectionIndicator(String indicator, String normal, String arrowLeft, String arrowRight,
	                                                String section, double angle, int amountOfSections, double range) {
		if (amountOfSections % 2 == 0)
			++amountOfSections;
		if (angle > range) {
			return indicator + arrowLeft + normal + repeatString(section, amountOfSections) + arrowRight;
		}
		if (angle < (-range)) {
			return normal + arrowLeft + repeatString(section, amountOfSections) + indicator + arrowRight;
		}
		double percent = -(angle / range);
		int nthSection = (int) Math.round(((double) (amountOfSections - 1) / 2) * percent);
		nthSection += Math.round((double) amountOfSections / 2);
		return normal + arrowLeft + repeatString(section, nthSection - 1) + indicator + section + normal + repeatString(section, amountOfSections - nthSection) + arrowRight;
	}

	public static String repeatString(String s, int amount) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < amount; ++i)
			sb.append(s);
		return sb.toString();
	}

	public static String correctWorldName(World world) {
		if (Config.worldNameAliases.containsKey(world.getName()))
			return Config.worldNameAliases.get(world.getName());
		return world.getName();
	}

	public static String fixDistance(Location loc1, Location loc2) {
		if (loc1.getWorld() != loc2.getWorld())
			return "?";
		return MathHelper.DF.format(loc1.distance(loc2));
	}
}
