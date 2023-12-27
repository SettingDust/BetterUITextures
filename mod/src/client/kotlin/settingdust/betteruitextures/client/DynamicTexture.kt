package settingdust.betteruitextures.client

import kotlin.math.min
import kotlinx.serialization.Contextual
import net.mehvahdjukaar.moonlight.api.resources.textures.ImageTransformer
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import org.quiltmc.qkl.library.serialization.annotation.CodecSerializable

@CodecSerializable
data class DynamicTexture(
    val modId: String? = null,
    val targetTexture: @Contextual Identifier? = null,
    val size: Size? = null,
    val modifiers: Set<TextureModifier> = setOf()
)

fun DynamicTexture.targetTexture(manager: ResourceManager): TextureImage =
    if (size != null) {
        val image = TextureImage.createNew(size.width, size.height, null)
        if (targetTexture != null) {
            try {
                val targetImage = TextureImage.open(manager, targetTexture)
                ImageTransformer.builder(
                        targetImage.imageWidth(),
                        targetImage.imageHeight(),
                        image.imageWidth(),
                        image.imageHeight()
                    )
                    .copyRect(
                        0,
                        0,
                        min(targetImage.imageWidth(), image.imageWidth()),
                        min(targetImage.imageHeight(), image.imageHeight()),
                        0,
                        0,
                        min(targetImage.imageWidth(), image.imageWidth()),
                        min(targetImage.imageHeight(), image.imageHeight()),
                    )
                    .build()
                    .apply(targetImage, image)
            } catch (_: Throwable) {}
        }
        image
    } else if (targetTexture == null) error("Either 'size' and 'targetTexture' are null")
    else TextureImage.open(manager, targetTexture)
