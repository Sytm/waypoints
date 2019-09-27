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
