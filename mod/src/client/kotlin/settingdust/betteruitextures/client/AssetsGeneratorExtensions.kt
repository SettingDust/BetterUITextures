package settingdust.betteruitextures.client

import net.mehvahdjukaar.moonlight.api.resources.textures.ImageTransformer
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage

fun TextureImage.generateBackgroundNinePatch(
    ninePatch: NinePatch,
    originalSize: Size,
    targetSize: Size = originalSize,
    offset: Point = Point(0, 0),
    centerColorPoint: Point? = Point(7, 7)
): TextureImage {
    val originalCenterSize =
        Size(
            originalSize.width - ninePatch.first.x - ninePatch.second.x,
            originalSize.height - ninePatch.first.y - ninePatch.second.y
        )
    val targetCenterSize =
        Size(
            targetSize.width - ninePatch.first.x - ninePatch.second.x,
            targetSize.height - ninePatch.first.y - ninePatch.second.y
        )
    val result = TextureImage.createNew(targetSize.width, targetSize.height, null)

    val targetRightX = targetSize.width - ninePatch.second.x
    val targetBottomY = targetSize.height - ninePatch.second.y
    val originalRightX = originalSize.width - ninePatch.second.x + offset.x
    val originalBottomY = originalSize.height - ninePatch.second.y + offset.y

    if (centerColorPoint != null) {
        val backgroundColor =
            getFramePixel(0, offset.x + centerColorPoint.x, offset.y + centerColorPoint.y)

        for (x in ninePatch.first.x + offset.x until targetRightX) {
            for (y in ninePatch.first.y + offset.y until targetBottomY) {
                result.setFramePixel(0, x, y, backgroundColor)
            }
        }
    }

    ImageTransformer.builder(imageWidth(), imageHeight(), targetSize.width, targetSize.height)
        .apply {
            // Corners
            // Left Top
            copyRect(offset.x, offset.y, ninePatch.first.x, ninePatch.first.y, 0, 0)
            // Right Top
            copyRect(
                originalRightX,
                offset.y,
                ninePatch.second.x,
                ninePatch.first.y,
                targetRightX,
                0
            )
            // Left Bottom
            copyRect(
                offset.x,
                originalBottomY,
                ninePatch.first.x,
                ninePatch.second.y,
                0,
                targetBottomY
            )
            // Right Bottom
            copyRect(
                originalRightX,
                originalBottomY,
                ninePatch.second.x,
                ninePatch.second.y,
                targetRightX,
                targetBottomY
            )

            // Edges
            // Top
            copyRect(
                ninePatch.first.x + offset.x,
                offset.y,
                targetCenterSize.width,
                ninePatch.first.y,
                ninePatch.first.x,
                0
            )
            // Left
            copyRect(
                offset.x,
                ninePatch.first.y + offset.y,
                ninePatch.first.x,
                targetCenterSize.height,
                0,
                ninePatch.first.y,
            )
            // Right
            copyRect(
                originalRightX,
                ninePatch.first.y + offset.y,
                ninePatch.second.x,
                targetCenterSize.height,
                targetRightX,
                ninePatch.first.y,
            )
            // Bottom
            copyRect(
                ninePatch.first.x + offset.x,
                originalBottomY,
                targetCenterSize.width,
                ninePatch.second.y,
                ninePatch.first.x,
                targetBottomY
            )
        }
        .build()
        .apply(this, result)

    if (centerColorPoint == null) {
        // Center
        image.copyRect(
            result.image,
            ninePatch.first.x + offset.x,
            ninePatch.first.y + offset.y,
            ninePatch.first.x,
            ninePatch.first.y,
            originalCenterSize.width,
            originalCenterSize.height,
            false,
            false
        )
    }
    return result
}
