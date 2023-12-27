package settingdust.betteruitextures.client

import com.mojang.serialization.Codec
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.mehvahdjukaar.moonlight.api.resources.textures.ImageTransformer
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage
import net.minecraft.registry.Registry
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import net.pearx.kasechange.toSnakeCase
import org.quiltmc.qkl.library.serialization.annotation.CodecSerializable
import settingdust.betteruitextures.BetterUITextures

object TextureModifierTypes {
    @JvmStatic val REMOVE_COLOR = register<RemoveColor>()
    @JvmStatic val REMOVE_RECT = register<RemoveRect>()
    @JvmStatic val REMOVE_BORDER = register<RemoveBorder>()
    @JvmStatic val COPY_RECT = register<CopyRect>()
    @JvmStatic val BLEND = register<Blend>()
    @JvmStatic val COPY_PREDEFINED = register<CopyPredefined>()

    private inline fun <reified T : TextureModifier> register() =
        Registry.register(
            Registries.TEXTURE_MODIFIERS as Registry<TextureModifierType<T>>,
            BetterUITextures.identifier(T::class.simpleName!!.toSnakeCase()),
            TextureModifierType(),
        )!!
}

inline fun <reified T : TextureModifier> TextureModifierType() = TextureModifierType<T>(typeOf<T>())

class TextureModifierType<T : TextureModifier>(type: KType) {
    val codec: Codec<T> = codecFactory.create(type)
}

@Serializable(with = TextureModifier.Serializer::class)
interface TextureModifier {
    val type: TextureModifierType<*>

    fun apply(manager: ResourceManager, baseTexture: TextureImage)

    object Serializer :
        DispatchedCodecSerializer<TextureModifier, TextureModifierType<*>>(
            Registries.TEXTURE_MODIFIERS.codec,
            { it.type },
            { it.codec as Codec<TextureModifier> },
            TextureModifier::class.simpleName!!
        )
}

@CodecSerializable
data class RemoveColor(val rect: Rect, val color: UInt) : TextureModifier {
    @Transient override val type = TextureModifierTypes.REMOVE_COLOR

    override fun apply(manager: ResourceManager, baseTexture: TextureImage) =
        with(baseTexture) {
            for (x in 0 until rect.width) {
                for (y in 0 until rect.height) {
                    val originalColor = getFramePixel(0, x + rect.x, y + rect.y).toUInt()
                    if (originalColor == color) setFramePixel(0, x + rect.x, y + rect.y, 0)
                }
            }
        }
}

@CodecSerializable
data class RemoveRect(val rect: Rect) : TextureModifier {
    @Transient override val type = TextureModifierTypes.REMOVE_RECT

    override fun apply(manager: ResourceManager, baseTexture: TextureImage) =
        with(baseTexture) {
            for (x in 0 until rect.width) {
                for (y in 0 until rect.height) {
                    setFramePixel(0, x + rect.x, y + rect.y, 0)
                }
            }
        }
}

@CodecSerializable
data class RemoveBorder(val rect: Rect, val border: Border) : TextureModifier {
    @Transient override val type = TextureModifierTypes.REMOVE_BORDER

    override fun apply(manager: ResourceManager, baseTexture: TextureImage) =
        with(baseTexture) {
            val rightX = rect.width - border.second.width
            val bottomY = rect.height - border.second.height
            for (x in 0 until rect.width) {
                for (y in 0 until rect.height) {
                    if (x < border.first.width || x > rightX) {
                        setFramePixel(0, x + rect.x, y + rect.y, 0)
                    } else if (y < border.first.height || y > bottomY) {
                        setFramePixel(0, x + rect.x, y + rect.y, 0)
                    }
                }
            }
        }
}

@CodecSerializable
data class CopyRect(
    val sourceTexture: @Contextual Identifier,
    val fromRect: Rect,
    val targetRect: Rect
) : TextureModifier {
    @Transient override val type = TextureModifierTypes.COPY_RECT

    override fun apply(manager: ResourceManager, baseTexture: TextureImage) {
        val sourceImage = TextureImage.open(manager, sourceTexture)
        ImageTransformer.builder(
                sourceImage.imageWidth(),
                sourceImage.imageHeight(),
                baseTexture.imageWidth(),
                baseTexture.imageHeight(),
            )
            .copyRect(
                fromRect.x,
                fromRect.y,
                fromRect.width,
                fromRect.height,
                targetRect.x,
                targetRect.y,
                targetRect.width,
                targetRect.height,
            )
            .build()
            .apply(sourceImage, baseTexture)
    }
}

@CodecSerializable
data class Blend(val sourceTextures: Set<@Contextual Identifier>) : TextureModifier {
    @Transient override val type = TextureModifierTypes.BLEND

    override fun apply(manager: ResourceManager, baseTexture: TextureImage) {
        baseTexture.applyOverlay(
            *sourceTextures.map { TextureImage.open(manager, it) }.toTypedArray(),
        )
    }
}

@CodecSerializable
data class CopyPredefined(val sourceTexture: @Contextual Identifier, val targetRect: Rect) :
    TextureModifier {
    @Transient override val type = TextureModifierTypes.COPY_PREDEFINED

    override fun apply(manager: ResourceManager, baseTexture: TextureImage) {
        val sourceImage =
            PredefinedTextureLoader.predefined[PredefinedTextureLoader.wrapId(sourceTexture)]!!
                .generate(manager)
        ImageTransformer.builder(
                sourceImage.imageWidth(),
                sourceImage.imageHeight(),
                baseTexture.imageWidth(),
                baseTexture.imageHeight()
            )
            .copyRect(
                0,
                0,
                sourceImage.imageWidth(),
                sourceImage.imageHeight(),
                targetRect.x,
                targetRect.y,
                targetRect.width,
                targetRect.height
            )
            .build()
            .apply(sourceImage, baseTexture)
    }
}
