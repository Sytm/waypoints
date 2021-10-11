package de.md5lukas.waypoints.legacy.internal

import de.md5lukas.waypoints.api.OverviewSort

internal enum class LegacySortMode(val overviewSort: OverviewSort) {

    NAME_ASC(OverviewSort.NAME_ASCENDING),
    NAME_DESC(OverviewSort.NAME_DESCENDING),
    CREATED_ASC(OverviewSort.CREATED_ASCENDING),
    CREATED_DESC(OverviewSort.CREATED_DESCENDING),
    TYPE(OverviewSort.TYPE_ASCENDING)
}