package settingdust.betteruitextures.client

import com.google.gson.JsonParser
import com.mojang.serialization.JsonOps
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.fabricmc.loader.api.FabricLoader
import net.mehvahdjukaar.moonlight.api.resources.ResType
import net.mehvahdjukaar.moonlight.api.resources.pack.DynClientResourcesGenerator
import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicTexturePack
import net.mehvahdjukaar.moonlight.api.resources.textures.ImageTransformer
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage
import net.minecraft.resource.ResourceFinder
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourcePackProfile
import net.minecraft.util.Identifier
import settingdust.betteruitextures.BetterUITextures

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

        val dynamicTextures = Object2ObjectOpenHashMap<Identifier, DynamicTexture>()

        val dynamicTexturesCodec = codecFactory.create<DynamicTexture>()
        for ((id, resource) in
            ResourceFinder.json("dynamic_texture/modifier").findResources(manager)) {
            BetterUITextures.logger.debug("Loading {} from resource", id)
            val json = resource.reader.use { JsonParser.parseReader(it) }
            val texture = dynamicTexturesCodec.parse(JsonOps.INSTANCE, json)
            texture.error().ifPresent {
                BetterUITextures.logger.error(
                    "Loading {} from resource failed: {}",
                    id,
                    it.message()
                )
            }
            texture.result().ifPresent { dynamicTextures[id] = it }
        }

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

        val book = TextureImage.open(manager, Identifier("item/book"))
        book.toGrayscale()
        val overlay = TextureImage.createNew(book.imageWidth(), book.imageHeight(), null)
        overlay.image.fillRect(
            0,
            0,
            overlay.imageWidth(),
            overlay.imageHeight(),
            0x99FFFFFF.toUInt().toInt()
        )
        book.applyOverlayOnExisting(overlay)
        dynamicPack.addAndCloseTexture(Identifier(BetterUITextures.ID, "icons/book"), book)

        for ((id, texture) in predefined) {
            dynamicPack.addAndCloseTexture(id, texture.generate(manager), false)
        }

        for ((_, texture) in dynamicTextures) {
            if (texture.modId != null && !FabricLoader.getInstance().isModLoaded(texture.modId))
                continue
            var textureImage = texture.targetTexture(manager)

            for (modifier in texture.modifiers) {
                textureImage = modifier.apply(manager, textureImage)
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

    data object StandaloneWindow : DynamicAssetsGenerator() {
        private val BACKGROUND = Identifier(BetterUITextures.ID, "gui/standalone_background")
        val NINE_PATCH = Border(Size(4, 17), Size(7, 6))
        private val SIZE = Size(195, 136)

        override fun regenerateDynamicAssets(
            manager: ResourceManager,
            dynamicPack: DynamicTexturePack
        ) {
            dynamicPack.addAndCloseTexture(BACKGROUND, generateBackground(manager), false)
        }

        private fun generateBackground(manager: ResourceManager) =
            TextureImage.open(
                    manager,
                    Identifier(
                        "gui/container/creative_inventory/tab_items"
                    ) // HandledScreen.BACKGROUND_TEXTURE
                )
                .generateBackgroundNinePatch(NINE_PATCH, SIZE, centerColorPoint = Point(6, 18))

        fun generateBackground(manager: ResourceManager, windowSize: Size): TextureImage {
            val background = TextureImage.open(manager, BACKGROUND)
            if (windowSize == SIZE) return background
            return background.resizeNinePatch(NINE_PATCH, windowSize)
        }
    }

    data object InventoryWindow : DynamicAssetsGenerator() {
        private val TOP = Identifier(BetterUITextures.ID, "gui/inventory/top")
        private val BOTTOM = Identifier(BetterUITextures.ID, "gui/inventory/bottom")

        const val WIDTH = 176
        const val BOTTOM_HEIGHT = 83
        const val TOP_HEIGHT = 139

        override fun regenerateDynamicAssets(
            manager: ResourceManager,
            dynamicPack: DynamicTexturePack
        ) {
            dynamicPack.addAndCloseTexture(BOTTOM, generateBottom(manager), false)
            dynamicPack.addAndCloseTexture(TOP, Top.generate(manager), false)
        }

        private fun generateBottom(manager: ResourceManager): TextureImage {
            val inventoryTexture =
                TextureImage.open(manager, Identifier("gui/container/generic_54"))
            val result = TextureImage.createNew(WIDTH, BOTTOM_HEIGHT, null)

            ImageTransformer.builder(256, 256, WIDTH, BOTTOM_HEIGHT)
                .apply { copyRect(0, TOP_HEIGHT, WIDTH, BOTTOM_HEIGHT, 0, 0) }
                .build()
                .apply(inventoryTexture, result)

            return result
        }

        fun removeBottom(image: TextureImage, windowSize: Size, offset: Point = Point(0, 0)) {
            val windowBottomY = windowSize.height + offset.y
            for (y in offset.y + 1..BOTTOM_HEIGHT + offset.y) {
                for (x in offset.x until windowSize.width + offset.x) {
                    image.setFramePixel(0, x, windowBottomY - y, 0)
                }
            }
        }

        fun generateBackground(manager: ResourceManager, topSize: Size): TextureImage {
            val top = TextureImage.open(manager, TOP)
            val bottom = TextureImage.open(manager, BOTTOM)
            val image =
                TextureImage.createNew(topSize.width, topSize.height + bottom.imageHeight(), null)

            top.resizeNinePatch(Top.NINE_PATCH, Size(topSize.width, topSize.height))
                .image
                .copyRect(image.image, 0, 0, 0, 0, topSize.width, topSize.height, false, false)

            bottom.image.copyRect(
                image.image,
                0,
                0,
                0,
                topSize.height,
                WIDTH,
                BOTTOM_HEIGHT,
                false,
                false
            )

            return image
        }

        private object Top {
            val NINE_PATCH = Border(Size(7, 17), Size(7, 14))
            private val SIZE = Size(176, 139)

            fun generate(manager: ResourceManager) =
                TextureImage.open(manager, Identifier("gui/container/generic_54"))
                    .generateBackgroundNinePatch(NINE_PATCH, SIZE, centerColorPoint = Point(5, 7))
        }
    }

    data object EnchantingElements : DynamicAssetsGenerator() {
        val ENTRIES_BACKGROUND =
            Identifier(BetterUITextures.ID, "gui/enchanting/entries_background")
        val ENTRY_STATUSES = Identifier(BetterUITextures.ID, "gui/enchanting/entry_statuses")

        override fun regenerateDynamicAssets(
            manager: ResourceManager,
            dynamicPack: DynamicTexturePack
        ) {
            dynamicPack.addAndCloseTexture(
                ENTRIES_BACKGROUND,
                EntriesBackground.generate(manager),
                false
            )
            dynamicPack.addAndCloseTexture(ENTRY_STATUSES, EntryStatuses.generate(manager), false)
        }

        object EntriesBackground {
            const val WIDTH = 110
            const val HEIGHT = 59
            val NINE_PATCH = Border(Size(1, 1), Size(1, 1))

            internal fun generate(manager: ResourceManager) =
                TextureImage.open(manager, Identifier("gui/container/enchanting_table"))
                    .generateBackgroundNinePatch(
                        NINE_PATCH,
                        Size(WIDTH, HEIGHT),
                        offset = Point(59, 13),
                        centerColorPoint = null
                    )
        }

        object EntryStatuses {
            const val WIDTH = 108
            const val HEIGHT = 57
            val NINE_PATCH = Border(Size(2, 2), Size(2, 2))

            internal fun generate(manager: ResourceManager) =
                TextureImage.open(manager, Identifier("gui/container/enchanting_table"))
                    .generateBackgroundNinePatch(
                        NINE_PATCH,
                        Size(WIDTH, HEIGHT),
                        offset = Point(0, 166),
                        centerColorPoint = null
                    )
        }
    }
}
