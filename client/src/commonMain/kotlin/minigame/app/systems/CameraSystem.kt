package app.systems

import app.components.CameraFollowingTarget
import app.components.RigidBody
import app.components.Sprite
import com.github.quillraven.fleks.EachFrame
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import korlibs.korge.box2d.body
import korlibs.korge.view.Container
import korlibs.math.geom.Point
import korlibs.math.geom.Vector2
import org.jbox2d.common.Vec2
import kotlin.math.pow

class CameraSystem(private val camera: Container) : IteratingSystem(
    family {
        all(Sprite, CameraFollowingTarget)
    },
    interval = EachFrame
) {
    override fun onTickEntity(entity: Entity) {
        val sprite = entity[Sprite]
        val character = sprite.body
        val globalSize = sprite.animationImages.default.run { Point(width, height) }
        val centerPos = character.parent!!.localToGlobal(character.pos + globalSize/2)
        val tx = camera.globalPos.x - centerPos.x
        val ty = camera.globalPos.y - centerPos.y
        val scale = 0.175f
        camera.x += (tx - camera.x) * scale
        camera.y += (ty - camera.y) * scale
    }
}

private fun Vec2.pow(pow: Float) = run { Vec2(x.pow(pow), y.pow(pow)) }
private fun Vec2.toPoint() = Point(x, y)