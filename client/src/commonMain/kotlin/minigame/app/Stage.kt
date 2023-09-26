package app

import com.github.quillraven.fleks.WorldConfiguration
import com.github.quillraven.fleks.configureWorld
import korlibs.image.tiles.tiled.readTiledMap
import korlibs.io.file.std.resourcesVfs
import korlibs.korge.annotations.KorgeExperimental
import korlibs.korge.box2d.getOrCreateBox2dWorld
import korlibs.korge.box2d.worldView
import korlibs.korge.tiled.TiledMapView
import korlibs.korge.tiled.tiledMapView
import korlibs.korge.ui.uiContainer
import korlibs.korge.view.Container
import korlibs.korge.view.addUpdater
import korlibs.korge.view.align.centerOn
import korlibs.korge.view.camera
import korlibs.korge.view.scale
import korlibs.math.geom.Size
import org.jbox2d.dynamics.World
import minigame.screen
import util.transform

data class Stage(
    val mainContainer: Container,
    val camera: Container,
    val box2dWorld: World,
    val worldView: Container,
    val tiledMapView: TiledMapView
) {
    private var fleksWorldOrNull: com.github.quillraven.fleks.World? = null

    var fleksWorld
        set(value) { fleksWorldOrNull = value }
        get() = fleksWorldOrNull?: throw AssertionError("fleksWorld is not initialized yet")

    companion object {
        @OptIn(KorgeExperimental::class)
        suspend operator fun invoke(
            tiledMapName: String, worldConfiguration: WorldConfiguration.(Stage) -> Unit
        ): Stage {
            val tileMap = resourcesVfs[tiledMapName].readTiledMap()
            val mainContainer = screen.uiContainer(Size()).transform { centerOn(screen) }
//            mainContainer.scale(7.5, 7.5)
            val camera = mainContainer.camera {
                scale(7.5, 7.5)
            }
            val worldView = mainContainer.worldView()
            val box2dWorld = worldView.getOrCreateBox2dWorld().world
            box2dWorld.gravity.y = 9.8f * 2
            val tiledMapView = camera.tiledMapView(tileMap, smoothing = false, showShapes = false)
            worldView.registerCollider(tiledMapView)
            return Stage(mainContainer, camera, box2dWorld, worldView, tiledMapView).also { stage ->
                stage.fleksWorld = configureWorld {
                    worldConfiguration(this, stage)
                }.also { world ->
                    worldView.addUpdater { deltaTime ->
                        world.update(deltaTime.seconds.toFloat())
                    }
                }
            }
        }
    }
}
