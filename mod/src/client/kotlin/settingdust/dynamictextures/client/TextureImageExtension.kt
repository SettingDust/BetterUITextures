package settingdust.dynamictextures.client

import net.mehvahdjukaar.moonlight.api.resources.textures.ImageTransformer
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage
import net.minecraft.client.texture.NativeImage

fun NativeImage.resize(sourceRect: Rect, targetSize: Size, repeat: Boolean = false) =
    NativeImage(targetSize.width, targetSize.height, true).also {
        if (!repeat)
            resizeSubRectTo(sourceRect.x, sourceRect.y, sourceRect.width, sourceRect.height, it)
        else {
            var widthCursor = 0
            while (true) {
                val width =
                    (targetSize.width - widthCursor).coerceAtMost(sourceRect.width).coerceAtLeast(0)
                if (width <= 0) break
                copyRect(
                    it,
                    sourceRect.x,
                    sourceRect.y,
                    widthCursor,
                    0,
                    width,
                    sourceRect.height,
                    false,
                    false
                )
                widthCursor += width
                if (widthCursor >= it.width || width == 0) break
            }
            var heightCursor = 0
            while (true) {
                val height =
                    (targetSize.height - heightCursor)
                        .coerceAtMost(sourceRect.height)
                        .coerceAtLeast(0)
                if (height <= 0) break
                it.copyRect(0, 0, 0, heightCursor, targetSize.width, height, false, false)
                heightCursor += height
                if (heightCursor >= it.height) break
            }
        }
    }

fun TextureImage.resizeNinePatch(
    ninePatch: Border,
    target: Size,
    repeat: Boolean = false
): TextureImage {
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
        image.resize(
            Rect(0, ninePatch.first.height, ninePatch.first.width, centerSize.height),
            Size(ninePatch.first.width, targetCenterSize.height),
            repeat
        )
    val rightEdge =
        image.resize(
            Rect(
                originalRightX,
                ninePatch.first.height,
                ninePatch.second.width,
                centerSize.height,
            ),
            Size(ninePatch.second.width, targetCenterSize.height),
            repeat
        )
    val topEdge =
        image.resize(
            Rect(
                ninePatch.first.width,
                0,
                centerSize.width,
                ninePatch.first.height,
            ),
            Size(targetCenterSize.width, ninePatch.first.height),
            repeat
        )
    val bottomEdge =
        image.resize(
            Rect(
                ninePatch.first.width,
                originalBottomY,
                centerSize.width,
                ninePatch.second.height,
            ),
            Size(targetCenterSize.width, ninePatch.second.height),
            repeat
        )
    val center =
        image.resize(
            Rect(
                ninePatch.first.width,
                ninePatch.first.height,
                centerSize.width,
                centerSize.height,
            ),
            Size(targetCenterSize.width, targetCenterSize.height),
            repeat
        )

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
