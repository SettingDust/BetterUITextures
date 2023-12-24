package settingdust.betteruitextures.client

import net.mehvahdjukaar.moonlight.api.resources.textures.ImageTransformer
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage

fun TextureImage.generateBackgroundNinePatch(ninePatch: NinePatch, size: Size): TextureImage {
    val centerSize =
        Size(
            size.width - ninePatch.first.x - ninePatch.second.x,
            size.height - ninePatch.first.y - ninePatch.second.y
        )
    val result = TextureImage.createNew(size.width, size.height, null)

    val targetRightX = size.width - ninePatch.second.x
    val targetBottomY = size.height - ninePatch.second.y
    val originalRightX = imageWidth() - ninePatch.second.x
    val originalBottomY = imageHeight() - ninePatch.second.y

    val backgroundColor = getFramePixel(0, 7, 7)

    for (x in ninePatch.first.x until targetRightX) {
        for (y in ninePatch.first.y until targetBottomY) {
            result.setFramePixel(0, x, y, backgroundColor)
        }
    }

    ImageTransformer.builder(256, 256, size.width, size.height)
        .apply {
            // Corners
            copyRect(0, 0, ninePatch.first.x, ninePatch.first.y, 0, 0)
            copyRect(originalRightX, 0, ninePatch.second.x, ninePatch.first.y, targetRightX, 0)
            copyRect(0, originalBottomY, ninePatch.first.x, ninePatch.second.y, 0, targetBottomY)
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
                ninePatch.first.x,
                0,
                centerSize.width,
                ninePatch.first.y,
                ninePatch.first.x,
                0
            )
            // Left
            copyRect(
                0,
                ninePatch.first.y,
                ninePatch.first.x,
                centerSize.height,
                0,
                ninePatch.first.y,
            )
            // Right
            copyRect(
                originalRightX,
                ninePatch.first.y,
                ninePatch.second.x,
                centerSize.height,
                targetRightX,
                ninePatch.first.y,
            )
            // Bottom
            copyRect(
                ninePatch.first.x,
                originalBottomY,
                centerSize.width,
                ninePatch.second.y,
                ninePatch.first.x,
                targetBottomY
            )
        }
        .build()
        .apply(this, result)
    return result
}
