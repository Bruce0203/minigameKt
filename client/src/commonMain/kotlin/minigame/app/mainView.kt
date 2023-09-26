package app

import app.components.CameraFollowingTarget
import app.components.RigidBody
import app.components.Sprite
import app.entities.createBot
import app.entities.createPlayer
import app.systems.CameraSystem
import app.systems.MovementSystem
import minigame.app.*
import minigame.util.*

suspend fun mainView() {

    val stage = Stage("sprites/level1.tmx") { stage ->
        components {
            add(Sprite); add(RigidBody)
            add(CameraFollowingTarget)
        }
        systems {
            add(CameraSystem(stage.camera))
            add(MovementSystem())
        }
    }
    stage.createPlayer()
    stage.createBot()
    stage.debugView()
}
