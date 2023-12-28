package settingdust.dynamictextures.client

import com.google.gson.JsonParser
import com.mojang.serialization.JsonOps
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.fabricmc.loader.api.FabricLoader
import net.mehvahdjukaar.moonlight.api.resources.ResType
import net.mehvahdjukaar.moonlight.api.resources.pack.DynClientResourcesGenerator
import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicTexturePack
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage
import net.minecraft.resource.Resource
import net.minecraft.resource.ResourceFinder
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourcePackProfile
import net.minecraft.util.Identifier
import settingdust.dynamictextures.BetterUITextures

object GenericAssetsGenerator :
    DynClientResourcesGenerator(
        DynamicTexturePack(
            BetterUITextures.identifier("generic"),
            ResourcePackProfile.InsertionPosition.BOTTOM,
            false,
            false,
        ),
    ) {

    init {
        FabricLoader.getInstance()
            .allMods
            .map { it.metadata.id }
            .forEach { dynamicPack.addNamespaces(it) }
    }

    val textureIds = mutableSetOf<Identifier>()

    override fun getLogger() = BetterUITextures.logger

    override fun dependsOnLoadedPacks() = true

    override fun regenerateDynamicAssets(manager: ResourceManager) {
        for (id in textureIds) {
            dynamicPack.removeResource(ResType.TEXTURES.getPath(id))
        }
        textureIds.clear()

        val predefined = Object2ObjectOpenHashMap<Identifier, PredefinedTexture>()
        val predefinedCodec = codecFactory.create<PredefinedTexture>()

        for ((id, resource) in
            ResourceFinder.json("dynamic_texture/defined").findResources(manager)) {
            BetterUITextures.logger.debug("Loading {} from resource", id)
            val json = resource.reader.use { JsonParser.parseReader(it) }
            val texture = predefinedCodec.parse(JsonOps.INSTANCE, json)
            texture.error().ifPresent {
                BetterUITextures.logger.error(
                    "Loading {} from resource failed: {}",
                    id,
                    it.message(),
                )
            }
            texture.result().ifPresent {
                predefined[
                    Identifier(
                        id.namespace,
                        id.path.removePrefix("textures").removeSuffix(".json")
                    )] = it
            }
        }

        for ((id, texture) in predefined) {
            dynamicPack.addAndCloseTexture(id, texture.generate(manager), false)
        }

        val dynamicTextures = sortedMapOf<Identifier, DynamicTexture>()

        val dynamicTexturesCodec = codecFactory.create<DynamicTexture>()

        fun loadModifiers(id: Identifier?, resource: Resource) {
            BetterUITextures.logger.debug("Loading {} from resource", id)
            val json = resource.reader.use { JsonParser.parseReader(it) }
            val texture = dynamicTexturesCodec.parse(JsonOps.INSTANCE, json)
            texture.error().ifPresent {
                BetterUITextures.logger.error(
                    "Loading {} from resource failed: {}",
                    id,
                    it.message(),
                )
            }
            texture.result().ifPresent { dynamicTextures[id] = it }
        }

        for ((id, resource) in
            ResourceFinder.json("dynamic_texture/generic_modifier").findResources(manager)) {
            loadModifiers(id, resource)
        }

        for ((id, resource) in
            ResourceFinder.json("dynamic_texture/modifier").findResources(manager)) {
            loadModifiers(id, resource)
        }

        for ((id, texture) in dynamicTextures) {
            if (texture.modId != null && !FabricLoader.getInstance().isModLoaded(texture.modId))
                continue
            var textureImage = texture.targetTexture(manager)

            for (modifier in texture.modifiers) {
                try {
                    textureImage = modifier.apply(manager, textureImage)
                } catch (e: Throwable) {
                    throw IllegalStateException("$modifier run failed for $id", e)
                }
            }

            addTexture(texture.targetTexture!!, textureImage)
        }
    }

    fun addTexture(identifier: Identifier, image: TextureImage) {
        textureIds += identifier
        dynamicPack.addAndCloseTexture(identifier, image, false)
    }

    fun TextureImage.removeElementBackground(
        width: Int,
        height: Int,
        ninePatch: Border,
        offsetX: Int = 0,
        offsetY: Int = 0,
        color: Int = -0x39393A,
    ) {
        val rightX = width - ninePatch.second.width
        val bottomY = height - ninePatch.second.height
        for (x in 0 until width) {
            for (y in 0 until height) {
                if (x < ninePatch.first.width || x > rightX) {
                    setFramePixel(0, x + offsetX, y + offsetY, 0)
                } else {
                    if (y < ninePatch.first.height || y > bottomY) {
                        setFramePixel(0, x + offsetX, y + offsetY, 0)
                    } else {
                        val originalColor = getFramePixel(0, x + offsetX, y + offsetY)
                        if (originalColor == color) setFramePixel(0, x + offsetX, y + offsetY, 0)
                    }
                }
            }
        }
    }
}
