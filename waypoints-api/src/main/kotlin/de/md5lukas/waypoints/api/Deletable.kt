package de.md5lukas.waypoints.api

interface Deletable {

  @JvmSynthetic suspend fun delete()

  fun deleteCF() = future { delete() }
}
