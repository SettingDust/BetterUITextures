package settingdust.betteruitextures.client

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
)

fun DynamicTexture.targetTexture(manager: ResourceManager): TextureImage =
    if (targetTexture != null) TextureImage.open(manager, targetTexture)
    else TextureImage.createNew(size!!.width, size.height, null)
