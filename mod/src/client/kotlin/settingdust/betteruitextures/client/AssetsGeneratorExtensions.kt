package settingdust.betteruitextures.client

import net.mehvahdjukaar.moonlight.api.resources.textures.ImageTransformer
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage

fun TextureImage.generateBackgroundNinePatch(
    ninePatch: Border,
    originalSize: Size,
    targetSize: Size = originalSize,
    offset: Point = Point(0, 0),
    centerColorPoint: Point? = Point(7, 7)
): TextureImage {
    val originalCenterSize =
        Size(
            originalSize.width - ninePatch.first.width - ninePatch.second.width,
            originalSize.height - ninePatch.first.height - ninePatch.second.height
        )
    val targetCenterSize =
        Size(
            targetSize.width - ninePatch.first.width - ninePatch.second.width,
            targetSize.height - ninePatch.first.height - ninePatch.second.height
        )
    val result = TextureImage.createNew(targetSize.width, targetSize.height, null)

    val targetRightX = targetSize.width - ninePatch.second.width
    val targetBottomY = targetSize.height - ninePatch.second.height
    val originalRightX = originalSize.width - ninePatch.second.width + offset.x
    val originalBottomY = originalSize.height - ninePatch.second.height + offset.y

    if (centerColorPoint != null) {
        val backgroundColor =
            getFramePixel(0, offset.x + centerColorPoint.x, offset.y + centerColorPoint.y)

        for (x in ninePatch.first.width + offset.x until targetRightX) {
            for (y in ninePatch.first.height + offset.y until targetBottomY) {
                result.setFramePixel(0, x, y, backgroundColor)
            }
        }
    }

    ImageTransformer.builder(imageWidth(), imageHeight(), targetSize.width, targetSize.height)
        .apply {
            // Corners
            // Left Top
            copyRect(offset.x, offset.y, ninePatch.first.width, ninePatch.first.height, 0, 0)
            // Right Top
            copyRect(
                originalRightX,
                offset.y,
                ninePatch.second.width,
                ninePatch.first.height,
                targetRightX,
                0
            )
            // Left Bottom
            copyRect(
                offset.x,
                originalBottomY,
                ninePatch.first.width,
                ninePatch.second.height,
                0,
                targetBottomY
            )
            // Right Bottom
            copyRect(
                originalRightX,
                originalBottomY,
                ninePatch.second.width,
                ninePatch.second.height,
                targetRightX,
                targetBottomY
            )

            // Edges
            // Top
            copyRect(
                ninePatch.first.width + offset.x,
                offset.y,
                targetCenterSize.width,
                ninePatch.first.height,
                ninePatch.first.width,
                0
            )
            // Left
            copyRect(
                offset.x,
                ninePatch.first.height + offset.y,
                ninePatch.first.width,
                targetCenterSize.height,
                0,
                ninePatch.first.height,
            )
            // Right
            copyRect(
                originalRightX,
                ninePatch.first.height + offset.y,
                ninePatch.second.width,
                targetCenterSize.height,
                targetRightX,
                ninePatch.first.height,
            )
            // Bottom
            copyRect(
                ninePatch.first.width + offset.x,
                originalBottomY,
                targetCenterSize.width,
                ninePatch.second.height,
                ninePatch.first.width,
                targetBottomY
            )
        }
        .build()
        .apply(this, result)

    if (centerColorPoint == null) {
        // Center
        image.copyRect(
            result.image,
            ninePatch.first.width + offset.x,
            ninePatch.first.height + offset.y,
            ninePatch.first.width,
            ninePatch.first.height,
            originalCenterSize.width,
            originalCenterSize.height,
            false,
            false
        )
    }
    return result
}
