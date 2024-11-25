package org.oxycblt.auxio.music.stack.interpret.linker

class Contribution<T> {
    private val map = mutableMapOf<T, Int>()

    val candidates: Collection<T> get() = map.keys

    fun contribute(key: T) {
        map[key] = map.getOrDefault(key, 0) + 1
    }

    fun contribute(keys: Collection<T>) {
        keys.forEach { contribute(it) }
    }

    fun resolve() = map.maxByOrNull { it.value }?.key ?: error("Nothing was contributed")

}