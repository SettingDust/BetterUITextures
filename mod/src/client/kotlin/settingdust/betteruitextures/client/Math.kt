package settingdust.betteruitextures.client

import org.quiltmc.qkl.library.serialization.annotation.CodecSerializable

@CodecSerializable data class Point(val x: Int, val y: Int)

@CodecSerializable data class Size(val width: Int, val height: Int)

@CodecSerializable
data class Rect(val x: Int, val y: Int, val width: Int, val height: Int) {
    companion object {
        val INVALID = Rect(-1, -1, -1, -1)
    }
}

/** The second should be the border size at */
@CodecSerializable data class Border(val first: Size, val second: Size)
