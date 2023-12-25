package settingdust.betteruitextures.client

import net.fabricmc.loader.api.FabricLoader
import net.mehvahdjukaar.moonlight.api.resources.pack.DynClientResourcesGenerator
import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicTexturePack
import net.mehvahdjukaar.moonlight.api.resources.textures.ImageTransformer
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourcePackProfile
import net.minecraft.util.Identifier
import settingdust.betteruitextures.BetterUITextures

data class Point(val x: Int, val y: Int)

data class Size(val width: Int, val height: Int)

data class NinePatch(val first: Point, val second: Point)

object GenericAssetsGenerator :
    DynClientResourcesGenerator(
        DynamicTexturePack(
            BetterUITextures.identifier("generic"),
            ResourcePackProfile.InsertionPosition.BOTTOM,
            false,
            false,
        ),
    ) {

    private val generators =
        DynamicAssetsGenerator::class
            .sealedSubclasses
            .map {
                it.objectInstance ?: throw IllegalStateException("Generators have to be object")
            }
            .sortedBy { it.modId }
            .filter { it.modId != null && FabricLoader.getInstance().isModLoaded(it.modId) }

    init {
        generators.map { it.modId }.forEach(dynamicPack::addNamespaces)
    }

    override fun getLogger() = BetterUITextures.logger

    override fun dependsOnLoadedPacks() = true

    override fun regenerateDynamicAssets(manager: ResourceManager) {
        StandaloneWindow.regenerateDynamicAssets(manager, dynamicPack)
        InventoryWindow.regenerateDynamicAssets(manager, dynamicPack)

        for (generator in generators) {
            generator.regenerateDynamicAssets(manager, dynamicPack)
        }
    }

    fun TextureImage.removeElementBackground(
        width: Int,
        height: Int,
        ninePatch: NinePatch,
        offsetX: Int = 0,
        offsetY: Int = 0,
        color: Int = -0x39393A,
    ) {
        val rightX = width - ninePatch.second.x
        val bottomY = height - ninePatch.second.y
        for (x in 0 until width) {
            for (y in 0 until height) {
                if (x < ninePatch.first.x || x > rightX) {
                    setFramePixel(0, x + offsetX, y + offsetY, 0)
                } else {
                    if (y < ninePatch.first.y || y > bottomY) {
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
        private val BACKGROUND = Identifier(BetterUITextures.NAMESPACE, "gui/standalone_background")
        val NINE_PATCH = NinePatch(Point(4, 17), Point(7, 6))
        private val SIZE = Size(195, 136)

        private fun generateBackground(manager: ResourceManager) =
            TextureImage.open(
                    manager,
                    Identifier(
                        "gui/container/creative_inventory/tab_items"
                    ) // HandledScreen.BACKGROUND_TEXTURE
                )
                .generateBackgroundNinePatch(NINE_PATCH, SIZE, backgroundColorPoint = Point(6, 18))

        fun generateBackground(manager: ResourceManager, windowSize: Size): TextureImage {
            val background = TextureImage.open(manager, BACKGROUND)
            if (windowSize == SIZE) return background
            return background.resizeNinePatch(NINE_PATCH, windowSize)
        }

        override fun regenerateDynamicAssets(
            manager: ResourceManager,
            dynamicPack: DynamicTexturePack
        ) {
            dynamicPack.addAndCloseTexture(BACKGROUND, generateBackground(manager), false)
        }
    }

    data object InventoryWindow : DynamicAssetsGenerator() {
        private val TOP = Identifier(BetterUITextures.NAMESPACE, "gui/inventory_top")
        private val BOTTOM = Identifier(BetterUITextures.NAMESPACE, "gui/inventory_bottom")

        const val WIDTH = 176
        const val BOTTOM_HEIGHT = 83
        const val TOP_HEIGHT = 139

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
            val NINE_PATCH = NinePatch(Point(8, 17), Point(7, 14))
            private val SIZE = Size(176, 139)

            fun generate(manager: ResourceManager) =
                TextureImage.open(manager, Identifier("gui/container/generic_54"))
                    .generateBackgroundNinePatch(NINE_PATCH, SIZE)
        }

        override fun regenerateDynamicAssets(
            manager: ResourceManager,
            dynamicPack: DynamicTexturePack
        ) {
            dynamicPack.addAndCloseTexture(BOTTOM, generateBottom(manager), false)
            dynamicPack.addAndCloseTexture(TOP, Top.generate(manager), false)
        }
    }
}
