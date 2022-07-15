package de.md5lukas.waypoints.api

/**
 * The type of the holders / folders / waypoints.
 */
enum class Type {
    /**
     * This type is for waypoints that are visible for every player on the server
     */
    PUBLIC,

    /**
     * This type is for waypoints that are visible for every player on the server that has a specific permission
     */
    PERMISSION,

    /**
     * This type is for waypoints that are generated when a player dies. It is only visible for the same player
     */
    DEATH,

    /**
     * This type is for waypoints that a private to each player and only visible to him
     */
    PRIVATE
}