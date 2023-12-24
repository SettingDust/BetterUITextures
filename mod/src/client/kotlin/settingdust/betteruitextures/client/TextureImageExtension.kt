package settingdust.betteruitextures.client

import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage
import net.minecraft.client.texture.NativeImage

fun TextureImage.resizeNinePatch(ninePatch: NinePatch, target: Size): TextureImage {
    val doubleCornerWidth: Int = ninePatch.first.x + ninePatch.second.x
    val doubleCornerHeight: Int = ninePatch.first.y + ninePatch.second.y

    val originalRightX = imageWidth() - ninePatch.second.x
    val targetRightX = target.width - ninePatch.second.x

    val originalBottomY = imageHeight() - ninePatch.second.y
    val targetBottomY = target.height - ninePatch.second.y

    val centerSize = Size(imageWidth() - doubleCornerWidth, imageHeight() - doubleCornerHeight)
    val targetCenterSize =
        Size(target.width - doubleCornerWidth, target.height - doubleCornerHeight)

    val leftEdge =
        NativeImage(ninePatch.first.x, targetCenterSize.height, true).also {
            image.resizeSubRectTo(
                0,
                ninePatch.first.y,
                ninePatch.first.x,
                targetCenterSize.height,
                it
            )
        }
    val rightEdge =
        NativeImage(ninePatch.first.x, targetCenterSize.height, true).also {
            image.resizeSubRectTo(
                originalRightX,
                ninePatch.first.y,
                ninePatch.second.x,
                targetCenterSize.height,
                it
            )
        }
    val topEdge =
        NativeImage(centerSize.width, ninePatch.first.y, true).also {
            image.resizeSubRectTo(
                ninePatch.first.x,
                0,
                targetCenterSize.width,
                ninePatch.first.y,
                it
            )
        }
    val bottomEdge =
        NativeImage(centerSize.width, ninePatch.second.y, true).also {
            image.resizeSubRectTo(
                ninePatch.second.y,
                originalBottomY,
                targetCenterSize.width,
                ninePatch.second.y,
                it
            )
        }
    val center =
        NativeImage(centerSize.width, centerSize.height, true).also {
            image.resizeSubRectTo(
                ninePatch.first.x,
                ninePatch.first.y,
                targetCenterSize.width,
                targetCenterSize.height,
                it
            )
        }

    return TextureImage.of(
        NativeImage(target.width, target.height, true).also {
            // Corners
            image.copyRect(it, 0, 0, 0, 0, ninePatch.first.x, ninePatch.first.y, false, false)
            image.copyRect(
                it,
                originalRightX,
                0,
                targetRightX,
                0,
                ninePatch.first.x,
                ninePatch.first.y,
                false,
                false
            )
            image.copyRect(
                it,
                0,
                originalBottomY,
                0,
                targetBottomY,
                ninePatch.first.x,
                ninePatch.second.y,
                false,
                false
            )
            image.copyRect(
                it,
                originalRightX,
                originalBottomY,
                targetRightX,
                targetBottomY,
                ninePatch.second.x,
                ninePatch.second.y,
                false,
                false
            )

            leftEdge.copyRect(
                it,
                0,
                0,
                0,
                ninePatch.first.y,
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
                ninePatch.first.y,
                rightEdge.width,
                rightEdge.height,
                false,
                false
            )
            topEdge.copyRect(
                it,
                0,
                0,
                ninePatch.first.x,
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
                ninePatch.first.x,
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
                ninePatch.first.x,
                ninePatch.first.y,
                center.width,
                center.height,
                false,
                false
            )
        },
        metadata
    )
}
