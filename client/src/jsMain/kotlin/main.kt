import korlibs.io.file.std.resourcesVfs
import korlibs.io.lang.readProperties
import minigame.*

suspend fun main() {
    val clientProps = resourcesVfs["client.properties"].readProperties()
    currentUrl = clientProps["server"]!!
    version = clientProps["version"]!!
    startMain()
}
