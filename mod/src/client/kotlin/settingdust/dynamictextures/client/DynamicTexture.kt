package settingdust.dynamictextures.client

import kotlin.math.min
import kotlinx.serialization.Contextual
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
) {
    constructor(
        modId: String? = null,
        targetTexture: @Contextual Identifier? = null,
        size: Size? = null,
        modifiers: TextureModifier
    ) : this(modId, targetTexture, size, setOf(modifiers))
}

fun DynamicTexture.targetTexture(manager: ResourceManager): TextureImage {
    if (size != null) {
        if (targetTexture != null) {
            try {
                val targetImage = TextureImage.open(manager, targetTexture)
                return targetImage.expandCanvas(
                    Rect(
                        0,
                        0,
                        min(targetImage.imageWidth(), size.width),
                        min(targetImage.imageHeight(), size.height),
                    )
                )
            } catch (_: Throwable) {}
        }
        return TextureImage.createNew(size.width, size.height, null)
    } else if (targetTexture == null) error("Either 'size' and 'targetTexture' are null")
    else return TextureImage.open(manager, targetTexture)
}
