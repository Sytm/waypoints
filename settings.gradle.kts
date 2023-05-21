rootProject.name = "waypoints"

plugins { id("org.gradle.toolchains.foojay-resolver-convention") version ("0.4.0") }

include(":waypoints")

include(":waypoints-api")

include(":api-base")

include(":api-sqlite")

include(":api-tests")

include(":signgui")

include(":utils")

include(":pointers")
