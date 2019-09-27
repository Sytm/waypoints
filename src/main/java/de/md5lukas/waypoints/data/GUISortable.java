package de.md5lukas.waypoints.data;

import de.md5lukas.waypoints.gui.GUIDisplayable;
import de.md5lukas.waypoints.gui.GUIType;

public interface GUISortable extends GUIDisplayable {

	String getName();
	long createdAt();
	GUIType getType();
}
