package minigame

import app.*
import korlibs.image.color.*
import korlibs.image.font.*
import korlibs.image.text.*
import korlibs.io.file.std.*
import korlibs.korge.*
import korlibs.korge.scene.*
import korlibs.korge.style.*
import korlibs.korge.ui.*
import korlibs.korge.view.*
import korlibs.korge.view.align.*
import korlibs.math.geom.*
import korlibs.math.interpolation.*
import korlibs.memory.*
import korlibs.time.*
import minigame.event.*
import minigame.util.Effect.Companion.easingEffect
import minigame.util.Effect.Companion.effectAlpha
import org.jbox2d.common.*
import util.*

lateinit var currentUrl: String
lateinit var version: String

val defaultStyle: ViewStyles.() -> Unit = {
    textFont = font
    textAlignment = TextAlignment.MIDDLE_CENTER
    textSize = 100f
    textColor = Colors.BLACK
}

lateinit var scene: SceneContainer
lateinit var screen: UIContainer
lateinit var font: Font
lateinit var camera: Camera

suspend fun startMain() {
    Settings.aabbExtension = 0f
    Settings.aabbMultiplier = 0f
    font = resourcesVfs["fonts/NanumSquareNeoTTF-dEb.woff"].readWoffFont()
    Korge(
        windowSize = Size(1280, 720),
//        title = "",
//        icon = "images/logo.png",
        scaleMode = ScaleMode.NO_SCALE,
        clipBorders = false,
        scaleAnchor = Anchor.TOP_LEFT,
        backgroundColor = Colors["#353535"]
    ) {
        scene = sceneContainer()
        scene.changeTo({ MainScene() })
    }
}

class MainScene : Scene() {
    override suspend fun SContainer.sceneMain() {
        screen = uiContainer(size) { styles(defaultStyle) }
        camera = screen.camera()
        onStageResized { width, height ->
            screen.size(width, height)
            dispatch(ResizedEvent())
        }
        screen.container {
            text(version, textSize = 30f) {
            }.zIndex(100)
            zIndex(100)
        }.transform {
            val padding = 10
            positionY(screen.height - height - padding)
            positionX(padding*2)
        }
        val loading = uiText("로딩 중...") {
            styles(defaultStyle)
            centerOn(screen)
        }
        loading.alignY(screen, 0.48, true)
        val curtain = screen.solidRect(screen.size, color = Colors.BLACK).zIndex(1000)
            .transform { size(screen.size) }
        mainView()
        loading.easingEffect(0.2.seconds, Easing.SMOOTH, arrayOf(
            effectAlpha(1f, isDown = true)
        )) { removeFromParent() }
        curtain.easingEffect(0.5.seconds, Easing.SMOOTH, arrayOf(
            effectAlpha(1f, isDown = true)
        )) { removeFromParent() }
    }
}
