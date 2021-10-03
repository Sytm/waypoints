package de.md5lukas.waypoints.api

import de.md5lukas.waypoints.api.gui.GUIDisplayable

enum class OverviewSort(private val comparator: Comparator<GUIDisplayable>) : Comparator<GUIDisplayable> {

    TYPE_ASCENDING(compareBy<GUIDisplayable> {
        it.guiType
    }.thenBy {
        it.name
    }),
    TYPE_DESCENDING(compareByDescending<GUIDisplayable> {
        it.guiType
    }.thenBy {
        it.name
    }),
    NAME_ASCENDING(compareBy {
        it.name
    }),
    NAME_DESCENDING(compareByDescending {
        it.name
    }),
    CREATED_ASCENDING(compareBy {
        it.createdAt
    }),
    CREATED_DESCENDING(compareByDescending {
        it.createdAt
    });

    override fun compare(o1: GUIDisplayable?, o2: GUIDisplayable?): Int = comparator.compare(o1, o2)
}