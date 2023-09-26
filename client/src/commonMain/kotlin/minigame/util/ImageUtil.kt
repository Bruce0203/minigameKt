package minigame.util

import korlibs.image.format.*

fun ImageAnimation.flipX() = let {
    ImageAnimation(
        it.frames.map { frame ->
            ImageFrame.invoke(
                frame.bitmap.flippedX(),
                frame.time,
                frame.targetX,
                frame.targetY,
                frame.main,
                frame.includeInAtlas,
                frame.name,
                frame.index
            )
        }.toList().apply { println(this) },
        it.direction,
        it.name,
        it.layers
    )
}
