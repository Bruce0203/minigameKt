package app.systems

import app.components.RigidBody
import app.components.Sprite
import com.github.quillraven.fleks.EachFrame
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.FamilyDefinition
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import korlibs.datastructure.FastArrayList
import korlibs.datastructure.fastArrayListOf
import korlibs.korge.box2d.body
import korlibs.korge.view.*
import korlibs.math.geom.Point
import korlibs.math.geom.Vector2
import minigame.util.*
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.contacts.ContactEdge
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class MovementSystem : IteratingSystem(
    family {
        all(Sprite, RigidBody)
    },
    interval = EachFrame
){

    override fun onTickEntity(entity: Entity) {
        val sprite = entity[Sprite]
        val character = sprite.body
        val rigidBody = entity[RigidBody]
        val animationView = sprite.animationView
        val isGround =
            character.hasFixtureUserData(rigidBody.footData)

        val speed = if (isGround) rigidBody.speed else rigidBody.speed
            .run { Vector2((x)*0.75f, (y)*0.75f) }
        val maxVelocityX = speed.x
        animationView.speed = sqrt(sqrt(sqrt(speed.x)))
        val (dx, dy) = rigidBody.inputAxis.invoke(entity)
        val dpos = Point(dx, dy) * speed
        val dposVec2 = dpos.let { Vec2(it.x, it.y) }
        if (dpos.x == 0f || dpos.isNaN() || character.pos.isNaN()) {
            val idle = if (sprite.isLeft) sprite.leftIdle else sprite.idle
            animationView.speed = character.body!!.linearVelocityY
            animationView.animation = if (sprite.isLeft) sprite.leftRun else sprite.run
            animationView.setFrame(3)
            if (abs(character.body!!.linearVelocityX) < 0.1) {
                animationView.animation = idle
            }
            animationView.play()
        } else if (dx != 0f) {
            character.body!!.applyForceToCenter(Vec2(dposVec2.x, 0f))
//            if (isGround) {
//                var velocityX = character.body!!.linearVelocityX
//                velocityX += dposVec2.x * character.body!!.m_invMass
//                character.body!!.linearVelocityX = max(-maxVelocityX, min(maxVelocityX, velocityX))
//            } else {
//                character.body!!.linearVelocityX += dposVec2.x * character.body!!.m_invMass
//            }
        }
        if (isGround) {
            character.body!!.m_linearDamping = Vec2(10f, 0f)
        } else {
            character.body!!.m_linearDamping = Vec2(8f, 0f)
        }
        if (isGround && !rigidBody.isJump) {
            character.body!!.linearVelocityY = dposVec2.y * character.body!!.m_invMass
            rigidBody.isJump = true
        } else if (rigidBody.isJump && isGround) {
            rigidBody.isJump = false
        }
        if (!isGround) {
            animationView.setFrame(3)
        }
        when {
            dx < 0.0 -> animationView.animation = sprite.leftRun
            dx > 0.0 -> animationView.animation = sprite.run
        }
        rigidBody.isGroundOld = isGround
        rigidBody.oldYVel = character.body!!.linearVelocityY
    }
}

private fun View.hasFixtureUserData(any1: Any?) = body?.getContactList()?.getAllContacts()?.any {
    val contact = it.contact
    contact?.getFixtureA()?.userData == any1 || contact?.getFixtureB()?.userData == any1
}?: false

private fun ContactEdge.getAllContacts() =
    getAll({ it?.prev }, fastArrayListOf(this)) + getAll({ it?.next }, fastArrayListOf())


