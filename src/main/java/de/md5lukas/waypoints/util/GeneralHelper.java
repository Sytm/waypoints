/*
 *     Waypoints2, A plugin for spigot to add waypoints functionality
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

package de.md5lukas.waypoints.util;

import fr.minuskube.inv.content.SlotPos;
import fr.minuskube.inv.util.Pattern;

import java.util.function.Predicate;

public class GeneralHelper {

	public static <T> SlotPos find(Pattern<T> pattern, Predicate<T> predicate) {
		for (int row = 0; row < pattern.getRowCount(); row++) {
			for (int column = 0; column < pattern.getColumnCount(); column++) {
				if (predicate.test(pattern.getObject(row, column)))
					return SlotPos.of(row, column);
			}
		}
		return null;
	}
}
