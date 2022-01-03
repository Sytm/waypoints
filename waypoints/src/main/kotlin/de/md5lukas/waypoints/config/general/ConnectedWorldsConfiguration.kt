package de.md5lukas.waypoints.config.general

class ConnectedWorldsConfiguration {

    var worlds: List<ConnectedWorlds> = emptyList()
        private set

    fun loadFromConfiguration(list: List<Map<String, String>>) {
        val newWorlds = ArrayList<ConnectedWorlds>()

        list.forEach {
            newWorlds.add(
                ConnectedWorlds(
                    it["overworld"]!!,
                    it["nether"]!!,
                )
            )
        }

        worlds = newWorlds
    }
}

data class ConnectedWorlds(val primary: String, val secondary: String)