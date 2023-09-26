package app.components

import com.github.quillraven.fleks.Component
import minigame.util.*

object CameraFollowingTarget : Component<CameraFollowingTarget>, ComponentHooks<CameraFollowingTarget>() {
    override fun type() = CameraFollowingTarget
}
