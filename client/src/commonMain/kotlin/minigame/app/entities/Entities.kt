package app.entities

import app.Stage
import app.components.CameraFollowingTarget
import app.components.Sprite
import app.components.createRigidBody
import app.components.createSprite
import korlibs.datastructure.fastArrayListOf
import korlibs.event.Key
import korlibs.korge.box2d.body
import korlibs.math.geom.Vector2
import minigame.*
import minigame.util.*
import org.jbox2d.dynamics.Body

suspend fun Stage.createPlayer() = fleksWorld.entity {
    it += createSprite("sprites/kat.ase")
    it += createRigidBody(speed = Vector2(60f, 7.8f), inputAxis = {
        screen.stage!!.keys.run {
            Vector2(
                getDeltaAxis(Key.A, Key.D).takeIf { it != 0f }?: getDeltaAxis(Key.LEFT, Key.RIGHT),
                getDeltaAxis(Key.W, Key.S).takeIf { it != 0f }?: getDeltaAxis(Key.UP, Key.DOWN)
            )
        }
    })
    it += CameraFollowingTarget
}
suspend fun Stage.createBot() = fleksWorld.entity {
    it += createSprite("sprites/player.ase")
    it += createRigidBody(speed = Vector2(60f, 5.9f)) {
        val stage = this@createBot
        this[Sprite].body.body!!
        Vector2(0, -1)
    }
}

private fun Body.getAllContacts() =
    getAll({ it?.prev }, fastArrayListOf(this)) +
            getAll({ it?.m_next }, fastArrayListOf())
