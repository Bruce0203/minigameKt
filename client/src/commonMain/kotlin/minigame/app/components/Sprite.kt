package app.components

import animation.ImageAnimationView
import animation.imageAnimationView
import app.Stage
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentHook
import korlibs.image.format.ASE
import korlibs.image.format.ImageDataContainer
import korlibs.image.format.readImageDataContainer
import korlibs.image.format.toProps
import korlibs.io.file.std.resourcesVfs
import korlibs.korge.box2d.body
import korlibs.korge.ui.uiContainer
import korlibs.korge.view.Container
import korlibs.korge.view.Image
import korlibs.korge.view.align.alignY
import korlibs.korge.view.align.centerOn
import korlibs.math.geom.Size
import minigame.util.*

data class Sprite(
    val body: Container,
    val animationView: ImageAnimationView<Image>,
    val animationImages: ImageDataContainer,
) : Component<Sprite> {
    val idle = animationImages.default.animationsByName["Idle"]!!
    val leftIdle = idle.flipX()
    val run = animationImages.default.animationsByName["Run"]!!
    val leftRun = run.flipX()
    val isLeft get() = body.body!!.linearVelocityX < 0
    override fun type() = Sprite
    companion object : ComponentHooks<Sprite>() {
        override val onRemoved: ComponentHook<Sprite> = { _, component ->
            component.body.removeFromParent()
        }
    }
}

suspend fun Stage.createSprite(spriteName: String): Sprite {
    val images = resourcesVfs[spriteName].readImageDataContainer(ASE.toProps())
    lateinit var character: Container
    lateinit var anim: ImageAnimationView<Image>
    camera.uiContainer(Size()) {
        character = this
        anim = imageAnimationView(
            images.default.animationsByName["Idle"]!!,
        ) { smoothing = false }
    }.centerOn(tiledMapView).alignY(tiledMapView, 0.3, true)
    return Sprite(character, anim, images)
}
