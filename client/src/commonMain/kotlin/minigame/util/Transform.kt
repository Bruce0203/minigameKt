package util

import korlibs.korge.view.View
import minigame.event.*

fun <T : View> T.transform(code: T.() -> Unit): T {
    code(this)
    onEvent(ResizedEvent) { code(this) }
    return this
}
