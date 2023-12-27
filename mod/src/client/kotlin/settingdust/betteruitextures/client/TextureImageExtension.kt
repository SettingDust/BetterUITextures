package settingdust.betteruitextures.client

import net.mehvahdjukaar.moonlight.api.resources.textures.ImageTransformer
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage
import net.minecraft.client.texture.NativeImage

fun TextureImage.resizeNinePatch(ninePatch: Border, target: Size): TextureImage {
    if (imageWidth() == target.width && imageHeight() == target.height) return this
    val doubleCornerWidth: Int = ninePatch.first.width + ninePatch.second.width
    val doubleCornerHeight: Int = ninePatch.first.height + ninePatch.second.height

    val originalRightX = imageWidth() - ninePatch.second.width
    val targetRightX = target.width - ninePatch.second.width

    val originalBottomY = imageHeight() - ninePatch.second.height
    val targetBottomY = target.height - ninePatch.second.height

    val centerSize = Size(imageWidth() - doubleCornerWidth, imageHeight() - doubleCornerHeight)
    val targetCenterSize =
        Size(target.width - doubleCornerWidth, target.height - doubleCornerHeight)

    val leftEdge =
        NativeImage(ninePatch.first.width, targetCenterSize.height, true).also {
            image.resizeSubRectTo(
                0,
                ninePatch.first.height,
                ninePatch.first.width,
                centerSize.height,
                it
            )
        }
    val rightEdge =
        NativeImage(ninePatch.second.width, targetCenterSize.height, true).also {
            image.resizeSubRectTo(
                originalRightX,
                ninePatch.first.height,
                ninePatch.second.width,
                centerSize.height,
                it
            )
        }
    val topEdge =
        NativeImage(targetCenterSize.width, ninePatch.first.height, true).also {
            image.resizeSubRectTo(
                ninePatch.first.width,
                0,
                centerSize.width,
                ninePatch.first.height,
                it
            )
        }
    val bottomEdge =
        NativeImage(targetCenterSize.width, ninePatch.second.height, true).also {
            image.resizeSubRectTo(
                ninePatch.first.width,
                originalBottomY,
                centerSize.width,
                ninePatch.second.height,
                it
            )
        }
    val center =
        NativeImage(targetCenterSize.width, targetCenterSize.height, true).also {
            image.resizeSubRectTo(
                ninePatch.first.width,
                ninePatch.first.height,
                centerSize.width,
                centerSize.height,
                it
            )
        }

    return TextureImage.of(
        NativeImage(target.width, target.height, true).also {
            // Corners
            image.copyRect(
                it,
                0,
                0,
                0,
                0,
                ninePatch.first.width,
                ninePatch.first.height,
                false,
                false
            )
            image.copyRect(
                it,
                originalRightX,
                0,
                targetRightX,
                0,
                ninePatch.second.width,
                ninePatch.first.height,
                false,
                false
            )
            image.copyRect(
                it,
                0,
                originalBottomY,
                0,
                targetBottomY,
                ninePatch.first.width,
                ninePatch.second.height,
                false,
                false
            )
            image.copyRect(
                it,
                originalRightX,
                originalBottomY,
                targetRightX,
                targetBottomY,
                ninePatch.second.width,
                ninePatch.second.height,
                false,
                false
            )

            leftEdge.copyRect(
                it,
                0,
                0,
                0,
                ninePatch.first.height,
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
                ninePatch.first.height,
                rightEdge.width,
                rightEdge.height,
                false,
                false
            )
            topEdge.copyRect(
                it,
                0,
                0,
                ninePatch.first.width,
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
                ninePatch.first.width,
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
                ninePatch.first.width,
                ninePatch.first.height,
                center.width,
                center.height,
                false,
                false
            )
        },
        metadata
    )
}

fun TextureImage.expandCanvas(targetRect: Rect): TextureImage =
    TextureImage.createNew(targetRect.width, targetRect.height, metadata).also {
        ImageTransformer.builder(imageWidth(), imageHeight(), targetRect.width, targetRect.height)
            .copyRect(0, 0, imageWidth(), imageHeight(), targetRect.x, targetRect.y)
            .build()
            .apply(this, it)
    }
