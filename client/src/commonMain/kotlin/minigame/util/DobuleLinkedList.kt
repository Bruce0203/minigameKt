package minigame.util

import korlibs.datastructure.FastArrayList

tailrec fun <T> T.getAll(
    code: (T?) -> T?, list: FastArrayList<T>
): List<T> {
    val next = code(this)
    return if (next == null || next == this) list else {
        list.add(next)
        next.getAll(code, list)
    }
}
