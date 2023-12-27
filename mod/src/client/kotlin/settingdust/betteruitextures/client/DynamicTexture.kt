package settingdust.betteruitextures.client

import kotlinx.serialization.Contextual
import net.minecraft.util.Identifier
import org.quiltmc.qkl.library.serialization.annotation.CodecSerializable

@CodecSerializable
data class DynamicTexture(
    val modId: String? = null,
    val targetTexture: @Contextual Identifier,
    val modifiers: Set<TextureModifier>
)
