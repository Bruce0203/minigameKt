package app.components

import app.Stage
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentHook
import com.github.quillraven.fleks.Entity
import korlibs.korge.box2d.*
import korlibs.math.geom.Rectangle
import korlibs.math.geom.Size
import korlibs.math.geom.Vector2
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import minigame.util.*
import org.jbox2d.dynamics.BodyType

typealias InputAxis = Entity.() -> Vector2

class RigidBody internal constructor(
    val stage: Stage,
    var speed: Vector2,
    var isJump: Boolean = false,
    var oldYVel: Float = 0f,
    var isGroundOld: Boolean = false,
    val inputAxis: InputAxis = { Vector2.ZERO },
    val footData: Any = UUID.generateUUID()
) : Component<RigidBody> {
    override fun type() = RigidBody
    companion object : ComponentHooks<RigidBody>() {
        override val onAdded: ComponentHook<RigidBody> = { entity, component ->
            val sprite = entity[Sprite]
            val (width, height) = sprite.animationImages.default.let { Size(it.width, it.height) }
            val mainBodyRect = Rectangle(.0, .0, width*0.1*0.5, height*0.1*0.5)
//        solidRect(mainBodyRect.width*20f, mainBodyRect.height*20f).alpha(0.5f)
            val character = sprite.body
            component.stage.worldView.createBody {
                type = BodyType.DYNAMIC
                fixedRotation = true
                allowSleep = false
            }.fixture {
                friction = 0f
//                userData = CharacterBody.UPPER
                shape = BoxShape(mainBodyRect)
            }.fixture {
                friction = 0f
                val xPadding = mainBodyRect.width/4f
                val footRect = Rectangle(
                    xPadding, mainBodyRect.y*2 + mainBodyRect.height/2f,
                    mainBodyRect.width - xPadding*2, mainBodyRect.y*2 + mainBodyRect.height/2f
                )
                userData = component.footData
                shape = BoxShape(footRect)
            }.also { character.body = it }.view = character
        }
        override val onRemoved: ComponentHook<RigidBody> = { entity, component ->
            entity[Sprite].body.body?.destroyBody()
        }
    }
}

fun Stage.createRigidBody(speed: Vector2, inputAxis: InputAxis = { Vector2() }) =
    RigidBody(this, speed, inputAxis = inputAxis)
