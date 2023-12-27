package settingdust.betteruitextures.client

import com.google.gson.JsonParser
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.mehvahdjukaar.moonlight.api.resources.textures.ImageTransformer
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage
import net.minecraft.registry.Registry
import net.minecraft.resource.ResourceFinder
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.SynchronousResourceReloader
import net.minecraft.util.Identifier
import net.pearx.kasechange.toSnakeCase
import org.quiltmc.qkl.library.serialization.annotation.CodecSerializable
import settingdust.betteruitextures.BetterUITextures

object PredefinedTextureLoader : IdentifiableResourceReloadListener, SynchronousResourceReloader {
    private const val TYPE = "dynamic_texture/defined"
    val predefined = Object2ObjectOpenHashMap<Identifier, PredefinedTexture>()

    override fun getFabricId() = BetterUITextures.identifier(TYPE)

    override fun reload(manager: ResourceManager) {
        predefined.clear()
        val finder = ResourceFinder.json(TYPE)
        val codec = codecFactory.create<PredefinedTexture>()

        for ((id, resource) in finder.findResources(manager)) {
            BetterUITextures.logger.debug("Loading {} from resource", id)
            val json = resource.reader.use { JsonParser.parseReader(it) }
            val texture = codec.parse(JsonOps.INSTANCE, json)
            texture.error().ifPresent {
                BetterUITextures.logger.error(
                    "Loading {} from resource failed: {}",
                    id,
                    it.message(),
                )
            }
            texture.result().ifPresent { predefined[id] = it }
        }
    }

    fun wrapId(identifier: Identifier) =
        Identifier(
            identifier.getNamespace(),
            String.format(
                "dynamic_texture/defined/%s.json",
                identifier.getPath(),
            ),
        )
}

object PredefinedTextureTypes {
    @JvmStatic val NINE_PATCH = register<NinePatch>()
    @JvmStatic val FIXED = register<Fixed>()

    private inline fun <reified T : PredefinedTexture> register() =
        Registry.register(
            Registries.PREDEFINED_TEXTURE as Registry<PredefinedTextureType<T>>,
            BetterUITextures.identifier(T::class.simpleName!!.toSnakeCase()),
            PredefinedTextureType(),
        )!!
}

inline fun <reified T : PredefinedTexture> PredefinedTextureType() =
    PredefinedTextureType<T>(typeOf<T>())

class PredefinedTextureType<T : PredefinedTexture>(type: KType) {
    val codec: Codec<T> = codecFactory.create(type)
}

@Serializable(with = PredefinedTexture.Serializer::class)
interface PredefinedTexture {
    @Transient val type: PredefinedTextureType<*>

    fun generate(manager: ResourceManager): TextureImage

    object Serializer :
        DispatchedCodecSerializer<PredefinedTexture, PredefinedTextureType<*>>(
            Registries.PREDEFINED_TEXTURE.codec,
            { it.type },
            { it.codec as Codec<PredefinedTexture> },
            PredefinedTexture::class.simpleName!!,
        )
}

interface CacheablePredefinedTexture : PredefinedTexture {
    @Transient val cache: CachedPredefinedTexture

    override fun generate(manager: ResourceManager): TextureImage {
        if (cache.texture == null) cache.texture = generateTexture(manager)
        return cache.texture!!
    }

    fun generateTexture(manager: ResourceManager): TextureImage
}

class CachedPredefinedTexture {
    var texture: TextureImage? = null
}

@CodecSerializable
data class Fixed(val source: @Contextual Identifier, val sourceRect: Rect) :
    PredefinedTexture, CacheablePredefinedTexture {
    @Transient override val cache = CachedPredefinedTexture()
    @Transient override val type = PredefinedTextureTypes.FIXED

    override fun generateTexture(manager: ResourceManager): TextureImage {
        val sourceImage = TextureImage.open(manager, source)
        val result = TextureImage.createNew(sourceRect.width, sourceRect.height, null)
        ImageTransformer.builder(
                sourceImage.imageWidth(),
                sourceImage.imageHeight(),
                sourceRect.width,
                sourceRect.height
            )
            .apply {
                copyRect(sourceRect.x, sourceRect.y, sourceRect.width, sourceRect.height, 0, 0)
            }
            .build()
            .apply(sourceImage, result)
        return result
    }
}

@CodecSerializable
data class NinePatch(
    val source: @Contextual Identifier,
    val border: Border,
    val sourceRect: Rect,
    val targetSize: Size = Size(sourceRect.width, sourceRect.height),
    val centerColorPoint: Point? = null
) : PredefinedTexture, CacheablePredefinedTexture {
    @Transient override val cache = CachedPredefinedTexture()
    @Transient override val type = PredefinedTextureTypes.NINE_PATCH

    override fun generateTexture(manager: ResourceManager): TextureImage {
        val sourceImage = TextureImage.open(manager, source)
        val originalCenterSize =
            Size(
                sourceRect.width - border.first.width - border.second.width,
                sourceRect.height - border.first.height - border.second.height,
            )
        val targetCenterSize =
            Size(
                targetSize.width - border.first.width - border.second.width,
                targetSize.height - border.first.height - border.second.height,
            )

        val targetRightX = targetSize.width - border.second.width
        val targetBottomY = targetSize.height - border.second.height
        val originalRightX = sourceRect.width - border.second.width + sourceRect.x
        val originalBottomY = sourceRect.height - border.second.height + sourceRect.y

        val result = TextureImage.createNew(targetSize.width, targetSize.height, null)

        if (centerColorPoint != null) {
            val backgroundColor =
                sourceImage.getFramePixel(
                    0,
                    sourceRect.x + centerColorPoint.x,
                    sourceRect.y + centerColorPoint.y,
                )

            for (x in border.first.width + sourceRect.x until targetRightX) {
                for (y in border.first.height + sourceRect.y until targetBottomY) {
                    result.setFramePixel(0, x, y, backgroundColor)
                }
            }
        }

        ImageTransformer.builder(
                sourceImage.imageWidth(),
                sourceImage.imageHeight(),
                targetSize.width,
                targetSize.height,
            )
            .apply {
                // Corners
                // Left Top
                copyRect(sourceRect.x, sourceRect.y, border.first.width, border.first.height, 0, 0)
                // Right Top
                copyRect(
                    originalRightX,
                    sourceRect.y,
                    border.second.width,
                    border.first.height,
                    targetRightX,
                    0,
                )
                // Left Bottom
                copyRect(
                    sourceRect.x,
                    originalBottomY,
                    border.first.width,
                    border.second.height,
                    0,
                    targetBottomY,
                )
                // Right Bottom
                copyRect(
                    originalRightX,
                    originalBottomY,
                    border.second.width,
                    border.second.height,
                    targetRightX,
                    targetBottomY,
                )

                // Edges
                // Top
                copyRect(
                    border.first.width + sourceRect.x,
                    sourceRect.y,
                    targetCenterSize.width,
                    border.first.height,
                    border.first.width,
                    0,
                )
                // Left
                copyRect(
                    sourceRect.x,
                    border.first.height + sourceRect.y,
                    border.first.width,
                    targetCenterSize.height,
                    0,
                    border.first.height,
                )
                // Right
                copyRect(
                    originalRightX,
                    border.first.height + sourceRect.y,
                    border.second.width,
                    targetCenterSize.height,
                    targetRightX,
                    border.first.height,
                )
                // Bottom
                copyRect(
                    border.first.width + sourceRect.x,
                    originalBottomY,
                    targetCenterSize.width,
                    border.second.height,
                    border.first.width,
                    targetBottomY,
                )
            }
            .build()
            .apply(sourceImage, result)

        if (centerColorPoint == null) {
            // Center
            sourceImage.image.copyRect(
                result.image,
                border.first.width + sourceRect.x,
                border.first.height + sourceRect.y,
                border.first.width,
                border.first.height,
                originalCenterSize.width,
                originalCenterSize.height,
                false,
                false,
            )
        }
        return result
    }
}
