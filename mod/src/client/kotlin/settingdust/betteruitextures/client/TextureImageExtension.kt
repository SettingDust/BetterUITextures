package settingdust.betteruitextures.client

import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage
import net.minecraft.client.texture.NativeImage

typealias Size = Pair<Int, Int>

fun TextureImage.resizeNinePatch(cornerSize: Size, centerSize: Size, target: Size): TextureImage {
    val doubleCornerWidth: Int = cornerSize.first * 2
    val doubleCornerHeight: Int = cornerSize.second * 2

    val originalRightX = cornerSize.first + centerSize.first
    val targetRightX = target.first - cornerSize.first

    val originalBottomY = cornerSize.second + centerSize.second
    val targetBottomY = target.second - cornerSize.second

    val leftEdge =
        NativeImage(cornerSize.first, target.second - doubleCornerHeight, true).also {
            image.resizeSubRectTo(0, cornerSize.second, cornerSize.first, centerSize.second, it)
        }
    val rightEdge =
        NativeImage(cornerSize.first, target.second - doubleCornerHeight, true).also {
            image.resizeSubRectTo(
                originalRightX,
                cornerSize.second,
                cornerSize.first,
                centerSize.second,
                it
            )
        }
    val topEdge =
        NativeImage(target.first - doubleCornerWidth, cornerSize.second, true).also {
            image.resizeSubRectTo(cornerSize.first, 0, centerSize.first, cornerSize.second, it)
        }
    val bottomEdge =
        NativeImage(target.first - doubleCornerWidth, cornerSize.second, true).also {
            image.resizeSubRectTo(
                cornerSize.first,
                originalBottomY,
                centerSize.first,
                cornerSize.second,
                it
            )
        }
    val center =
        NativeImage(target.first - doubleCornerWidth, target.second - doubleCornerHeight, true)
            .also {
                image.resizeSubRectTo(
                    cornerSize.first,
                    cornerSize.second,
                    centerSize.first,
                    cornerSize.second,
                    it
                )
            }

    return TextureImage.of(
        NativeImage(target.first, target.second, true).also {
            // Corners
            image.copyRect(it, 0, 0, 0, 0, cornerSize.first, cornerSize.second, false, false)
            image.copyRect(
                it,
                originalRightX,
                0,
                targetRightX,
                0,
                cornerSize.first,
                cornerSize.second,
                false,
                false
            )
            image.copyRect(
                it,
                0,
                originalBottomY,
                0,
                targetBottomY,
                cornerSize.first,
                cornerSize.second,
                false,
                false
            )
            image.copyRect(
                it,
                originalRightX,
                originalBottomY,
                targetRightX,
                targetBottomY,
                cornerSize.first,
                cornerSize.second,
                false,
                false
            )

            leftEdge.copyRect(
                it,
                0,
                0,
                0,
                cornerSize.second,
                leftEdge.width,
                leftEdge.height,
                false,
                false
            )
            rightEdge.copyRect(
                it,
                0,
                0,
                targetRightX,
                cornerSize.second,
                rightEdge.width,
                rightEdge.height,
                false,
                false
            )
            topEdge.copyRect(
                it,
                0,
                0,
                cornerSize.first,
                0,
                topEdge.width,
                topEdge.height,
                false,
                false
            )
            bottomEdge.copyRect(
                it,
                0,
                0,
                cornerSize.first,
                targetBottomY,
                bottomEdge.width,
                bottomEdge.height,
                false,
                false
            )
            center.copyRect(
                it,
                0,
                0,
                cornerSize.first,
                cornerSize.second,
                center.width,
                center.height,
                false,
                false
            )
        },
        metadata
    )
}
