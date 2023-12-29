package settingdust.dynamictextures.client

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
import settingdust.dynamictextures.BetterUITextures
import settingdust.dynamictextures.serialization.UIntHexSerializer

object TextureModifierTypes {
    @JvmStatic val REMOVE_COLOR = register<RemoveColor>()
    @JvmStatic val REMOVE_RECT = register<RemoveRect>()
    @JvmStatic val REMOVE_BORDER = register<RemoveBorder>()
    @JvmStatic val OVERLAY = register<CopyRect>()
    @JvmStatic val BLEND = register<Overlay>()
    @JvmStatic val EXPAND_CANVAS = register<ExpandCanvas>()
    @JvmStatic val COPY_NINE_PATCH = register<CopyNinePatch>()
    @JvmStatic val GRAYSCALE = register<Grayscale>()

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

    fun apply(manager: ResourceManager, baseTexture: TextureImage): TextureImage

    object Serializer :
        DispatchedCodecSerializer<TextureModifier, TextureModifierType<*>>(
            Registries.TEXTURE_MODIFIERS.codec,
            { it.type },
            { it.codec as Codec<TextureModifier> },
            TextureModifier::class.simpleName!!
        )
}

@CodecSerializable
data class RemoveColor(
    val rect: Rect,
    val color: @Serializable(with = UIntHexSerializer::class) UInt
) : TextureModifier {
    @Transient override val type = TextureModifierTypes.REMOVE_COLOR

    override fun apply(manager: ResourceManager, baseTexture: TextureImage) =
        with(baseTexture) {
            for (x in 0 until rect.width) {
                for (y in 0 until rect.height) {
                    val originalColor = getFramePixel(0, x + rect.x, y + rect.y).toUInt()
                    if (originalColor == color) setFramePixel(0, x + rect.x, y + rect.y, 0)
                }
            }
            baseTexture
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
            baseTexture
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
                    if (x <= border.first.width || x >= rightX) {
                        setFramePixel(0, x + rect.x, y + rect.y, 0)
                    } else if (y <= border.first.height || y >= bottomY) {
                        setFramePixel(0, x + rect.x, y + rect.y, 0)
                    }
                }
            }
            baseTexture
        }
}

@CodecSerializable
data class CopyRect(
    val sourceTexture: @Contextual Identifier,
    var fromRect: Rect = Rect.INVALID,
    val targetRect: Rect,
    val repeat: Boolean = false
) : TextureModifier {
    @Transient override val type = TextureModifierTypes.OVERLAY

    override fun apply(manager: ResourceManager, baseTexture: TextureImage): TextureImage {
        val sourceImage = TextureImage.open(manager, sourceTexture)
        if (fromRect == Rect.INVALID)
            fromRect = Rect(0, 0, sourceImage.imageWidth(), sourceImage.imageHeight())
        val resized =
            TextureImage.of(
                sourceImage.image.resize(
                    fromRect,
                    Size(targetRect.width, targetRect.height),
                    repeat
                ),
                null
            )
        ImageTransformer.builder(
                targetRect.width,
                targetRect.height,
                baseTexture.imageWidth(),
                baseTexture.imageHeight(),
            )
            .copyRect(
                0,
                0,
                targetRect.width,
                targetRect.height,
                targetRect.x,
                targetRect.y,
                targetRect.width,
                targetRect.height,
            )
            .build()
            .apply(resized, baseTexture)
        return baseTexture
    }
}

@CodecSerializable
data class Overlay(
    val sourceTextures: Set<DynamicTexture>,
    /** Only blend the pixel alpha not 0 */
    val onExisting: Boolean = false,
    /** If false, source texture on base texture. If true, base texture on source texture. */
    val invert: Boolean = false
) : TextureModifier {
    @Transient override val type = TextureModifierTypes.BLEND

    override fun apply(manager: ResourceManager, baseTexture: TextureImage): TextureImage {
        var finalTexture = baseTexture
        for (sourceTexture in
            sourceTextures.map {
                var textureImage = it.targetTexture(manager)
                for (modifier in it.modifiers) textureImage = modifier.apply(manager, textureImage)
                textureImage
            }) {
            if (!invert) {
                if (onExisting) finalTexture.applyOverlayOnExisting(sourceTexture)
                else finalTexture.applyOverlay(sourceTexture)
            } else {
                if (onExisting) sourceTexture.applyOverlayOnExisting(finalTexture)
                else sourceTexture.applyOverlay(finalTexture)
                finalTexture = sourceTexture
            }
        }
        return finalTexture
    }
}

@CodecSerializable
data class ExpandCanvas(val rect: Rect) : TextureModifier {
    @Transient override val type = TextureModifierTypes.EXPAND_CANVAS

    override fun apply(manager: ResourceManager, baseTexture: TextureImage) =
        baseTexture.expandCanvas(rect)
}

@CodecSerializable
data class CopyNinePatch(
    val sourceTexture: @Contextual Identifier,
    val border: Border,
    var sourceRect: Rect = Rect.INVALID,
    var targetRect: Rect,
    val repeat: Boolean = false
) : TextureModifier {
    @Transient override val type = TextureModifierTypes.COPY_NINE_PATCH

    override fun apply(manager: ResourceManager, baseTexture: TextureImage): TextureImage {
        val sourceImage = TextureImage.open(manager, sourceTexture)
        if (sourceRect == Rect.INVALID)
            sourceRect = Rect(0, 0, sourceImage.imageWidth(), sourceImage.imageHeight())
        val extractedImage = TextureImage.createNew(sourceRect.width, sourceRect.height, null)
        ImageTransformer.builder(
                sourceImage.imageWidth(),
                sourceImage.imageHeight(),
                sourceRect.width,
                sourceRect.height
            )
            .copyRect(
                sourceRect.x,
                sourceRect.y,
                sourceRect.width,
                sourceRect.height,
                0,
                0,
                sourceRect.width,
                sourceRect.height
            )
            .build()
            .apply(sourceImage, extractedImage)
        val resized =
            extractedImage.resizeNinePatch(
                border,
                Size(targetRect.width, targetRect.height),
                repeat
            )
        ImageTransformer.builder(
                targetRect.width,
                targetRect.height,
                targetRect.width,
                targetRect.height
            )
            .copyRect(
                0,
                0,
                targetRect.width,
                targetRect.height,
                targetRect.x,
                targetRect.y,
                targetRect.width,
                targetRect.height
            )
            .build()
            .apply(resized, baseTexture)

        return baseTexture
    }
}

@CodecSerializable
data class Grayscale(
    val rect: Rect,
    val color: @Serializable(with = UIntHexSerializer::class) UInt
) : TextureModifier {
    @Transient override val type = TextureModifierTypes.GRAYSCALE

    override fun apply(manager: ResourceManager, baseTexture: TextureImage) =
        baseTexture.also { baseTexture.toGrayscale() }
}
